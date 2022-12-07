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
package com.github.acctrans.repository.freeze;

import com.github.acctrans.dao.freeze.FreezeDO;
import com.github.acctrans.dao.freeze.FreezeDao;
import com.github.acctrans.entity.FreezeEntity;
import com.github.acctrans.util.Convert;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Repository("freezeRepository")
public class FreezeRepositoryImpl implements FreezeRepository {
    @Resource
    private FreezeDao freezeDao;

    @Override
    public void addFreezeAmount(FreezeEntity freezeEntity) {
        FreezeDO freezeDO = Convert.freezeEntity2FreezeDO(freezeEntity);
        freezeDao.insertFreezeAmount(freezeDO);
    }

    @Override
    public void removeFreezeAmount(FreezeEntity freezeEntity) {
        FreezeDO freezeDO = Convert.freezeEntity2FreezeDO(freezeEntity);
        freezeDao.deleteFreezeAmount(freezeDO);
    }

    @Override
    public List<FreezeEntity> getFreezeAmount(FreezeEntity freezeEntity) {
        List<FreezeEntity> rlt = new ArrayList<>();
        for (FreezeDO freezeDO : freezeDao.queryFreezeAmount(Convert.freezeEntity2FreezeDO(freezeEntity))) {
            rlt.add(Convert.freezeDO2FreezeEntity(freezeDO));
        }
        return rlt;
    }
}