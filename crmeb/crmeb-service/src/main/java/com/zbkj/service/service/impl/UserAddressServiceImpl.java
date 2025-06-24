package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.SystemCity;
import com.zbkj.common.model.user.UserAddress;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.UserAddressRequest;
import com.zbkj.service.dao.UserAddressDao;
import com.zbkj.service.service.SystemCityService;
import com.zbkj.service.service.UserAddressService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * UserAddressServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressDao, UserAddress> implements UserAddressService {

    @Resource
    private UserAddressDao dao;

    @Autowired
    private SystemCityService systemCityService;

    @Autowired
    private UserService userService;

    /**
     * 列表
     *
     * @return List<UserAddress>
     */
    @Override
    public List<UserAddress> getList(PageParamRequest pageParamRequest) {
        Integer UserId = userService.getUserIdException();
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserAddress> lqw = Wrappers.lambdaQuery();
        lqw.select(UserAddress::getId, UserAddress::getRealName, UserAddress::getPhone, UserAddress::getProvince,
                UserAddress::getCity, UserAddress::getDistrict, UserAddress::getDetail, UserAddress::getIsDefault);
        lqw.eq(UserAddress::getUid, UserId);
        lqw.eq(UserAddress::getIsDel, false);
        lqw.orderByDesc(UserAddress::getIsDefault);
        lqw.orderByDesc(UserAddress::getId);
        return dao.selectList(lqw);
    }

    /**
     * 创建地址
     *
     * @param request UserAddressRequest 参数
     * @return List<UserAddress>
     */
    @Override
    public UserAddress create(UserAddressRequest request) {
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(request, userAddress);
        userAddress.setCity(request.getAddress().getCity());
        userAddress.setCityId(request.getAddress().getCityId());
        userAddress.setDistrict(request.getAddress().getDistrict());
        userAddress.setProvince(request.getAddress().getProvince());

        // 添加地址时cityId和城市名称不能同时为空，如果id为空，必须用城市名称自查后set CityId
        if (request.getAddress().getCityId() == 0 && StrUtil.isBlank(request.getAddress().getDistrict())) {
            throw new CrmebException("请选择正确城市数据");
        }
        if (StrUtil.isNotBlank(request.getAddress().getCity()) && request.getAddress().getCityId() == 0) {
            SystemCity systemCity = systemCityService.getCityByCityName(request.getAddress().getCity());
            if (ObjectUtil.isNull(systemCity)) {
                throw new CrmebException("当前城市未找到");
            }
            SystemCity currentCity = systemCityService.getByAreaNameAndPid(request.getAddress().getDistrict(), systemCity.getCityId());
            if (ObjectUtil.isNull(currentCity)) throw new CrmebException("当前城市区域未找到！");
            userAddress.setCityId(currentCity.getCityId());
        }

        if (request.getAddress().getCityId() > 0 && StrUtil.isNotBlank(request.getAddress().getDistrict())) {
            checkCity(userAddress.getCityId());
        }
        userAddress.setUid(userService.getUserIdException());
        if (userAddress.getIsDefault()) {
            //把当前用户其他默认地址取消
            cancelDefault(userAddress.getUid());
        }
        saveOrUpdate(userAddress);
        return userAddress;
    }

    /**
     * 设置默认
     *
     * @param id Integer id
     * @return UserAddress
     */
    @Override
    public Boolean def(Integer id) {
        //把当前用户其他默认地址取消
        cancelDefault(userService.getUserIdException());
        UserAddress userAddress = new UserAddress();
        userAddress.setId(id);
        userAddress.setUid(userService.getUserIdException());
        userAddress.setIsDefault(true);
        userAddress.setUpdateTime(DateUtil.date());
        return updateById(userAddress);
    }

    /**
     * 删除
     *
     * @param id Integer id
     * @return UserAddress
     */
    @Override
    public Boolean delete(Integer id) {
        //把当前用户其他默认地址取消
        LambdaQueryWrapper<UserAddress> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserAddress::getId, id);
        lambdaQueryWrapper.eq(UserAddress::getUid, userService.getUserIdException());
        dao.delete(lambdaQueryWrapper);
        return true;
    }

    /**
     * 获取默认地址
     *
     * @return UserAddress
     */
    @Override
    public UserAddress getDefault() {
        //把当前用户其他默认地址取消
        LambdaQueryWrapper<UserAddress> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserAddress::getIsDefault, true);
        lambdaQueryWrapper.eq(UserAddress::getUid, userService.getUserId());
        lambdaQueryWrapper.eq(UserAddress::getIsDel, false);
        return dao.selectOne(lambdaQueryWrapper);
    }

    @Override
    public UserAddress getById(Integer addressId) {
        LambdaQueryWrapper<UserAddress> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserAddress::getId, addressId);
        lambdaQueryWrapper.eq(UserAddress::getIsDel, false);
        return dao.selectOne(lambdaQueryWrapper);
    }

    /**
     * 获取地址详情
     *
     * @param id 地址id
     * @return UserAddress
     */
    @Override
    public UserAddress getDetail(Integer id) {
        Integer UserId = userService.getUserIdException();
        LambdaQueryWrapper<UserAddress> lqw = Wrappers.lambdaQuery();
        lqw.select(UserAddress::getId, UserAddress::getRealName, UserAddress::getPhone, UserAddress::getProvince,
                UserAddress::getCity, UserAddress::getDistrict, UserAddress::getDetail, UserAddress::getIsDefault);
        lqw.eq(UserAddress::getId, id);
        lqw.eq(UserAddress::getUid, UserId);
        lqw.eq(UserAddress::getIsDel, false);
        return dao.selectOne(lqw);
    }

    /**
     * 获取默认地址
     *
     * @return UserAddress
     */
    @Override
    public UserAddress getDefaultByUid(Integer uid) {
        LambdaQueryWrapper<UserAddress> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(UserAddress::getIsDefault, true);
        lambdaQueryWrapper.eq(UserAddress::getUid, uid);
        return dao.selectOne(lambdaQueryWrapper);
    }

    /**
     * 检测城市id是否合法
     *
     * @param cityId Integer 城市id
     */
    private void checkCity(Integer cityId) {
        //检测城市Id是否存在
        SystemCity systemCity = systemCityService.getByAreaId(cityId);
        if (ObjectUtil.isNull(systemCity)) {
            throw new CrmebException("请选择正确的城市");
        }
    }

    /**
     * 取消默认地址
     *
     * @param userId Integer 城市id
     */
    private void cancelDefault(Integer userId) {
        //检测城市Id是否存在
        UserAddress userAddress = new UserAddress();
        userAddress.setIsDefault(false);
        LambdaQueryWrapper<UserAddress> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserAddress::getUid, userId);
        update(userAddress, lambdaQueryWrapper);
    }

}

