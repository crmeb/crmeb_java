package com.zbkj.service.service;

import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.request.*;
import com.zbkj.common.request.onepass.OnePassLoginRequest;
import com.zbkj.common.request.onepass.OnePassShipmentCancelOrderRequest;
import com.zbkj.common.request.onepass.OnePassShipmentCreateOrderRequest;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.vo.OnePassLogisticsQueryVo;

/**
 * 一号通 OnePassService 接口
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
public interface OnePassService {


    /**
     * 用户登录
     * @param request 登录参数
     */
    Boolean login(OnePassLoginRequest request);

    /**
     * 保存一号通应用信息
     * @param request 一号通服务中申请的应用信息
     * @return 保存结果
     */
    Boolean saveOnePassApplicationInfo(OnePassLoginRequest request);

    /**
     * 获取一号通应用信息
     * @return 一号通应用信息
     */
    OnePassLoginRequest getOnePassApplicationInfo();

    /**
     *  商家寄件
     * @param request 寄件请求对象
     * @return 寄件返回数据
     */
    JSONObject shipmentCreateOrder(OnePassShipmentCreateOrderRequest request);


    /**
     * 取消商家寄件
     * @param request 取消商家寄件请求对象
     * @return 取消寄件返回对象
     */
    JSONObject shipmentCancelOrder(OnePassShipmentCancelOrderRequest request);

    /**
     * 获取商家寄件所需的快递公司列表
     * @return 商家寄件功能对应的快递公司列表
     */
    JSONObject shipmentComs();

    /**
     * 商家寄件功能对应的回调
     * @return 回调数据
     */
    Boolean shipmentCallBackMethod(String type, String data);


    /**
     * 一号通用户信息
     */
    JSONObject info();
    /**
     * 服务开通
     * @param request 服务开通参数
     */
    Boolean serviceOpen(ServiceOpenRequest request);

    /**
     * 复制平台商品
     * @param url 商品链接
     */
    JSONObject copyGoods(String url);

    /**
     * 电子面单
     */
    MyRecord expressDump(MyRecord record);

    /**
     * 物流追踪
     * @param expressNo 快递单号
     * @param com   快递公司简写
     * @return OnePassLogisticsQueryVo
     */
    OnePassLogisticsQueryVo exprQuery(String expressNo, String com);

    /**
     * 校验一号通账号是否配置
     */
    Boolean checkAccount();
}
