package com.zbkj.crmeb.express.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.constants.OnePassConstants;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.utils.OnePassUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.express.dao.ExpressDao;
import com.zbkj.crmeb.express.model.Express;
import com.zbkj.crmeb.express.request.ExpressSearchRequest;
import com.zbkj.crmeb.express.request.ExpressUpdateRequest;
import com.zbkj.crmeb.express.request.ExpressUpdateShowRequest;
import com.zbkj.crmeb.express.service.ExpressService;
import com.zbkj.crmeb.express.vo.ExpressSheetVo;
import com.zbkj.crmeb.system.service.SystemConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * ExpressServiceImpl 接口实现
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
@Service
public class ExpressServiceImpl extends ServiceImpl<ExpressDao, Express> implements ExpressService {

    @Resource
    private ExpressDao dao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OnePassUtil onePassUtil;

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 分页显示快递公司表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhang
     * @since 2020-04-17
     */
    @Override
    public List<Express> getList(ExpressSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Express> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StrUtil.isNotBlank(request.getKeywords())){
            lambdaQueryWrapper.like(Express::getCode, request.getKeywords()).or().like(Express::getName, request.getKeywords());
        }

        lambdaQueryWrapper.orderByDesc(Express::getSort, Express::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /** 详情
     * @param id Integer 订单id
     * @author Mr.Zhang
     * @since 2020-06-10
     * @return Express
     */
    @Override
    public Express info(Integer id) {
        Express info = getById(id);
        if(null == info){
            throw new CrmebException("没有找到快递公司信息");
        }
        return info;
    }

    /**
     * 编辑
     */
    @Override
    public Boolean updateExpress(ExpressUpdateRequest expressRequest) {
        Express temp = getById(expressRequest.getId());
        if (ObjectUtil.isNull(temp)) throw new CrmebException("编辑的记录不存在!");

        if (StrUtil.isBlank(expressRequest.getAccount()) && temp.getPartnerId().equals(true)) {
            throw new CrmebException("请输入月结账号");
        }
        if (StrUtil.isBlank(expressRequest.getPassword()) && temp.getPartnerKey().equals(true)) {
            throw new CrmebException("请输入月结密码");
        }
        if (StrUtil.isBlank(expressRequest.getNetName()) && temp.getNet().equals(true)) {
            throw new CrmebException("请输入取件网点");
        }
        Express express = new Express();
        BeanUtils.copyProperties(expressRequest, express);
        return updateById(express);
    }

    /**
     * 修改显示状态
     */
    @Override
    public Boolean updateExpressShow(ExpressUpdateShowRequest expressRequest) {
        Express temp = getById(expressRequest.getId());
        if (ObjectUtil.isNull(temp)) throw new CrmebException("编辑的记录不存在!");
        if (temp.getIsShow().equals(expressRequest.getIsShow())) {
            return Boolean.TRUE;
        }
        Express express = new Express();
        BeanUtils.copyProperties(expressRequest, express);
        return updateById(express);
    }

    /**
     * 同步物流公司
     */
    @Override
    public Boolean syncExpress() {
        if (redisUtil.exists(OnePassConstants.ONE_PASS_EXPRESS_CACHE_KEY)) {
            return Boolean.TRUE;
        }
        getExpressList();

        redisUtil.set(OnePassConstants.ONE_PASS_EXPRESS_CACHE_KEY, 1, 3600L, TimeUnit.SECONDS);
        return Boolean.TRUE;
    }

    /**
     * 查询全部物流公司
     * @param type 类型：normal-普通，elec-电子面单
     */
    @Override
    public List<Express> findAll(String type) {
        LambdaQueryWrapper<Express> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Express::getIsShow, true);
        if (type.equals("elec")) {
            lqw.eq(Express::getStatus, true);
        }
        lqw.orderByDesc(Express::getSort, Express::getId);
        return dao.selectList(lqw);
    }

    /**
     * 查询物流公司面单模板
     * @param com 快递公司编号
     */
    @Override
    public JSONObject template(String com) {
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("com", com);
        return onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_EXPRESS_TEMP_URI, param, header);
    }

    /**
     * 查询快递公司
     * @param code 快递公司编号
     */
    @Override
    public Express getByCode(String code) {
        LambdaQueryWrapper<Express> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Express::getCode, code);
        return dao.selectOne(lqw);
    }

    /**
     * 打印电子面单
     * @param cargo 物品名称
     * @param count 商品数量
     *              TODO
     */
    @Override
    public Boolean dump(String cargo, Integer count) {
        // 获取系统保存的电子面单信息
        ExpressSheetVo expressSheetVo = systemConfigService.getExpressSheet();
        Express express = getById(expressSheetVo.getExportId());
        if (ObjectUtil.isNull(express)) {
            throw new CrmebException("电子面单中对应的快递公司不存在");
        }
        return false;
    }

    /**
     * 从平台获取物流公司
     * 并存入数据库
     */
    private void getExpressList() {
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("type", 1);// 国内运输商
        param.add("page", 0);
        param.add("limit", 1000);

        JSONObject post = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_EXPRESS_URI, param, header);
        System.out.println("OnePass Express ALL post = " + post);
        JSONObject jsonObject = post.getJSONObject("data");
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        if (CollUtil.isEmpty(jsonArray)) return;

        List<Express> expressList = CollUtil.newArrayList();
        List<String> codeList = getAllCode();
        jsonArray.forEach(temp -> {
            JSONObject object = (JSONObject) temp;
            if (StrUtil.isBlank(object.getString("code")) || !codeList.contains(object.getString("code"))) {
                Express express = new Express();
                express.setName(Optional.ofNullable(object.getString("name")).orElse(""));
                express.setCode(Optional.ofNullable(object.getString("code")).orElse(""));
                express.setPartnerId(false);
                express.setPartnerKey(false);
                express.setNet(false);
                if (ObjectUtil.isNotNull(object.getInteger("partner_id"))) {
                    express.setPartnerId(object.getInteger("partner_id") == 1);
                }
                if (ObjectUtil.isNotNull(object.getInteger("partner_key"))) {
                    express.setPartnerKey(object.getInteger("partner_key") == 1);
                }
                if (ObjectUtil.isNotNull(object.getInteger("net"))) {
                    express.setNet(object.getInteger("net") == 1);
                }
                express.setIsShow(true);
                express.setStatus(false);
                if (!express.getPartnerId() && !express.getPartnerKey() && !express.getNet()) {
                    express.setStatus(true);
                }
                expressList.add(express);
            }
        });

        if (CollUtil.isNotEmpty(expressList)) {
            boolean saveBatch = saveBatch(expressList);
            if (!saveBatch) throw new CrmebException("同步物流公司失败");
        }
    }

    /**
     * 获取所有物流公司code
     */
    private List<String> getAllCode() {
        LambdaQueryWrapper<Express> lqw = new LambdaQueryWrapper<>();
        lqw.select(Express::getCode);
        List<Express> expressList = dao.selectList(lqw);
        if (CollUtil.isEmpty(expressList)) {
            return CollUtil.newArrayList();
        }
        return expressList.stream().map(Express::getCode).collect(Collectors.toList());
    }

    public static String st = "mnc7Yay0RsvF70LWX7i6k";
    public static String sk = "￥bugJEjOmF01hxGr~qj5";
}

