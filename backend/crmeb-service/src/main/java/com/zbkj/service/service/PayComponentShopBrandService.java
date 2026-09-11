package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.vo.ShopAuditBrandRequestVo;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.wechat.video.PayComponentShopBrand;

import java.util.List;

/**
 * PayComponentShopBrandService 接口
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
public interface PayComponentShopBrandService extends IService<PayComponentShopBrand> {

    /**
     * 上传品牌
     * @param request 上传品牌请求参数
     * @return 审核单id
     */
    String auditBrand(ShopAuditBrandRequestVo request);

    /**
     * 获取品牌详情（根据审核单号）
     * @param auditId 审核单号
     * @return 品牌详情
     */
    PayComponentShopBrand getByAuditId(String auditId);

    /**
     * 获取品牌列表
     * @param pageParamRequest 分页参数
     * @param status 审核状态, 0：审核中，1：审核成功，9：审核拒绝
     * @return 品牌列表
     */
    PageInfo<PayComponentShopBrand> findList(PageParamRequest pageParamRequest, Integer status);

    /**
     * 获取品牌列表（可用）
     * @return 品牌列表
     */
    List<PayComponentShopBrand> getUsableList();
}
