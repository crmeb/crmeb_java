package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.vo.ShopAuditBrandRequestItemDataVo;
import com.zbkj.common.vo.ShopAuditBrandRequestItemVo;
import com.zbkj.common.vo.ShopAuditBrandRequestVo;
import com.zbkj.common.vo.ShopAuditBrandResponseVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.wechat.video.PayComponentShopBrand;
import com.zbkj.service.dao.PayComponentShopBrandDao;
import com.zbkj.service.service.PayComponentShopBrandService;
import com.zbkj.service.service.WechatVideoBeforeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * PayComponentShopBrandServiceImpl 接口实现
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
public class PayComponentShopBrandServiceImpl extends ServiceImpl<PayComponentShopBrandDao, PayComponentShopBrand> implements PayComponentShopBrandService {

    @Resource
    private PayComponentShopBrandDao dao;

    @Autowired
    private WechatVideoBeforeService wechatVideoBeforeService;

    /**
     * 上传品牌
     * @param request 上传品牌请求参数
     * @return 审核单id
     */
    @Override
    public String auditBrand(ShopAuditBrandRequestVo request) {
        // 参数校验

        // 组件上传品牌
        ShopAuditBrandResponseVo brandResponseVo = wechatVideoBeforeService.shopAuditBrand(request);

        // 数据库存储
        PayComponentShopBrand shopBrand = new PayComponentShopBrand();
        ShopAuditBrandRequestItemVo auditReq = request.getAuditReq();
        ShopAuditBrandRequestItemDataVo brandInfo = auditReq.getBrandInfo();
        BeanUtils.copyProperties(brandInfo, shopBrand);
        shopBrand.setAuditId(brandResponseVo.getAuditId());
        shopBrand.setLicense(auditReq.getLicense());
        shopBrand.setStatus(0);
        shopBrand.setCreateTime(DateUtil.date());
        save(shopBrand);
        return brandResponseVo.getAuditId();
    }

    /**
     * 获取品牌详情（根据审核单号）
     * @param auditId 审核单号
     * @return 品牌详情
     */
    @Override
    public PayComponentShopBrand getByAuditId(String auditId) {
        LambdaQueryWrapper<PayComponentShopBrand> lqw = Wrappers.lambdaQuery();
        lqw.eq(PayComponentShopBrand::getAuditId, auditId);
        lqw.orderByDesc(PayComponentShopBrand::getId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取品牌列表
     * @param pageParamRequest 分页参数
     * @param status 审核状态, 0：审核中，1：审核成功，9：审核拒绝
     * @return 品牌列表
     */
    @Override
    public PageInfo<PayComponentShopBrand> findList(PageParamRequest pageParamRequest, Integer status) {
        Page<PayComponentShopBrand> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<PayComponentShopBrand> lqw = Wrappers.lambdaQuery();
        if (ObjectUtil.isNotNull(status)) {
            lqw.eq(PayComponentShopBrand::getStatus, status);
        }
        lqw.orderByDesc(PayComponentShopBrand::getId);
        List<PayComponentShopBrand> list = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, list);
    }

    /**
     * 获取品牌列表（可用）
     * @return 品牌列表
     */
    @Override
    public List<PayComponentShopBrand> getUsableList() {
        LambdaQueryWrapper<PayComponentShopBrand> lqw = Wrappers.lambdaQuery();
        lqw.select(PayComponentShopBrand::getId, PayComponentShopBrand::getBrandId, PayComponentShopBrand::getBrandWording);
        lqw.eq(PayComponentShopBrand::getStatus, 1);
        lqw.orderByDesc(PayComponentShopBrand::getId);
        return dao.selectList(lqw);
    }
}

