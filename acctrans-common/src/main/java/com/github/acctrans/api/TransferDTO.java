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

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TransferDTO implements Serializable {
    private Long fromAccountId;
    private Long toAccountId;
    private Long tid;
    private BigDecimal amount;
    private Integer currency;
}