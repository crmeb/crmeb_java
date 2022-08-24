package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.model.user.UserAddress;
import com.zbkj.common.request.UserAddressRequest;

import java.util.List;

/**
 * UserAddressService 接口实现
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
public interface UserAddressService extends IService<UserAddress> {

    /**
     * 用户地址列表
     * @param pageParamRequest 分页参数
     * @return List<UserAddress>
     */
    List<UserAddress> getList(PageParamRequest pageParamRequest);

    /**
     * 添加用户地址
     * @param request 地址请求参数
     * @return UserAddress
     */
    UserAddress create(UserAddressRequest request);

    /**
     * 设置默认地址
     * @param id 地址id
     * @return Boolean
     */
    Boolean def(Integer id);

    /**
     * 删除用户地址
     * @param id 地址id
     * @return Boolean
     */
    Boolean delete(Integer id);

    UserAddress getDefault();

    UserAddress getById(Integer addressId);

    /**
     * 获取地址详情
     * @param id 地址id
     * @return UserAddress
     */
    UserAddress getDetail(Integer id);

    /**
     * 获取默认地址
     * @return UserAddress
     */
    UserAddress getDefaultByUid(Integer uid);
}
