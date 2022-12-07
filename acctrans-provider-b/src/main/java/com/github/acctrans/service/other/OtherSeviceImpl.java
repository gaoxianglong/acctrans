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
package com.github.acctrans.service.other;

import com.github.acctrans.api.OtherSevice;
import com.github.acctrans.api.TransferDTO;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.hmily.annotation.HmilyTCC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DubboService(version = "1.0.0", retries = 0, loadbalance = "hmilyConsistentHash")
public class OtherSeviceImpl implements OtherSevice {
    private Logger logger = LoggerFactory.getLogger(OtherSeviceImpl.class);

    @Override
    @HmilyTCC(confirmMethod = "confirm", cancelMethod = "cancle")
    public void run() {
        logger.info("一阶段执行成功");
    }

    @Override
    public void confirm() {
        logger.info("二阶段执行成功");
    }

    @Override
    public void cancle() {
        logger.info("二阶段回滚成功");
    }
}
