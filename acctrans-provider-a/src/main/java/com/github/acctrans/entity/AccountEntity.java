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
package com.github.acctrans.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountEntity {
    /**
     * 账户ID
     */
    private Long accountId;
    /**
     * 账户余额
     */
    private BigDecimal balance;
    /**
     * 冻结金额
     */
    private BigDecimal freezeAmount;
    /**
     * 系统金额
     */
    private BigDecimal sysAmount;
    /**
     * 账户状态:0正常,1异常
     */
    private Integer accountType;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updatetime;
}
