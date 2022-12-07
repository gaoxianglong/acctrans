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
package com.github.acctrans.util;

import com.github.acctrans.api.TransferDTO;
import com.github.acctrans.dao.account.AccountDO;
import com.github.acctrans.dao.freeze.FreezeDO;
import com.github.acctrans.entity.AccountEntity;
import com.github.acctrans.entity.FreezeEntity;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Convert {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static AccountEntity transferDTO2AccountEntity(TransferDTO transferDTO) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAccountId(transferDTO.getFromAccountId());
        accountEntity.setUpdatetime(System.currentTimeMillis());
        accountEntity.setSysAmount(transferDTO.getAmount());
        return accountEntity;
    }

    public static AccountDO accountEntity2AccountDO(AccountEntity accountEntity) {
        AccountDO accountDO = new AccountDO();
        accountDO.setAccountId(accountEntity.getAccountId());
        accountDO.setBalance(Objects.nonNull(accountEntity.getBalance()) ? accountEntity.getBalance().toString() : null);
        accountDO.setSysAmount(Objects.nonNull(accountEntity.getSysAmount()) ? accountEntity.getSysAmount().toString() : null);
        accountDO.setFreezeAmount(Objects.nonNull(accountEntity.getFreezeAmount()) ? accountEntity.getFreezeAmount().toString() : null);
        accountDO.setCreateTime(Objects.nonNull(accountEntity.getCreateTime()) ? format.format(new Date(accountEntity.getCreateTime())) : null);
        accountDO.setUpdateTime(Objects.nonNull(accountEntity.getUpdatetime()) ? format.format(new Date(accountEntity.getUpdatetime())) : null);
        return accountDO;
    }

    public static AccountEntity accountDO2AccountEntity(AccountDO accountDO) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setAccountId(accountDO.getAccountId());
        accountEntity.setBalance(new BigDecimal(accountDO.getBalance()));
        accountEntity.setFreezeAmount(new BigDecimal(accountDO.getFreezeAmount()));
        accountEntity.setSysAmount(new BigDecimal(accountDO.getSysAmount()));
        accountEntity.setAccountType(accountDO.getAccountType());
        try {
            if (Objects.nonNull(accountDO.getCreateTime())) {
                accountEntity.setCreateTime(format.parse(accountDO.getCreateTime()).getTime());
            }
            if (Objects.nonNull(accountDO.getUpdateTime())) {
                accountEntity.setUpdatetime(format.parse(accountDO.getUpdateTime()).getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return accountEntity;
    }

    public static FreezeDO freezeEntity2FreezeDO(FreezeEntity freezeEntity) {
        FreezeDO freezeDO = new FreezeDO();
        freezeDO.setAccountId(freezeEntity.getAccountId());
        freezeDO.setType(freezeEntity.getType());
        freezeDO.setCurrency(freezeEntity.getCurrency());
        if (Objects.nonNull(freezeEntity.getAmount())) {
            freezeDO.setAmount(freezeEntity.getAmount().toString());
        }
        freezeDO.setTid(freezeEntity.getTid());
        return freezeDO;
    }

    public static FreezeEntity freezeDO2FreezeEntity(FreezeDO freezeDO) {
        FreezeEntity freezeEntity = new FreezeEntity();
        freezeEntity.setAccountId(freezeDO.getAccountId());
        freezeEntity.setAmount(new BigDecimal(freezeDO.getAmount()));
        freezeEntity.setCurrency(freezeDO.getCurrency());
        freezeEntity.setTid(freezeDO.getTid());
        freezeEntity.setType(freezeDO.getType());
        return freezeEntity;
    }
}
