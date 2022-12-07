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
package com.github.acctrans.repository.global;

import com.github.acctrans.dao.global.GlobalDao;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository("globalRepository")
public class GlobalRepositoryImpl implements GlobalRepository {
    @Resource
    private GlobalDao globalDao;

    @Override
    public void addTryLog(Long tid) {
        globalDao.insertTryLog(tid);
    }

    @Override
    public void addConfirmLog(Long tid) {
        globalDao.insertConfirmLog(tid);
    }

    @Override
    public void addCancleLog(Long tid) {
        globalDao.insertCancleLog(tid);
    }

    @Override
    public Integer getTryLog(Long tid) {
        return globalDao.queryTryLog(tid);
    }

    @Override
    public Integer getConfirmLog(Long tid) {
        return globalDao.queryConfirmLog(tid);
    }

    @Override
    public Integer getCancleLog(Long tid) {
        return globalDao.queryCancleLog(tid);
    }
}