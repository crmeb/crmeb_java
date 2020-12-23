package com.zbkj.crmeb.user.service;

import com.common.PageParamRequest;
import com.zbkj.crmeb.user.model.UserTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * UserTagService 接口实现
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
public interface UserTagService extends IService<UserTag> {

    List<UserTag> getList(PageParamRequest pageParamRequest);

    String clean(String tagIdValue);

    String getGroupNameInId(String tagIdValue);
}