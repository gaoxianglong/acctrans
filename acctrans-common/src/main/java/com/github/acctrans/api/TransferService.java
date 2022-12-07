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
package com.github.acctrans.api;

import org.dromara.hmily.annotation.Hmily;

public interface TransferService {
    /**
     * 资金流出记账接口
     *
     * @param transferDTO
     */
    @Hmily
    void transferOut(TransferDTO transferDTO);

    void confirm(TransferDTO transferDTO);

    void cancle(TransferDTO transferDTO);
}
