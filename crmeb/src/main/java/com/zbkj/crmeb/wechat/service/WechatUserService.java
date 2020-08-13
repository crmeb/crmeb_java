package com.zbkj.crmeb.wechat.service;

/**
 * <p>
 * 微信用户表 服务类
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-11
 */
public interface WechatUserService{
    void push(String userId, Integer newsId);
}
