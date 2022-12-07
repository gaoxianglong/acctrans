package com.github.acctrans.repository.freeze;

import com.github.acctrans.entity.FreezeEntity;

import java.util.List;

public interface FreezeRepository {
    void addFreezeAmount(FreezeEntity freezeEntity);

    void removeFreezeAmount(FreezeEntity freezeEntity);

    List<FreezeEntity> getFreezeAmount(FreezeEntity freezeEntity);
}
