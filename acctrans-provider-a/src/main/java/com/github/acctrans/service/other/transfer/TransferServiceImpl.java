/*
 * Copyright 2019-2119 gao_xianglong@sina.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.acctrans.service.other.transfer;

import com.github.acctrans.api.OtherSevice;
import com.github.acctrans.api.TransferDTO;
import com.github.acctrans.api.TransferService;
import com.github.acctrans.entity.AccountEntity;
import com.github.acctrans.entity.FreezeEntity;
import com.github.acctrans.repository.account.AccountRepository;
import com.github.acctrans.repository.freeze.FreezeRepository;
import com.github.acctrans.repository.global.GlobalRepository;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.hmily.annotation.HmilyTCC;
import org.dromara.hmily.core.context.HmilyContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@DubboService(version = "1.0.0", retries = 0, loadbalance = "hmilyConsistentHash")
public class TransferServiceImpl implements TransferService {
    private Logger logger = LoggerFactory.getLogger(TransferServiceImpl.class);

    @Resource
    private AccountRepository accountRepository;
    @Resource
    private FreezeRepository freezeRepository;
    @Resource
    private GlobalRepository globalRepository;
    @DubboReference(version = "1.0.0")
    private OtherSevice otherSevice;

    @Override
    @HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancle")
    @Transactional(rollbackFor = Throwable.class)
    public void transferOut(TransferDTO transferDTO) {
        logger.info("{} to {},amount:{},currency:{}",
                transferDTO.getFromAccountId(), transferDTO.getToAccountId(),
                transferDTO.getAmount(), transferDTO.getCurrency());
        Long tid = HmilyContextHolder.get().getTransId();
        transferDTO.setTid(tid);
        logger.info("tid:{}", tid);
        // 幂等处理
        if (globalRepository.getTryLog(tid) > 0) {
            return;
        }
        // 悬挂处理
        if (globalRepository.getConfirmLog(tid) > 0 || globalRepository.getCancleLog(tid) > 0) {
            return;
        }
        AccountEntity from = accountRepository.getAccount(transferDTO.getFromAccountId());
        // 余额检查
        if (from.getBalance().subtract(from.getSysAmount()).compareTo(transferDTO.getAmount()) == -1) {
            throw new RuntimeException(String.format("%s账户余额不足", transferDTO.getFromAccountId()));
        }
        // 账户状态检查,是否存在限制交易
        if (from.getAccountType() == 1) {
            throw new RuntimeException(String.format("%s状态异常", from.getAccountId()));
        }
        AccountEntity to = accountRepository.getAccount(transferDTO.getToAccountId());
        if (to.getAccountType() == 1) {
            throw new RuntimeException(String.format("%s状态异常", to.getAccountId()));
        }
        // 冻结资金
        from.setSysAmount(from.getSysAmount().add(transferDTO.getAmount()));
        accountRepository.setSysAmount(from);
        // 写入冻结表
        freezeRepository.addFreezeAmount(new FreezeEntity() {{
            setAccountId(transferDTO.getFromAccountId());
            setTid(tid);
            setAmount(transferDTO.getAmount());
            // 类型1为付款
            setType(1);
            setCurrency(transferDTO.getCurrency());
        }});
        freezeRepository.addFreezeAmount(new FreezeEntity() {{
            setAccountId(transferDTO.getToAccountId());
            setTid(tid);
            setAmount(transferDTO.getAmount());
            // 类型1为收款
            setType(2);
            setCurrency(transferDTO.getCurrency());
        }});
        // 添加分支事务记录
        globalRepository.addTryLog(tid);
        // TODO 省去记录账务明细
        logger.info("记账一阶段处理成功");
        otherSevice.run();
    }

    @Override
    public void confirm(TransferDTO transferDTO) {
        logger.info("confirm");
        Long tid = transferDTO.getTid();
        // 获取from冻结资金信息
        List<FreezeEntity> fromFreezeEntitys = freezeRepository.getFreezeAmount(new FreezeEntity() {{
            setAccountId(transferDTO.getFromAccountId());
            setTid(tid);
        }});
        BigDecimal fromAmount = BigDecimal.ZERO;
        for (FreezeEntity from : fromFreezeEntitys) {
            if (from.getType() == 1) {
                fromAmount = fromAmount.add(from.getAmount());
            }
        }
        // 删除from冻结资金记录
        freezeRepository.removeFreezeAmount(fromFreezeEntitys.get(0));

        // 获取to冻结资金信息
        List<FreezeEntity> toFreezeEntitys = freezeRepository.getFreezeAmount(new FreezeEntity() {{
            setAccountId(transferDTO.getToAccountId());
            setTid(tid);
        }});
        BigDecimal toAmount = BigDecimal.ZERO;
        for (FreezeEntity to : toFreezeEntitys) {
            if (to.getType() == 2) {
                toAmount = toAmount.add(to.getAmount());
            }
        }
        // 删除to冻结资金记录
        freezeRepository.removeFreezeAmount(toFreezeEntitys.get(0));

        // 变更from余额
        AccountEntity from = accountRepository.getAccount(transferDTO.getFromAccountId());
        from.setBalance(from.getBalance().subtract(fromAmount));
        from.setSysAmount(from.getSysAmount().subtract(fromAmount));
        accountRepository.setBalanceAndSysAmount(from);
        // 变更to余额
        AccountEntity to = accountRepository.getAccount(transferDTO.getToAccountId());
        to.setBalance(to.getBalance().add(toAmount));
        accountRepository.setBalanceAndSysAmount(to);
        // 添加分支事务记录
        globalRepository.addConfirmLog(tid);
        logger.info("记账二阶段处理成功");
    }

    @Override
    public void cancle(TransferDTO transferDTO) {
        logger.info("cancle");
        Long tid = transferDTO.getTid();
        // 空回滚处理
        if (globalRepository.getTryLog(tid) == 0) {
            logger.warn("{}:空回滚", tid);
            return;
        }
        // 幂等处理
        if (globalRepository.getCancleLog(tid) > 0) {
            return;
        }
        // 还原from的冻结资金
        AccountEntity accountEntity = accountRepository.getAccount(transferDTO.getFromAccountId());
        accountEntity.setSysAmount(accountEntity.getSysAmount().subtract(transferDTO.getAmount()));
        accountRepository.setSysAmount(accountEntity);
        // 还原from余额
        logger.warn("记账二阶段回滚成功");
    }
}