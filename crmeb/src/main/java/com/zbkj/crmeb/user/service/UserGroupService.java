package com.zbkj.crmeb.user.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.user.model.UserGroup;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * UserGroupService 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface UserGroupService extends IService<UserGroup> {

    List<UserGroup> getList(PageParamRequest pageParamRequest);

    String clean(String groupIdValue);

    String getGroupNameInId(String groupIdValue);
}