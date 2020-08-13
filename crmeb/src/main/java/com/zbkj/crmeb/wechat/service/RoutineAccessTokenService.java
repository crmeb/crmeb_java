package com.zbkj.crmeb.wechat.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.wechat.model.RoutineAccessToken;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.wechat.request.RoutineAccessTokenSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @Description RoutineAccessTokenService 接口
* @since 2020-04-18
*/
public interface RoutineAccessTokenService extends IService<RoutineAccessToken> {

    List<RoutineAccessToken> getList(RoutineAccessTokenSearchRequest request, PageParamRequest pageParamRequest);
}