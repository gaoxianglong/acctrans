package com.github.acctrans.dao.account;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountDao {
    /**
     * 更新系统金额
     *
     * @param accountDO
     */
    void updateSysAmount(AccountDO accountDO);

    /**
     * 更新账户金额和系统金额
     *
     * @param accountDO
     */
    void updateBalanceAndSysAmount(AccountDO accountDO);

    /**
     * 根据账户id查询账户信息
     *
     * @param accountId
     * @return
     */
    List<AccountDO> queryAccountById(long accountId);
}
