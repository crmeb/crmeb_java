package com.zbkj.crmeb.system.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemNotice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.system.request.SystemNoticeRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @Description SystemNoticeService 接口
* @since 2020-04-13
*/
public interface SystemNoticeService extends IService<SystemNotice> {
    List<SystemNotice> getList(SystemNoticeRequest request, PageParamRequest pageParamRequest);
}