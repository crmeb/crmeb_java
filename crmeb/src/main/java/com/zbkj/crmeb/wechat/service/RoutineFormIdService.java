package com.zbkj.crmeb.wechat.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.wechat.model.RoutineFormId;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.wechat.request.RoutineFormIdSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @Description RoutineFormIdService 接口
* @since 2020-04-18
*/
public interface RoutineFormIdService extends IService<RoutineFormId> {

    List<RoutineFormId> getList(RoutineFormIdSearchRequest request, PageParamRequest pageParamRequest);
}