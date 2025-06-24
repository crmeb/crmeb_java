package com.zbkj.common.result;

/**
 * @ClassName IResultEnum
 * @Description 错误码接口 ErrCode
 * @Author HZW
 * @Date 2023/2/22 12:08
 * @Version 1.0
 */
public interface IResultEnum {

    /**
     * 响应码规则:各个模块固定4位返回码
     * 1***：登录、注册、认证模块
     * 2***：支付、提现相关
     * 3***：管理员模块
     * 4***: 商户模块
     * 5***: 订单模块
     * 6***: 商品模块
     * 7***: 系统设置模块
     * 8***: 用户模块
     * 9***：社区模块
     * 10***: 优惠券模块
     * 11***: 营销模块
     * 12***: 一号通模块
     * 13***: 微信模块
     * 14***: 会员模块
     */

    Integer getCode();

    IResultEnum setCode(Integer code);

    String getMessage();

    IResultEnum setMessage(String message);

    IResultEnum setMsgParams(Object... msgParams);

    Object[] getMsgParams();
}
