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
        // ????????????
        if (globalRepository.getTryLog(tid) > 0) {
            return;
        }
        // ????????????
        if (globalRepository.getConfirmLog(tid) > 0 || globalRepository.getCancleLog(tid) > 0) {
            return;
        }
        AccountEntity from = accountRepository.getAccount(transferDTO.getFromAccountId());
        AccountEntity to = accountRepository.getAccount(transferDTO.getToAccountId());
        // ????????????
        if (from.getBalance().subtract(from.getSysAmount()).compareTo(transferDTO.getAmount()) == -1) {
            throw new RuntimeException(String.format("%s??????????????????", transferDTO.getFromAccountId()));
        }
        // ??????????????????,????????????????????????
        if (from.getAccountType() == 1) {
            throw new RuntimeException(String.format("%s????????????", from.getAccountId()));
        }
        if (to.getAccountType() == 1) {
            throw new RuntimeException(String.format("%s????????????", to.getAccountId()));
        }
        // ????????????
        from.setSysAmount(from.getSysAmount().add(transferDTO.getAmount()));
        accountRepository.setSysAmount(from);
        // ???????????????
        freezeRepository.addFreezeAmount(new FreezeEntity() {{
            setAccountId(transferDTO.getFromAccountId());
            setTid(tid);
            setAmount(transferDTO.getAmount());
            // ??????1?????????
            setType(1);
            setCurrency(transferDTO.getCurrency());
        }});
        freezeRepository.addFreezeAmount(new FreezeEntity() {{
            setAccountId(transferDTO.getToAccountId());
            setTid(tid);
            setAmount(transferDTO.getAmount());
            // ??????2?????????
            setType(2);
            setCurrency(transferDTO.getCurrency());
        }});
        // ????????????????????????
        globalRepository.addTryLog(tid);
        // TODO ????????????????????????
        logger.info("???????????????????????????");
        // ??????????????????
        otherSevice.run();
    }

    @Override
    public void confirm(TransferDTO transferDTO) {
        logger.info("confirm");
        Long tid = transferDTO.getTid();
        AccountEntity from = accountRepository.getAccount(transferDTO.getFromAccountId());
        AccountEntity to = accountRepository.getAccount(transferDTO.getToAccountId());

        // ??????from??????????????????
        List<FreezeEntity> fromFreezeEntitys = freezeRepository.getFreezeAmount(new FreezeEntity() {{
            setAccountId(transferDTO.getFromAccountId());
            setTid(tid);
        }});
        BigDecimal fromAmount = BigDecimal.ZERO;
        for (FreezeEntity fromFreezeEntity : fromFreezeEntitys) {
            if (fromFreezeEntity.getType() == 1) {
                fromAmount = fromAmount.add(fromFreezeEntity.getAmount());
            }
        }
        // ??????from??????????????????
        freezeRepository.removeFreezeAmount(fromFreezeEntitys.get(0));

        // ??????to??????????????????
        List<FreezeEntity> toFreezeEntitys = freezeRepository.getFreezeAmount(new FreezeEntity() {{
            setAccountId(transferDTO.getToAccountId());
            setTid(tid);
        }});
        BigDecimal toAmount = BigDecimal.ZERO;
        for (FreezeEntity toFreezeEntity : toFreezeEntitys) {
            if (toFreezeEntity.getType() == 2) {
                toAmount = toAmount.add(toFreezeEntity.getAmount());
            }
        }
        // ??????to??????????????????
        freezeRepository.removeFreezeAmount(toFreezeEntitys.get(0));

        // ??????from??????
        from.setBalance(from.getBalance().subtract(fromAmount));
        from.setSysAmount(from.getSysAmount().subtract(fromAmount));
        accountRepository.setBalanceAndSysAmount(from);
        // ??????to??????
        to.setBalance(to.getBalance().add(toAmount));
        accountRepository.setBalanceAndSysAmount(to);
        // ????????????????????????
        globalRepository.addConfirmLog(tid);
        logger.info("???????????????????????????");
    }

    @Override
    public void cancle(TransferDTO transferDTO) {
        logger.info("cancle");
        Long tid = transferDTO.getTid();
        // ???????????????
        if (globalRepository.getTryLog(tid) == 0) {
            logger.warn("{}:?????????", tid);
            return;
        }
        // ????????????
        if (globalRepository.getCancleLog(tid) > 0) {
            return;
        }
        // ??????from???????????????
        AccountEntity accountEntity = accountRepository.getAccount(transferDTO.getFromAccountId());
        accountEntity.setSysAmount(accountEntity.getSysAmount().subtract(transferDTO.getAmount()));
        accountRepository.setSysAmount(accountEntity);
        // ??????from??????????????????
        freezeRepository.removeFreezeAmount(new FreezeEntity() {{
            setAccountId(transferDTO.getFromAccountId());
            setTid(tid);
        }});
        // ??????to??????????????????
        freezeRepository.removeFreezeAmount(new FreezeEntity() {{
            setAccountId(transferDTO.getToAccountId());
            setTid(tid);
        }});
        logger.warn("???????????????????????????");
        // ????????????????????????
        globalRepository.addCancleLog(tid);
    }
}