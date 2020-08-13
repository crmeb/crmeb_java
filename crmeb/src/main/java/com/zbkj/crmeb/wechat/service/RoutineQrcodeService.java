package com.zbkj.crmeb.wechat.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.wechat.model.RoutineQrcode;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.wechat.request.RoutineQrcodeSearchRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @Description RoutineQrcodeService 接口
* @since 2020-04-18
*/
public interface RoutineQrcodeService extends IService<RoutineQrcode> {

    List<RoutineQrcode> getList(RoutineQrcodeSearchRequest request, PageParamRequest pageParamRequest);
}