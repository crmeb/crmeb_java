package com.zbkj.service.service;

import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.model.user.UserTag;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.request.UserTagRequest;

import java.util.List;

/**
 * UserTagService 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public interface UserTagService extends IService<UserTag> {

    /**
     * 用户标签列表
     * @param pageParamRequest 分页参数
     * @return List
     */
    List<UserTag> getList(PageParamRequest pageParamRequest);

    String getGroupNameInId(String tagIdValue);

    /**
     * 新增用户标签
     * @param userTagRequest 标签参数
     */
    Boolean create(UserTagRequest userTagRequest);

    /**
     * 删除用户标签
     * @param id 标签id
     */
    Boolean delete(Integer id);

    /**
     * 修改用户标签
     * @param id 标签id
     * @param userTagRequest 标签参数
     */
    Boolean updateTag(Integer id, UserTagRequest userTagRequest);
}
