package com.zbkj.crmeb.wechatMessage.service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * <p>
 * 用户中心 服务类
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-10
 */
public interface WeChatMessageService{
    String init(HttpServletRequest request);
}
