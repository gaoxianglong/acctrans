package com.github.acctrans.dao.global;

import org.springframework.stereotype.Repository;

@Repository
public interface GlobalDao {
    void insertTryLog(Long tid);

    void insertConfirmLog(Long tid);

    void insertCancleLog(Long tid);

    Integer queryTryLog(Long tid);

    Integer queryConfirmLog(Long tid);

    Integer queryCancleLog(Long tid);
}
