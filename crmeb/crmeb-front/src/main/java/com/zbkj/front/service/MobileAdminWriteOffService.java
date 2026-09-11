package com.zbkj.front.service;

import com.zbkj.common.response.StoreOrderVerificationConfirmResponse;

/**
 * 移动端商家核销服务
 */
public interface MobileAdminWriteOffService {

    StoreOrderVerificationConfirmResponse confirm(String verifyCode);

    Boolean writeOff(String verifyCode);
}
