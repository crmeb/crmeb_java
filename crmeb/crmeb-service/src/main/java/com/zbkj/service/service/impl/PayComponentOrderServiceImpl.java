package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.model.wechat.video.PayComponentOrder;
import com.zbkj.common.model.wechat.video.PayComponentOrderProduct;
import com.zbkj.common.vo.*;
import com.zbkj.service.dao.PayComponentOrderDao;
import com.zbkj.service.service.PayComponentOrderProductService;
import com.zbkj.service.service.PayComponentOrderService;
import com.zbkj.service.service.WechatVideoAftersaleService;
import com.zbkj.service.service.WechatVideoOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
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
public class PayComponentOrderServiceImpl extends ServiceImpl<PayComponentOrderDao, PayComponentOrder> implements PayComponentOrderService {

    @Resource
    private PayComponentOrderDao dao;

    @Autowired
    private WechatVideoOrderService wechatVideoOrderService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private PayComponentOrderProductService componentOrderProductService;
    @Autowired
    private WechatVideoAftersaleService wechatVideoAftersaleService;

    /**
     * 创建组件订单
     * @param shopOrderAddVo 创建订单参数
     * @return String
     */
    @Override
    public String create(ShopOrderAddVo shopOrderAddVo) {
        ShopOrderAddResultVo addResultVo = wechatVideoOrderService.shopOrderAdd(shopOrderAddVo);
        // 生成订单表数据
        PayComponentOrder order = new PayComponentOrder();
        order.setOrderNo(shopOrderAddVo.getOutOrderId());
        order.setComponentOrderId(addResultVo.getOrderId());
        order.setOpenid(shopOrderAddVo.getOpenid());
        order.setPath(shopOrderAddVo.getPath());
        order.setScene(shopOrderAddVo.getScene());
        order.setOutUserId(shopOrderAddVo.getOutUserId());
        ShopOrderPayInfoAddVo payInfo = shopOrderAddVo.getOrderDetail().getPayInfo();
        order.setPayMethod(payInfo.getPayMethod());
        order.setPayMethodType(payInfo.getPayMethodType());
        order.setPrepayId(payInfo.getPrepayId());
        order.setPrepayTime(payInfo.getPrepayTime());
        ShopOrderPriceInfoVo priceInfo = shopOrderAddVo.getOrderDetail().getPriceInfo();
        order.setOrderPrice(priceInfo.getOrderPrice());
        order.setFreight(priceInfo.getFreight());
        order.setDiscountedPrice(priceInfo.getDiscountedPrice());
        order.setDeliveryType(shopOrderAddVo.getDeliveryDetail().getDeliveryType());
        ShopOrderAddressInfoAddVo addressInfo = shopOrderAddVo.getAddressInfo();
        order.setReceiverName(addressInfo.getReceiverName());
        order.setTelNumber(addressInfo.getTelNumber());
        order.setDetailedAddress(addressInfo.getDetailedAddress());
        order.setTicket(addResultVo.getTicket());
        order.setTicketExpireTime(addResultVo.getTicketExpireTime());
        order.setFinalPrice(addResultVo.getFinalPrice());
        order.setStatus(10);
        order.setFinishAllDelivery(0);
        order.setCreateTime(CrmebDateUtil.nowDateTime());

        // 订单详情
        List<ShopOrderProductInfoAddVo> productInfos = shopOrderAddVo.getOrderDetail().getProductInfos();
        List<PayComponentOrderProduct> orderProductList = productInfos.stream().map(productInfo -> {
            PayComponentOrderProduct orderProduct = new PayComponentOrderProduct();
            orderProduct.setOrderNo(order.getOrderNo());
            orderProduct.setComponentOrderId(order.getComponentOrderId());
//            orderProduct.setProductId(Integer.valueOf());
            orderProduct.setOutProductId(productInfo.getOutProductId());
//            orderProduct.setSkuId();
            orderProduct.setOutSkuId(productInfo.getOutSkuId());
            orderProduct.setProductCnt(productInfo.getProductCnt());
            orderProduct.setSalePrice(productInfo.getSalePrice());
            orderProduct.setHeadImg(productInfo.getHeadImg());
            orderProduct.setTitle(productInfo.getTitle());
            orderProduct.setPath(productInfo.getPath());
            orderProduct.setCreateTime(CrmebDateUtil.nowDateTime());
            return orderProduct;
        }).collect(Collectors.toList());

        Boolean execute = transactionTemplate.execute(e -> {
            save(order);
            componentOrderProductService.saveBatch(orderProductList);
            return Boolean.TRUE;
        });
        if (!execute) {
            throw new CrmebException("保存组件订单失败");
        }
        return order.getTicket();
    }

    /**
     * 通过订单号获取订单
     * @param orderNo 订单编号
     * @return PayComponentOrder
     */
    @Override
    public PayComponentOrder getByOrderNo(String orderNo) {
        LambdaQueryWrapper<PayComponentOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(PayComponentOrder::getOrderNo, orderNo);
        return dao.selectOne(lqw);
    }

    /**
     * 创建售后
     * @param orderNo 订单编号
     */
    @Override
    public void createAfterSale(String orderNo) {
        PayComponentOrder order = getByOrderNo(orderNo);
        if (ObjectUtil.isNull(order)) {
            log.error("视频号订单创建售后未找到订单，订单编号=" + orderNo);
            return;
        }
        List<PayComponentOrderProduct> orderProductList = componentOrderProductService.getListByOrderNo(orderNo);

        ShopAftersaleAddVo shopAftersaleAddVo = new ShopAftersaleAddVo();
        shopAftersaleAddVo.setOutOrderId(orderNo);
        shopAftersaleAddVo.setOutAftersaleId(orderNo);
        shopAftersaleAddVo.setOpenid(order.getOpenid());
        shopAftersaleAddVo.setType(1);// 1:退款,2:退款退货,3:换货
        shopAftersaleAddVo.setCreateTime(CrmebDateUtil.nowDate());
        shopAftersaleAddVo.setStatus(2);// 2-商家受理中
        shopAftersaleAddVo.setFinishAllAftersale(0);
        shopAftersaleAddVo.setPath(StrUtil.format("/pages/order_details/index?order_id={}&type={}", orderNo, "video"));
        List<AftersaleProductInfoVo> infoVoList = orderProductList.stream().map(product -> {
            AftersaleProductInfoVo infoVo = new AftersaleProductInfoVo();
            infoVo.setOutProductId(product.getOutProductId());
            infoVo.setOutSkuId(product.getOutSkuId());
            infoVo.setProductCnt(product.getProductCnt());
            return infoVo;
        }).collect(Collectors.toList());
        shopAftersaleAddVo.setProductInfos(infoVoList);
        Boolean add = wechatVideoAftersaleService.shopAftersaleAdd(shopAftersaleAddVo);
        if (!add) {// 创建售后失败
            log.error("视频号订单创建售后失败，orderNo=" + orderNo);
            throw new CrmebException("视频号订单创建售后失败，orderNo=" + orderNo);
        }
        // 创建售后成功,更新售后
        ShopAftersaleUpdateVo updateVo = new ShopAftersaleUpdateVo();
        updateVo.setOutOrderId(orderNo);
        updateVo.setOpenid(order.getOpenid());
        updateVo.setOutAftersaleId(orderNo);
        updateVo.setStatus(13);
        updateVo.setFinishAllAftersale(1);
        Boolean update = wechatVideoAftersaleService.shopAftersaleUpdate(updateVo);
        if (!update) {
            log.error("视频号订单更新售后失败，orderNo=" + orderNo);
            throw new CrmebException("视频号订单更新售后失败，orderNo=" + orderNo);
        }
    }
}

