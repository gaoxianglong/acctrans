package com.github.acctrans.dao.freeze;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreezeDao {
    /**
     * 根据账户id+事务id插入冻结金额信息
     *
     * @param freezeDO
     */
    void insertFreezeAmount(FreezeDO freezeDO);

    /**
     * 根据账户id+事务id删除冻结记录
     *
     * @param freezeDO
     */
    void deleteFreezeAmount(FreezeDO freezeDO);

    List<FreezeDO> queryFreezeAmount(FreezeDO freezeDO);
}
