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
package com.github.acctrans.repository.account;

import com.github.acctrans.dao.account.AccountDO;
import com.github.acctrans.dao.account.AccountDao;
import com.github.acctrans.entity.AccountEntity;
import com.github.acctrans.util.Convert;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Repository("accountRepository")
public class AccountRepositoryImpl implements AccountRepository {
    @Resource
    private AccountDao accountDao;

    @Override
    public void setSysAmount(AccountEntity accountEntity) {
        AccountDO accountDO = Convert.accountEntity2AccountDO(accountEntity);
        accountDao.updateSysAmount(accountDO);
    }

    @Override
    public void setBalanceAndSysAmount(AccountEntity accountEntity) {
        AccountDO accountDO = Convert.accountEntity2AccountDO(accountEntity);
        accountDao.updateBalanceAndSysAmount(accountDO);
    }

    @Override
    public AccountEntity getAccount(long accountId) {
        List<AccountDO> rlt = accountDao.queryAccountById(accountId);
        if (Objects.nonNull(rlt)) {
            return Convert.accountDO2AccountEntity(rlt.get(0));
        }
        return null;
    }
}