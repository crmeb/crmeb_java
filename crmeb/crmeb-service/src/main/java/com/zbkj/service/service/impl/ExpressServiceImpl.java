package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.constants.OnePassConstants;
import com.zbkj.common.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.model.express.Express;
import com.zbkj.common.request.ExpressSearchRequest;
import com.zbkj.common.request.ExpressUpdateRequest;
import com.zbkj.common.request.ExpressUpdateShowRequest;
import com.zbkj.service.dao.ExpressDao;
import com.zbkj.service.service.ExpressService;
import com.zbkj.service.util.OnePassUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ExpressServiceImpl 接口实现
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
*/
@Service
public class ExpressServiceImpl extends ServiceImpl<ExpressDao, Express> implements ExpressService {

    private static final int EXPRESS_SYNC_PAGE_LIMIT = 100;
    private static final int EXPRESS_SYNC_MAX_PAGE = 200;

    @Resource
    private ExpressDao dao;

    @Autowired
    private OnePassUtil onePassUtil;

    /**
     * 分页显示快递公司表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @Override
    public List<Express> getList(ExpressSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Express> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(request.getKeywords())) {
            lambdaQueryWrapper.like(Express::getCode, request.getKeywords()).or().like(Express::getName, request.getKeywords());
        }
        // 排序：sort字段倒序，正常id正序，方便展示常用物流公司
        lambdaQueryWrapper.orderByDesc(Express::getSort);
        lambdaQueryWrapper.orderByAsc(Express::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 编辑
     */
    @Override
    public Boolean updateExpress(ExpressUpdateRequest expressRequest) {
        Express temp = getById(expressRequest.getId());
        if (ObjectUtil.isNull(temp)) throw new CrmebException("快递公司不存在!");

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
        getExpressList();
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
        lqw.orderByDesc(Express::getSort);
        lqw.orderByAsc(Express::getId);
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
        return onePassUtil.getData(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_EXPRESS_TEMP_URI
                +"?com="+com+"&is_shipment=1", header);
    }

    @Override
    public JSONObject templateFor(String com, String type, String is_shipment, String page, String limit) {
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        return onePassUtil.getData(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_EXPRESS_DUMP_RECORD_URI +"?com="+com+"&type="+type+"&is_shipment="+is_shipment+"&page="+page+"&limit="+limit, header);
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
     * 通过物流公司名称获取
     * @param name 物流公司名称
     */
    @Override
    public Express getByName(String name) {
        LambdaQueryWrapper<Express> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Express::getName, name);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取快递公司详情
     * @param id 快递公司id
     */
    @Override
    public Express getInfo(Integer id) {
        Express express = getById(id);
        if (ObjectUtil.isNull(express)) {
            throw new CrmebException("快递公司不存在");
        }
        return express;
    }

    /**
     * 从平台获取物流公司
     * 并存入数据库
     */
    private void getExpressList() {
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        List<Express> expressList = CollUtil.newArrayList();
        Set<String> codeSet = new HashSet<>(getAllCode());

        for (int page = 1; page <= EXPRESS_SYNC_MAX_PAGE; page++) {
            JSONObject jsonObject = getExpressPage(page, EXPRESS_SYNC_PAGE_LIMIT, header);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if (CollUtil.isEmpty(jsonArray)) {
                break;
            }

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String code = object.getString("code");
                if (StrUtil.isNotBlank(code) && !codeSet.contains(code)) {
                    Express express = new Express();
                    express.setName(Optional.ofNullable(object.getString("name")).orElse(""));
                    express.setCode(code);
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
                    codeSet.add(code);
                }
            }

            Integer total = jsonObject.getInteger("total");
            if (jsonArray.size() < EXPRESS_SYNC_PAGE_LIMIT || (ObjectUtil.isNotNull(total) && page * EXPRESS_SYNC_PAGE_LIMIT >= total)) {
                break;
            }
        }

        if (CollUtil.isNotEmpty(expressList)) {
            boolean saveBatch = saveBatch(expressList, EXPRESS_SYNC_PAGE_LIMIT);
            if (!saveBatch) throw new CrmebException("同步物流公司失败");
        }
    }

    private JSONObject getExpressPage(Integer page, Integer limit, HashMap<String, String> header) {
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        //        param.add("type", 1);// 快递类型：1，国内运输商；2，国际运输商；3，国际邮政 不传获取全部
        param.add("page", page);
        param.add("limit", limit);

        JSONObject post = onePassUtil.getFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_EXPRESS_URI, param, header);
        JSONObject jsonObject = post.getJSONObject("data");
        if (ObjectUtil.isNull(jsonObject) || ObjectUtil.isNull(jsonObject.getJSONArray("data"))) {
            throw new CrmebException("同步物流公司失败：一号通返回数据格式异常");
        }
        return jsonObject;
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
}
