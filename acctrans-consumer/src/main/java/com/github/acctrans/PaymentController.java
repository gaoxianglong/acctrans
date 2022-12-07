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
package com.github.acctrans;

import com.github.acctrans.api.TransferDTO;
import com.github.acctrans.api.TransferService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping(value = "/acctrans")
public class PaymentController {
    @DubboReference(version = "1.0.0")
    private TransferService transferService;

    //@HmilyTCC
    @GetMapping(value = "/pay")
    public String pay() {
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setFromAccountId(1001L);
        transferDTO.setCurrency(1);
        transferDTO.setAmount(new BigDecimal("50.00"));
        transferDTO.setToAccountId(1002L);
        transferService.transferOut(transferDTO);
        return "success";
    }
}
