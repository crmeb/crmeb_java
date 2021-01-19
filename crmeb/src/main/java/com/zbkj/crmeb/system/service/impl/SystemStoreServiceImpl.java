package com.zbkj.crmeb.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.utils.CrmebUtil;
import com.zbkj.crmeb.front.request.StoreNearRequest;
import com.zbkj.crmeb.front.response.StoreNearResponse;
import com.zbkj.crmeb.system.dao.SystemStoreDao;
import com.zbkj.crmeb.system.model.SystemStore;
import com.zbkj.crmeb.system.request.SystemStoreRequest;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.system.service.SystemStoreService;
import com.zbkj.crmeb.system.vo.SystemStoreNearVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * SystemStoreServiceImpl 接口实现
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
@Service
public class SystemStoreServiceImpl extends ServiceImpl<SystemStoreDao, SystemStore> implements SystemStoreService {

    @Resource
    private SystemStoreDao dao;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;


    /**
     * 分页显示门店
     * @param keywords 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @Override
    public List<SystemStore> getList(String keywords, int status, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<SystemStore> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(status == 1){ // 显示中
            lambdaQueryWrapper.eq(SystemStore::getIsShow, true).eq(SystemStore::getIsDel, false);
        }else if(status == 2){ // 回收站中
            lambdaQueryWrapper.eq(SystemStore::getIsDel, true);
        }else{ // 隐藏中的
            lambdaQueryWrapper.eq(SystemStore::getIsShow, false).eq(SystemStore::getIsDel, false);;
        }
        if(!StringUtils.isBlank(keywords)){
            lambdaQueryWrapper.and(i -> i.or().like(SystemStore::getName, keywords)
                    .or().like(SystemStore::getIntroduction, keywords)
                    .or().like(SystemStore::getPhone, keywords)
                    .or().like(SystemStore::getAddress, keywords)
                    .or().like(SystemStore::getDetailedAddress, keywords));
        }
        lambdaQueryWrapper.orderByDesc(SystemStore::getUpdateTime).orderByDesc(SystemStore::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据基本参数查询
     * @param systemStore 基本参数
     * @return 门店结果
     */
    @Override
    public SystemStore getByCondition(SystemStore systemStore) {
        LambdaQueryWrapper<SystemStore> lbaq = new LambdaQueryWrapper<>();
        lbaq.setEntity(systemStore);
        return dao.selectOne(lbaq);
    }

    /**
     * 修改门店显示状态
     * @param id integer id
     * @param status 状态
     * @author Mr.Zhang
     * @since 2020-04-13
     * @return bool
     */
    @Override
    public boolean updateStatus(Integer id, boolean status) {
        SystemStore systemStore = new SystemStore();
        systemStore.setId(id);
        systemStore.setIsShow(status);
        dao.updateById(systemStore);
        return true;
    }

    /**
     * 删除门店自提
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-04-13
     * @return bool
     */
    @Override
    public boolean delete(Integer id) {
        SystemStore systemStore = new SystemStore();
        systemStore.setId(id);
        systemStore.setIsDel(true);
        dao.updateById(systemStore);
        return true;
    }

    /**
     * 数量
     * @author Mr.Zhang
     * @since 2020-04-13
     * @return HashMap<String, Integer>
     */
    @Override
    public HashMap<String, Integer> getCount() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("show", getCountByStatus(1));
        map.put("hide", getCountByStatus(0));
        map.put("recycle", getCountByStatus(2));
        return map;
    }

    /**
     * 根据状态获取总数
     * @author Mr.Zhang
     * @since 2020-04-13
     * @return HashMap<String, Integer>
     */
    private Integer getCountByStatus(int status) {
        LambdaQueryWrapper<SystemStore> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(status == 2){
            lambdaQueryWrapper.eq(SystemStore::getIsDel, true);
        }else{
            lambdaQueryWrapper.eq(SystemStore::getIsShow, status).eq(SystemStore::getIsDel, false);
        }
        lambdaQueryWrapper.orderByDesc(SystemStore::getUpdateTime).orderByDesc(SystemStore::getId);
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 根据id集合查询数据，返回 map
     * @param storeIdList List<Integer> id集合
     * @author Mr.Zhang
     * @since 2020-04-17
     * @return HashMap<Integer, SystemStore>
     */
    @Override
    public HashMap<Integer, SystemStore> getMapInId(List<Integer> storeIdList){
        HashMap<Integer, SystemStore> map = new HashMap<>();
        if(storeIdList.size() < 1){
            return map;
        }
        LambdaQueryWrapper<SystemStore> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SystemStore::getId, storeIdList);
        List<SystemStore> systemStoreList = dao.selectList(lambdaQueryWrapper);
        if(systemStoreList.size() < 1){
            return map;
        }
        for (SystemStore systemStore : systemStoreList) {
            map.put(systemStore.getId(), systemStore);
        }
        return map;
    }

    /**
     * 附近的提货点
     * @param request StoreNearRequest 经纬度参数
     * @param pageParamRequest PageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-06-01
     * @return StoreNearResponse
     */
    @Override
    public StoreNearResponse getNearList(StoreNearRequest request, PageParamRequest pageParamRequest) {
        StoreNearResponse storeNearResponse = new StoreNearResponse();
        storeNearResponse.setTengXunMapKey(systemConfigService.getValueByKey(Constants.CONFIG_SITE_TENG_XUN_MAP_KEY));

        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        List<SystemStoreNearVo> storeNearVoArrayList = new ArrayList<>();

        if(StringUtils.isNotBlank(request.getLatitude()) && StringUtils.isNotBlank(request.getLongitude())){
            storeNearVoArrayList = dao.getNearList(request);
        }else{
            List<SystemStore> list = getList(null, 1, pageParamRequest);
            for (SystemStore systemStore : list) {
                SystemStoreNearVo systemStoreNearVo = new SystemStoreNearVo();
                BeanUtils.copyProperties(systemStore, systemStoreNearVo);
                storeNearVoArrayList.add(systemStoreNearVo);
            }
        }

        storeNearResponse.setList(storeNearVoArrayList);
        return storeNearResponse;
    }

    /**
     * 新增门店自提
     * @param request SystemStoreRequest 新增参数
     * @author Mr.Zhang
     * @since 2020-04-13
     * @return boolean
     */
    @Override
    public boolean create(SystemStoreRequest request) {
        SystemStore systemStore = new SystemStore();
        BeanUtils.copyProperties(request, systemStore);
        clearPrefix(systemStore);
        splitLat(systemStore);
        return save(systemStore);
    }

    /**
     * 修改门店自提
     * @param id integer id
     * @param request 修改参数
     * @author Mr.Zhang
     * @since 2020-04-13
     * @return boolean
     */
    @Override
    public boolean update(Integer id, SystemStoreRequest request) {
        SystemStore systemStore = new SystemStore();
        BeanUtils.copyProperties(request, systemStore);
        systemStore.setId(id);
        splitLat(systemStore);
        clearPrefix(systemStore);
        return updateById(systemStore);
    }

    /**
     * 彻底删除
     * @param id 提货点编号
     * @return Boolean
     */
    @Override
    public Boolean completeLyDelete(Integer id) {
        SystemStore systemStore = getById(id);
        if (ObjectUtil.isNull(systemStore)) throw new CrmebException("提货点不存在!");
        int delete = dao.deleteById(id);
        return delete > 0;
    }

    /**
     * 提货点恢复
     * @param id 提货点编号
     * @return Boolean
     */
    @Override
    public Boolean recovery(Integer id) {
        SystemStore systemStore = getById(id);
        if (ObjectUtil.isNull(systemStore)) throw new CrmebException("提货点不存在!");
        if (!systemStore.getIsDel()) return Boolean.TRUE;
        systemStore.setIsDel(false);
        return updateById(systemStore);
    }

    /**
     * 去掉图片前缀
     * @param systemStore SystemStore 新增参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    private void clearPrefix(SystemStore systemStore) {
        systemStore.setImage(systemAttachmentService.clearPrefix(systemStore.getImage()));
    }

    /**
     * 分解经纬度
     * @param systemStore SystemStore 新增参数
     * @author Mr.Zhang
     * @since 2020-04-13
     */
    private void splitLat(SystemStore systemStore) {
        if(!StringUtils.isBlank(systemStore.getLatitude())){
            List<String> list = CrmebUtil.stringToArrayStr(systemStore.getLatitude());
            systemStore.setLatitude(list.get(0));
            systemStore.setLongitude(list.get(1));
        }
    }

}

