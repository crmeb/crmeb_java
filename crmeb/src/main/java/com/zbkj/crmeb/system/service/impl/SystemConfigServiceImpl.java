package com.zbkj.crmeb.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.zbkj.crmeb.system.model.SystemConfig;
import com.zbkj.crmeb.system.dao.SystemConfigDao;
import com.zbkj.crmeb.system.request.SystemFormCheckRequest;
import com.zbkj.crmeb.system.request.SystemFormItemCheckRequest;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.crmeb.system.service.SystemFormTempService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
* @author Mr.Zhang
* @Description SystemConfigServiceImpl 接口实现
* @since 2020-04-13
*/
@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigDao, SystemConfig> implements SystemConfigService {

    @Resource
    private SystemConfigDao dao;

    @Autowired
    private SystemFormTempService systemFormTempService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 搜索列表
     * @param pageParamRequest PageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return String
     */
    @Override
    public List<SystemConfig> getList(PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<SystemConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(
                SystemConfig::getId,
                SystemConfig::getTitle,
                SystemConfig::getName,
                SystemConfig::getValue,
                SystemConfig::getStatus

        );
        lambdaQueryWrapper.eq(SystemConfig::getStatus, false);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据menu name 获取 value
     * @param name menu name
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return String
     */
    @Override
    public String getValueByKey(String name) {
        LambdaQueryWrapper<SystemConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(SystemConfig::getValue).eq(SystemConfig::getName, name);
        lambdaQueryWrapper.eq(SystemConfig::getStatus, false);

        SystemConfig systemConfig = dao.selectOne(lambdaQueryWrapper);
        if(StringUtils.isBlank(systemConfig.getValue())){
            systemConfig.setValue(null);
        }
        return systemConfig.getValue();
    }

    /**
     * 根据key更新值
     * @param key key
     * @return 更新结果
     */
    @Override
    public boolean setValueByKey(String key, String value) {
        LambdaQueryWrapper<SystemConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemConfig::getValue, value);
        lambdaQueryWrapper.eq(SystemConfig::getStatus, false);
        SystemConfig systemConfig = dao.selectOne(lambdaQueryWrapper);
        systemConfig.setValue(value);
        LambdaUpdateWrapper<SystemConfig> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(SystemConfig::getValue, value);
        lambdaUpdateWrapper.eq(SystemConfig::getName, key);
        return dao.update(systemConfig, lambdaUpdateWrapper) > 0;
    }

    /**
     * 同时获取多个配置
     * @param keys 多个配置key
     * @return 查询到的数据集合
     */
    @Override
    public List<String> getValuesByKes(List<String> keys) {
        List<String> result = new ArrayList<>();
        for (String key : keys) {
            result.add(getValueByKey(key));
        }
        return result;
    }

    /**
     * 根据 name 获取 value 找不到抛异常
     * @param name menu name
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return String
     */
    @Override
    public String getValueByKeyException(String name) {
        LambdaQueryWrapper<SystemConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(SystemConfig::getValue, SystemConfig::getTitle).
                eq(SystemConfig::getStatus, false).
                eq(SystemConfig::getName, name);

        SystemConfig systemConfig = dao.selectOne(lambdaQueryWrapper);
        if(StringUtils.isBlank(systemConfig.getValue())){
            throw new CrmebException("配置项 " + systemConfig.getTitle() + " 没有配置， 请配置！");
        }
        return systemConfig.getValue();
    }

    /**
     * 整体保存表单数据
     * @param systemFormCheckRequest SystemFormCheckRequest 数据保存
     * @author Mr.Zhang
     * @since 2020-04-13
     * @return bool
     */
    @Override
    public boolean saveForm(SystemFormCheckRequest systemFormCheckRequest) {
        //检测form表单，并且返回需要添加的数据
        systemFormTempService.checkForm(systemFormCheckRequest);

        //修改之前的数据
        updateStatusByFormId(systemFormCheckRequest.getId());
        List<SystemConfig> systemConfigList = new ArrayList<>();
        //批量添加
        for (SystemFormItemCheckRequest systemFormItemCheckRequest : systemFormCheckRequest.getFields()) {
            SystemConfig systemConfig = new SystemConfig();
            systemConfig.setName(systemFormItemCheckRequest.getName());
            systemConfig.setValue(systemAttachmentService.clearPrefix(systemFormItemCheckRequest.getValue()));
            systemConfig.setFormId(systemFormCheckRequest.getId());
            systemConfig.setTitle(systemFormItemCheckRequest.getTitle());
            systemConfigList.add(systemConfig);
        }
        saveBatch(systemConfigList);

        //删除之前隐藏的数据
        deleteStatusByFormId(systemFormCheckRequest.getId());

        return true;
    }



    /**
     * updateStatusByGroupId
     * @param formId Integer formId
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    private void updateStatusByFormId(Integer formId){
        LambdaQueryWrapper<SystemConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemConfig::getFormId, formId).eq(SystemConfig::getStatus, false);

        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setStatus(true);
        update(systemConfig, lambdaQueryWrapper);
    }

    /**
     * deleteStatusByGroupId
     * @param formId Integer formId
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    private void deleteStatusByFormId(Integer formId){
        LambdaQueryWrapper<SystemConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //删除已经隐藏的数据
        lambdaQueryWrapper.eq(SystemConfig::getFormId, formId).eq(SystemConfig::getStatus, true);
        dao.delete(lambdaQueryWrapper);
    }

    /**
     * 根据menu name更新 value
     * @param name menu name
     * @param value 值
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return JSONObject
     */
    @Override
    public boolean updateValueByName(String name, String value){
        value = systemAttachmentService.clearPrefix(value);

        LambdaQueryWrapper<SystemConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemConfig::getName, name).eq(SystemConfig::getValue, value);
        SystemConfig systemConfig = dao.selectOne(lambdaQueryWrapper);
        if(systemConfig != null){
            systemConfig.setValue(systemAttachmentService.clearPrefix(value));
            updateById(systemConfig);
            return true;
        }

        return false;
    }

    /**
     * 保存或更新配置数据
     * @param name 菜单名称
     * @param value 菜单值
     * @return 更新或者保存的状态
     */
    @Override
    public boolean updateOrSaveValueByName(String name, String value) {
        value = systemAttachmentService.clearPrefix(value);

        LambdaQueryWrapper<SystemConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemConfig::getName, name);
        List<SystemConfig> systemConfigs = dao.selectList(lambdaQueryWrapper);
        if(systemConfigs.size() >= 2){
            throw new CrmebException("配置名称存在多个请检查配置 eb_system_config 重复数据："+name+"条数："+systemConfigs.size());
        }else if(systemConfigs.size() == 1){
            systemConfigs.get(0).setValue(value);
            updateById(systemConfigs.get(0));
            return true;
        }else {
            save(new SystemConfig().setName(name).setValue(value));
            return true;
        }
    }

    /**
     * 根据name查询数据
     * @param id Integer id
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return JSONObject
     */
    private SystemConfig getVoByIdException(Integer id){
        LambdaQueryWrapper<SystemConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemConfig::getStatus, false).eq(SystemConfig::getId, id);
        SystemConfig systemConfig = dao.selectOne(lambdaQueryWrapper);
        if(systemConfig == null){
            throw new CrmebException("没有找到相对应的数据！");
        }

        return systemConfig;
    }

    /**
     * 根据id查询数据
     * @param formId Integer id
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return JSONObject
     */
    @Override
    public HashMap<String, String> info(Integer formId){
        LambdaQueryWrapper<SystemConfig> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(SystemConfig::getStatus, false).eq(SystemConfig::getFormId, formId);
        List<SystemConfig> systemConfigList = dao.selectList(lambdaQueryWrapper1);
        if(systemConfigList.size() < 1){
            return null;
        }
        HashMap<String, String> map = new HashMap<>();
        for (SystemConfig systemConfig : systemConfigList) {
            map.put(systemConfig.getName(), systemConfig.getValue());
        }
        map.put("id", formId.toString());
        return map;
    }

    /**
     * 根据name查询数据
     * @param name name
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return JSONObject
     */
    @Override
    public boolean checkName(String name){
        LambdaQueryWrapper<SystemConfig> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemConfig::getName, name);
        SystemConfig systemConfig = dao.selectOne(lambdaQueryWrapper);
        return systemConfig == null;
    }
}

