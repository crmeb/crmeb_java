package com.zbkj.crmeb.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.common.PageParamRequest;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;

import com.utils.ValidateFormUtil;
import com.zbkj.crmeb.system.model.SystemConfig;
import com.zbkj.crmeb.system.model.SystemFormTemp;
import com.zbkj.crmeb.system.dao.SystemFormTempDao;
import com.zbkj.crmeb.system.request.SystemFormCheckRequest;
import com.zbkj.crmeb.system.request.SystemFormItemCheckRequest;
import com.zbkj.crmeb.system.request.SystemFormTempSearchRequest;
import com.zbkj.crmeb.system.service.SystemFormTempService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.crmeb.system.vo.SystemConfigFormItemConfigRegListVo;
import com.zbkj.crmeb.system.vo.SystemConfigFormItemVo;
import com.zbkj.crmeb.system.vo.SystemConfigFormVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * SystemFormTempServiceImpl 接口实现
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
public class SystemFormTempServiceImpl extends ServiceImpl<SystemFormTempDao, SystemFormTemp> implements SystemFormTempService {

    @Resource
    private SystemFormTempDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-05-15
    * @return List<SystemFormTemp>
    */
    @Override
    public List<SystemFormTemp> getList(SystemFormTempSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 SystemFormTemp 类的多条件查询
        LambdaQueryWrapper<SystemFormTemp> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isBlank(request.getKeywords())){
            lambdaQueryWrapper.eq(SystemFormTemp::getId, request.getKeywords()).
                    or().like(SystemFormTemp::getName, request.getKeywords()).
                    or().like(SystemFormTemp::getInfo, request.getKeywords());
        }
        lambdaQueryWrapper.orderByDesc(SystemFormTemp::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 验证item规则
     * @param systemFormCheckRequest SystemFormCheckRequest 表单数据提交
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    @Override
    public void checkForm(SystemFormCheckRequest systemFormCheckRequest){
        //循环取出item数据， 组合成 key => val 的map格式
        HashMap<String, String> map = new HashMap<>();
        for (SystemFormItemCheckRequest systemFormItemCheckRequest : systemFormCheckRequest.getFields()) {
            map.put(systemFormItemCheckRequest.getName(), systemFormItemCheckRequest.getValue());
        }

        //取出表单模型的数据
        SystemFormTemp formTemp = getById(systemFormCheckRequest.getId());


        //解析表单规则进行验证
        SystemConfigFormVo systemConfigFormVo;
        try{
            systemConfigFormVo =  JSONObject.parseObject(formTemp.getContent(), SystemConfigFormVo.class);
        }catch (Exception e){
            throw new CrmebException("模板表单 【" + formTemp.getName() + "】 的内容不是正确的JSON格式！");
        }

        SystemConfigFormItemVo systemConfigFormItemVo;

        for (String item : systemConfigFormVo.getFields()) {

            systemConfigFormItemVo = JSONObject.parseObject(item, SystemConfigFormItemVo.class);
            String model = systemConfigFormItemVo.get__vModel__(); //字段 name

//            if(!map.containsKey(model)){
//                throw new CrmebException("模板表单 【" + formTemp.getName() + "】缺少 " + model + "字段的相关信息！");
//            }

            if(systemConfigFormItemVo.get__config__().getRequired() && map.get(model).equals("")){
                throw new CrmebException(systemConfigFormItemVo.get__config__().getLabel() + "不能为空！");
            }

            //正则验证
            checkRule(systemConfigFormItemVo.get__config__().getRegList(), map.get(model),  systemConfigFormItemVo.get__config__().getLabel());
        }
    }

    /**
     * 验证item规则
     * @param regList List<SystemConfigFormItemConfigRegListVo 正则表达式列表
     * @param value String 验证的值
     * @param name String 提示语字段名称
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    private void checkRule(List<SystemConfigFormItemConfigRegListVo> regList, String value, String name){
        if(regList.size() > 0){
            for (SystemConfigFormItemConfigRegListVo systemConfigFormItemConfigRegListVo : regList) {
                if(!ValidateFormUtil.regular(value, name, systemConfigFormItemConfigRegListVo.getPattern())){
                    throw new CrmebException(systemConfigFormItemConfigRegListVo.getMessage());
                }
            }
        }
    }
}

