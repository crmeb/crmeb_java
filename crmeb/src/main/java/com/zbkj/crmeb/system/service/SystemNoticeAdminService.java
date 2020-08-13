package com.zbkj.crmeb.system.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.system.model.SystemNoticeAdmin;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.crmeb.system.request.SystemNoticeAdminRequest;

import java.util.List;

/**
* @author Mr.Zhang
* @Description SystemNoticeAdminService 接口
* @since 2020-04-13
*/
public interface SystemNoticeAdminService extends IService<SystemNoticeAdmin> {
    List<SystemNoticeAdmin> getList(SystemNoticeAdminRequest request, PageParamRequest pageParamRequest);
}