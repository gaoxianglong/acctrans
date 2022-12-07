package com.github.acctrans.repository.global;

public interface GlobalRepository {
    void addTryLog(Long tid);

    void addConfirmLog(Long tid);

    void addCancleLog(Long tid);

    Integer getTryLog(Long tid);

    Integer getConfirmLog(Long tid);

    Integer getCancleLog(Long tid);
}