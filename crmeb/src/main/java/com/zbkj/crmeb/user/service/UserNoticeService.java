package com.zbkj.crmeb.user.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.user.model.UserNotice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.user.request.UserNoticeSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @Description UserNoticeService 接口
* @since 2020-04-28
*/
public interface UserNoticeService extends IService<UserNotice> {

    List<UserNotice> getList(UserNoticeSearchRequest request, PageParamRequest pageParamRequest);
}