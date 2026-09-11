package com.zbkj.front.service;


import com.zbkj.common.request.IosBindingPhoneRequest;
import com.zbkj.common.request.IosLoginRequest;
import com.zbkj.common.request.WxBindingPhoneRequest;
import com.zbkj.common.response.LoginResponse;

/**
 * IOS服务类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface IosService {

    /**
     * ios登录
     * @param loginRequest 登录请求对象
     */
    LoginResponse login(IosLoginRequest loginRequest);

    /**
     * IOS绑定手机号
     * @param request 绑定请求对象
     * @return 登录信息
     */
    LoginResponse registerBindingPhone(WxBindingPhoneRequest request);

    /**
     * ios绑定手机号（登录后）
     * @param request 请求对象
     * @return 是否绑定
     */
    Boolean bindingPhone(IosBindingPhoneRequest request);
}
