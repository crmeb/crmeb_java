package com.zbkj.crmeb.validatecode.service;

import com.zbkj.crmeb.validatecode.model.ValidateCode;

import java.util.HashMap;

/**
* @author Mr.Zhang
* @Description ValidateCodeService 接口
* @since 2020-04-16
*/
public interface ValidateCodeService {

    ValidateCode get();

    boolean check(ValidateCode validateCode);
}