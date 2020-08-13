package com.zbkj.crmeb.user.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.user.model.UserGroup;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Mr.Zhang
* @Description UserGroupService 接口
* @since 2020-04-28
*/
public interface UserGroupService extends IService<UserGroup> {

    List<UserGroup> getList(PageParamRequest pageParamRequest);

    String clean(String groupIdValue);

    String getGroupNameInId(String groupIdValue);
}