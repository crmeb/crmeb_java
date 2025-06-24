package com.zbkj.service.service;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;

import javax.servlet.http.HttpServletRequest;


/**
 * 类的详细说明
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/6/12
 */
public interface SafetyService {

    /**
     * 获取行为验证码
     */
    ResponseModel getSafetyCode(CaptchaVO data, HttpServletRequest request);

    /**
     * 验证行为验证码
     */
    ResponseModel checkSafetyCode(CaptchaVO data, HttpServletRequest request);

    /**
     * 行为验证码二次校验
     */
    ResponseModel verifySafetyCode(CaptchaVO data);
}
