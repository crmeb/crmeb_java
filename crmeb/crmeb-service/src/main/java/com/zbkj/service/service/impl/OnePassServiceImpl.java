package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.request.*;
import com.zbkj.common.request.onepass.OnePassLoginRequest;
import com.zbkj.common.request.onepass.OnePassShipmentCancelOrderRequest;
import com.zbkj.common.request.onepass.OnePassShipmentCreateOrderRequest;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.common.constants.OnePassConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.ValidateFormUtil;
import com.zbkj.common.model.express.Express;
import com.zbkj.common.vo.OnePassLoginVo;
import com.zbkj.common.vo.OnePassLogisticsQueryVo;
import com.zbkj.service.service.ExpressService;
import com.zbkj.service.service.OnePassService;
import com.zbkj.service.service.StoreOrderService;
import com.zbkj.service.service.SystemConfigService;
import com.zbkj.service.util.OnePassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OnePassService 接口实现
 * 一号通
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class OnePassServiceImpl implements OnePassService {

    private final Logger logger = LoggerFactory.getLogger(OnePassServiceImpl.class);

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private OnePassUtil onePassUtil;

    @Autowired
    private ExpressService expressService;

    @Autowired
    private StoreOrderService storeOrderService;

    /**
     * 保存一号通应用信息
     *
     * @param request 一号通服务中申请的应用信息
     * @return 保存结果
     */
    @Override
    public Boolean saveOnePassApplicationInfo(OnePassLoginRequest request) {
        setConfigSmsInfo(request.getAccessKey(), request.getSecretKey());
        return Boolean.TRUE;
    }

    /**
     * 获取一号通应用信息
     *
     * @return 一号通应用信息
     */
    @Override
    public OnePassLoginRequest getOnePassApplicationInfo() {
        return getConfigSmsInfo();
    }

    /**
     * 商家寄件
     *
     * @param request 寄件请求对象
     * @return 寄件返回数据
     */
    @Override
    public JSONObject shipmentCreateOrder(OnePassShipmentCreateOrderRequest request) {
        OnePassLoginVo loginVo = onePassUtil.getLoginVo();
        String accessToken = onePassUtil.getToken(loginVo);
        HashMap<String, String> header = onePassUtil.getCommonHeader(accessToken);
        setOnePassShipmentCallbackURL(request);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("kuaidicom", request.getKuaidicom());
        param.add("man_name", request.getManName());
        param.add("phone",request.getPhone());
        param.add("address",request.getAddress());
        param.add("send_real_name",request.getSendRealName());
        param.add("send_phone",request.getSendPhone());
        param.add("send_address",request.getSendAddress());
        param.add("call_back_url",request.getCallBackUrl());
        param.add("cargo",request.getCargo());
        param.add("service_type",request.getServiceType());
        param.add("payment",request.getPayment());
        param.add("weight",request.getWeight());
        param.add("remark",request.getRemark());
        param.add("day_type",request.getDayType());
        param.add("pickup_start_time",request.getPickupStartTime());
        param.add("pickup_end_time",request.getPickupEndTime());
        param.add("channel_sw",request.getChannelSw());
        param.add("valins_pay",request.getValinsPay());
        param.add("real_name",request.getRealName());
        param.add("send_id_card_type",request.getSendIdCardType());
        param.add("send_id_card",request.getSendIdCard());
        param.add("password_signing",request.getPasswordSigning());
        param.add("op",request.getOp());
        param.add("return_type",request.getReturnType());
        param.add("siid",request.getSiid());
        param.add("tempid",request.getTempid());
        logger.info("一号通-商家寄件:参数:{}", param);
        JSONObject jsonObject = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_SHIPMENT_CREATE_ORDER_URI, param, header);
        logger.info("一号通-商家寄件:结果:{}", jsonObject);
        return jsonObject.getJSONObject("data");
    }

    /**
     * 取消商家寄件
     *
     * @param request 取消商家寄件请求对象
     * @return 取消寄件返回对象
     */
    @Override
    public JSONObject shipmentCancelOrder(OnePassShipmentCancelOrderRequest request) {
        OnePassLoginVo loginVo = onePassUtil.getLoginVo();
        String accessToken = onePassUtil.getToken(loginVo);
        HashMap<String, String> header = onePassUtil.getCommonHeader(accessToken);

        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("task_id", request.getTaskId());
        param.add("order_id", request.getOrderId());
        param.add("cancel_msg",request.getCancelMsg());
        JSONObject jsonObject = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_SHIPMENT_CANCEL_ORDER_URI, param, header);
        logger.info("一号通-商家取消寄件 :{}", jsonObject);
        return jsonObject.getJSONObject("data");
    }

    /**
     * 获取商家寄件所需的快递公司列表
     *
     * @return 商家寄件功能对应的快递公司列表
     */
    @Override
    public JSONObject shipmentComs() {
        OnePassLoginVo loginVo = onePassUtil.getLoginVo();
        String accessToken = onePassUtil.getToken(loginVo);
        HashMap<String, String> header = onePassUtil.getCommonHeader(accessToken);
        JSONObject jsonObject = onePassUtil.getData(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_SHIPMENT_GET_KUAIDI_COMS_URI, header);
        logger.info("一号通-商家寄件-物流地址 :{}", jsonObject);
        return jsonObject;
    }

    /**
     * 商家寄件功能对应的回调
     * order_success	下单成功回调
     * order_take	取件回调
     * order_cancel	用户主动取消回调
     * order_fail	下单失败回调
     * order_receipt	快递签收回调
     * @return 回调数据
     */
    @Override
    public Boolean shipmentCallBackMethod(String type, String data) {
        logger.info("一号通-商家寄件-回调:type:{}", type);
        logger.info("一号通-商家寄件-回调:data:{}", data);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSON.parseObject(com.zbkj.common.utils.OnePassUtil.decrypt(data));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("一号通商家寄件 解密数据失败:{}", e.getMessage());
        }
        switch (type){
            case "order_success":
                break;
            case "order_take": // 取件回调
                storeOrderService.expressForOnePassShipmentTakeCallBack(jsonObject);
                break;
            case "order_cancel":
            case "order_fail": // 下单失败回调 清理掉所有商家寄件的数据
                storeOrderService.expressForOnePassShipmentCancelCallBack(jsonObject);
                break;
            case "order_receipt": // todo 一号通新增接口，后期可以规划和订单签收结果合并
                break;

        }

        return Boolean.TRUE;
    }

    /**
     * 用户登录
     */
    @Override
    public Boolean login(OnePassLoginRequest request) {
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add(OnePassConstants.ONE_PASS_ACCESS_KEY, request.getAccessKey());
        param.add(OnePassConstants.ONE_PASS_SECRET_KEY, request.getSecretKey());
        JSONObject jsonObject = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.USER_LOGIN_URI, param, null);

        String accessToken = OnePassConstants.ONE_PASS_USER_TOKEN_PREFIX.concat(jsonObject.getJSONObject("data").getString("access_token"));
        Long expiresIn = jsonObject.getJSONObject("data").getLong("expires_in");
        expiresIn = expiresIn - CrmebDateUtil.getTime();
        redisUtil.set(StrUtil.format(OnePassConstants.ONE_PASS_TOKEN_KEY_PREFIX, request.getAccessKey()), accessToken, expiresIn, TimeUnit.SECONDS);
        return true;
    }

    /**
     * 一号通用户信息
     */
    @Override
    public JSONObject info() {
        JSONObject info = getInfo();
        // 判断是否开通电子面单
        JSONObject dump = info.getJSONObject("dump");
        Integer open = dump.getInteger("open");
//        if (open.equals(1)) {
//            String exportSiid = systemConfigService.getValueByKey("config_export_siid");// 打印机编号
//            if (StrUtil.isBlank(exportSiid)) {
//                dump.put("open", 0);
//                info.put("dump", dump);
//            }
//        }
        return info;
    }

    private JSONObject getInfo() {
        OnePassLoginVo loginVo = onePassUtil.getLoginVo();
        String accessToken = onePassUtil.getToken(loginVo);
        HashMap<String, String> header = onePassUtil.getCommonHeader(accessToken);
        JSONObject jsonObject = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.USER_INFO_URI, null, header);
        JSONObject data = jsonObject.getJSONObject("data");
        data.put("account", loginVo.getAccessKey());
        return data;
    }


    /**
     * 服务开通
     *
     * @param request 服务开通参数
     */
    @Override
    public Boolean serviceOpen(ServiceOpenRequest request) {
        if (!validateMealType(request.getType())) throw new CrmebException("请选择正确的服务类型");
        Boolean open = false;
        switch (request.getType()) {
            case OnePassConstants.ONE_PASS_MEAL_TYPE_SMS:// 短信开通
                if (StrUtil.isBlank(request.getSign())) throw new CrmebException("签名不能为空");
                open = smsOpen(request);
                break;
            case OnePassConstants.ONE_PASS_MEAL_TYPE_EXPR:// 物流开通
                open = expressOpen(request);
                break;
            case OnePassConstants.ONE_PASS_MEAL_TYPE_DUMP:// 电子面单开通
                expressOpenValidate(request);
                open = expressOpen(request);
                break;
            case OnePassConstants.ONE_PASS_MEAL_TYPE_COPY:// 产品复制开通
                open = copyOpen();
                break;
        }
        return open;
    }


    @Override
    public JSONObject copyGoods(String url) {
        HashMap<String, String> header = onePassUtil.getCommonHeader(onePassUtil.getToken());
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("url", url);
        JSONObject jsonObject = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_COPY_GOODS_URI, params, header);
        System.out.println("one pass copy product = " + jsonObject);
        return jsonObject.getJSONObject("data");
    }

    /**
     * 电子面单
     * @param record 电子面单参数
     * @return
     */
    @Override
    public MyRecord expressDump(MyRecord record) {
        HashMap<String, String> header = onePassUtil.getCommonHeader(onePassUtil.getToken());
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        Map<String, Object> columns = record.getColumns();
        columns.forEach(params::add);
        JSONObject post = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_EXPRESS_DUMP_URI, params, header);
        MyRecord myRecord = new MyRecord();
        JSONObject jsonObject = post.getJSONObject("data");
        return myRecord.setColums(jsonObject);
    }

    /**
     * 物流追踪
     * @param expressNo 快递单号
     * @param com   快递公司简写
     * @return OnePassLogisticsQueryVo
     */
    @Override
    public OnePassLogisticsQueryVo exprQuery(String expressNo, String com) {
        HashMap<String, String> header = onePassUtil.getCommonHeader(onePassUtil.getToken());
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("com", com);
        params.add("num", expressNo);
        JSONObject post = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_EXPRESS_QUEARY_URI, params, header);
        String dataStr = post.getString("data");
        if (StrUtil.isBlank(dataStr) || dataStr.equals("[]")) {
            return null;
        }
        JSONObject jsonObject = post.getJSONObject("data");
        return JSONObject.toJavaObject(jsonObject, OnePassLogisticsQueryVo.class);
    }


    /**
     * 校验一号通账号是否配置
     */
    @Override
    public Boolean checkAccount() {
        String account = systemConfigService.getValueByKey(OnePassConstants.ONE_PASS_ACCESS_KEY);// 获取配置账号
        if (StrUtil.isBlank(account)) {
            return Boolean.FALSE;
        }
        String token = systemConfigService.getValueByKey(OnePassConstants.ONE_PASS_SECRET_KEY); //获取配置密码
        if (StrUtil.isBlank(token)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }









    ///////////////////////////////////////////////////// 工具方法

    /**
     * 物流、电子面单开通参数校验
     */
    private void expressOpenValidate(ServiceOpenRequest request) {
        if (StrUtil.isBlank(request.getCom())) {
            throw new CrmebException("请选择快递公司");
        }
        if (StrUtil.isBlank(request.getTempId())) {
            throw new CrmebException("请选择快递模板");
        }
        if (StrUtil.isBlank(request.getToName())) {
            throw new CrmebException("请填写寄件人姓名");
        }
        if (StrUtil.isBlank(request.getToTel())) {
            throw new CrmebException("请输入寄件人手机号码");
        }
        if (StrUtil.isBlank(request.getToAddress())) {
            throw new CrmebException("请填写寄件人详细地址");
        }
        if (StrUtil.isBlank(request.getSiid())) {
            throw new CrmebException("请填写云打印机编号");
        }
        ValidateFormUtil.isPhoneException(request.getToTel());
    }

    /**
     * 商品复制开通
     */
    private Boolean copyOpen() {
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_COPY_OPEN_URI, null, header);
        return Boolean.TRUE;
    }

    /**
     * 物流开通
     * 保存平台电子面单打印信息
     */
    private Boolean expressOpen(ServiceOpenRequest request) {
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        if (request.getType().equals(OnePassConstants.ONE_PASS_MEAL_TYPE_EXPR)) {// 物流
            onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_EXPRESS_OPEN_URI, null, header);
            return Boolean.TRUE;
        }

        // 电子面单
        Express express = expressService.getByCode(request.getCom());
        if (ObjectUtil.isNull(express)) throw new CrmebException("没有找到对应的快递公司");

        // 保存平台电子面单打印信息
        Boolean execute = transactionTemplate.execute(e -> {
            // 快递公司id
            systemConfigService.updateOrSaveValueByName("config_export_id", express.getId().toString());
            // 快递公司模板id
            systemConfigService.updateOrSaveValueByName("config_export_temp_id", request.getTempId());
            // 快递公司编码
            systemConfigService.updateOrSaveValueByName("config_export_com", request.getCom());
            // 发货人姓名
            systemConfigService.updateOrSaveValueByName("config_export_to_name", request.getToName());
            // 发货人电话
            systemConfigService.updateOrSaveValueByName("config_export_to_tel", request.getToTel());
            // 发货人详细地址
            systemConfigService.updateOrSaveValueByName("config_export_to_address", request.getToAddress());
            // 电子面单打印机编号
            systemConfigService.updateOrSaveValueByName("config_export_siid", request.getSiid());
            return Boolean.TRUE;
        });
        if (Boolean.FALSE.equals(execute)) throw new CrmebException("保存平台电子面单打印信息失败");

        // 查询是否已经开通
        JSONObject info = info();
        JSONObject dump = info.getJSONObject("dump");
        if (dump.getInteger("open").equals(1)) {
            return Boolean.TRUE;
        }
        onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_EXPRESS_OPEN_URI, null, header);
        return Boolean.TRUE;
    }

    /**
     * 短信开通
     */
    private Boolean smsOpen(ServiceOpenRequest request) {
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("sign", request.getSign());
        onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_SMS_OPEN_URI, params, header);
        return Boolean.TRUE;
    }

    /**
     * 校验套餐类型
     */
    private Boolean validateMealType(String type) {
        List<String> list = CollUtil.newArrayList();
        list.add(OnePassConstants.ONE_PASS_MEAL_TYPE_SMS);
        list.add(OnePassConstants.ONE_PASS_MEAL_TYPE_EXPR);
        list.add(OnePassConstants.ONE_PASS_MEAL_TYPE_DUMP);
        list.add(OnePassConstants.ONE_PASS_MEAL_TYPE_COPY);
        return list.contains(type);
    }

    /**
     * 配置一号通应用 appid 和 密钥信息
     *
     * @param accessKey  账号
     * @param secretKey 密码
     */
    private void setConfigSmsInfo(String accessKey, String secretKey) {
        boolean accountResult = systemConfigService.updateOrSaveValueByName(OnePassConstants.ONE_PASS_ACCESS_KEY, accessKey);
        boolean tokenResult = systemConfigService.updateOrSaveValueByName(OnePassConstants.ONE_PASS_SECRET_KEY, secretKey);

        if (!accountResult || !tokenResult) {
            throw new CrmebException("数据更新失败！");
        }
    }


    /**
     * 获取一号通 应用配置信息
     * @return 应用配置
     */
    private OnePassLoginRequest getConfigSmsInfo() {
        OnePassLoginRequest onePassLoginRequest = new OnePassLoginRequest();
        onePassLoginRequest.setAccessKey(systemConfigService.getValueByKey(OnePassConstants.ONE_PASS_ACCESS_KEY));
        onePassLoginRequest.setSecretKey(systemConfigService.getValueByKey(OnePassConstants.ONE_PASS_SECRET_KEY));
        return onePassLoginRequest;
    }

    /**
     * 一号通 商家发货 获取预置好的回调地址
     */
    private void setOnePassShipmentCallbackURL(OnePassShipmentCreateOrderRequest request){
        request.setCallBackUrl(systemConfigService.getValueByKey(Constants.CONFIG_KEY_API_URL) + OnePassConstants.ONE_PASS_API_SHIPMENT_CALLBACK_URI);
    }

}
