package com.github.acctrans.repository.account;

import com.github.acctrans.entity.AccountEntity;

public interface AccountRepository {
    void setSysAmount(AccountEntity accountEntity);

    void setBalanceAndSysAmount(AccountEntity accountEntity);

    AccountEntity getAccount(long accountId);
}
