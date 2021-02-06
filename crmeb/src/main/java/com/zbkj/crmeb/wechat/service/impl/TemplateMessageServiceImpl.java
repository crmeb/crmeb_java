package com.zbkj.crmeb.wechat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.utils.RedisUtil;
import com.zbkj.crmeb.user.model.UserToken;
import com.zbkj.crmeb.user.service.UserTokenService;
import com.zbkj.crmeb.wechat.dao.TemplateMessageDao;
import com.zbkj.crmeb.wechat.model.TemplateMessage;
import com.zbkj.crmeb.wechat.request.TemplateMessageSearchRequest;
import com.zbkj.crmeb.wechat.service.TemplateMessageService;
import com.zbkj.crmeb.wechat.service.WeChatService;
import com.zbkj.crmeb.wechat.service.WechatProgramMyTempService;
import com.zbkj.crmeb.wechat.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TemplateMessageServiceImpl 接口实现
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
public class TemplateMessageServiceImpl extends ServiceImpl<TemplateMessageDao, TemplateMessage> implements TemplateMessageService {
    private static final Logger logger = LoggerFactory.getLogger(TemplateMessageServiceImpl.class);

    @Resource
    private TemplateMessageDao dao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private WechatProgramMyTempService wechatProgramMyTempService;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-06-03
    * @return List<TemplateMessage>
    */
    @Override
    public List<TemplateMessage> getList(TemplateMessageSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        //带 TemplateMessage 类的多条件查询
        LambdaQueryWrapper<TemplateMessage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(null != request.getStatus()){
            lambdaQueryWrapper.eq(TemplateMessage::getStatus, request.getStatus());
        }

        if(StringUtils.isNotBlank(request.getName())){
            lambdaQueryWrapper.like(TemplateMessage::getName, request.getName());
        }

        if(StringUtils.isNotBlank(request.getTempId())){
            lambdaQueryWrapper.eq(TemplateMessage::getTempId, request.getTempId());
        }

        if(StringUtils.isNotBlank(request.getTempKey())){
            lambdaQueryWrapper.eq(TemplateMessage::getTempKey, request.getTempKey());
        }
        if (ObjectUtil.isNotNull(request.getType())) {
            lambdaQueryWrapper.eq(TemplateMessage::getType, request.getType());
        }
        lambdaQueryWrapper.orderByDesc(TemplateMessage::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 发送给微信消息进入队列
     * @param tempKey String 模板消息key
     * @param map 需要替换的数据
     * @param userId Integer 用户id
     * @param type String 类型， public 公众号；program 小程序
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    public void push(String tempKey, HashMap<String, String> map, Integer userId, String type){

        switch (type){
            case Constants.PAY_TYPE_WE_CHAT_FROM_PUBLIC:
                pushPublicTempMessage(tempKey, map, userId, type);
                break;
            case Constants.PAY_TYPE_WE_CHAT_FROM_PROGRAM:
                wechatProgramMyTempService.push(Integer.parseInt(tempKey), map, userId);
                break;
            default:
                break;
        }
    }

    /**
     * 发送公众号消息
     * @param tempKey String 模板消息key
     * @param map 需要替换的数据
     * @param userId Integer 用户id
     * @param type String 类型， public 公众号；program 小程序
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    public void pushPublicTempMessage(String tempKey, HashMap<String, String> map, Integer userId, String type){
        TemplateMessageVo templateMessageVo = new TemplateMessageVo();

        TemplateMessage templateMessage = getInfoByTempKey(tempKey);
        if(templateMessage == null || StringUtils.isBlank(templateMessage.getContent())){
            return;
        }
        templateMessageVo.setTemplate_id(templateMessage.getTempId());

        HashMap<String, SendTemplateMessageItemVo> hashMap = new HashMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()){
            hashMap.put(entry.getKey(), new SendTemplateMessageItemVo(entry.getValue()));
        }
        //token 类型
        int openIdType = Constants.THIRD_LOGIN_TOKEN_TYPE_PUBLIC;
        if(Constants.ADMIN_LOGIN_TYPE_WE_CHAT_FROM_PUBLIC.equals(type)){
            openIdType = Constants.THIRD_ADMIN_LOGIN_TOKEN_TYPE_PUBLIC;

        }

        //拿到三方登录绑定的token
        UserToken UserToken = userTokenService.getTokenByUserId(userId, openIdType);

        if(null == UserToken || StringUtils.isBlank(UserToken.getToken())){
            return;
        }

        templateMessageVo.setData(hashMap);
        templateMessageVo.setTouser(UserToken.getToken());
        redisUtil.lPush(Constants.WE_CHAT_MESSAGE_KEY_PUBLIC, JSONObject.toJSONString(templateMessageVo));
    }

    /**
     * 查询单条数据抛异常
     * @param id Integer id
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    @Override
    public TemplateMessage infoException(Integer id) {
        TemplateMessage templateMessage = info(id);
        if(null == templateMessage){
            throw new CrmebException("此模板" + id + " 不存在或者已删除");
        }

        if(templateMessage.getStatus().equals(0)){
            throw new CrmebException("此模板未启用");
        }
        return templateMessage;
    }

    /**
     * 公众号消费队列消费
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    @Override
    public void consumePublic() {
        String redisKey = Constants.WE_CHAT_MESSAGE_KEY_PUBLIC;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("TemplateMessageServiceImpl.consumePublic | size:" + size);
        if(size < 1){
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if(null == data){
                continue;
            }
            try{
                TemplateMessageVo templateMessage = JSONObject.toJavaObject(JSONObject.parseObject(data.toString()), TemplateMessageVo.class);
                boolean result = weChatService.sendPublicTempMessage(templateMessage);
                if(!result){
                    redisUtil.lPush(redisKey, data);
                }
            }catch (Exception e){
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 小程序消费队列消费
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    @Override
    public void consumeProgram() {
        String redisKey = Constants.WE_CHAT_MESSAGE_KEY_PROGRAM;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("TemplateMessageServiceImpl.consumeProgram | size:" + size);
        if(size < 1){
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if(null == data){
                continue;
            }
            try{
                TemplateMessageVo templateMessage = JSONObject.toJavaObject(JSONObject.parseObject(data.toString()), TemplateMessageVo.class);
                boolean result = weChatService.sendProgramTempMessage(templateMessage);
                if(!result){
                    redisUtil.lPush(redisKey, data);
                }
            }catch (Exception e){
                redisUtil.lPush(redisKey, data);
            }
        }
    }


    /**
     * 模板消息 行业信息
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return TemplateMessageIndustryVo
     */
    @Override
    public TemplateMessageIndustryVo getIndustry() {
        JSONObject data;
        Object object = redisUtil.get(Constants.WE_CHAT_MESSAGE_INDUSTRY_KEY);
        if(null == object){
            data = weChatService.getIndustry();
        }else{
            data = JSONObject.parseObject(object.toString());
        }
        return JSONObject.toJavaObject(data, TemplateMessageIndustryVo.class);
    }

    /**
     * 发送模板消息
     * @param templateNo 模板消息编号
     * @param temMap 内容Map
     * @param openId 微信用户openid
     */
    @Override
    public void pushTemplateMessage(String templateNo, HashMap<String, String> temMap, String openId) {
        TemplateMessageVo templateMessageVo = new TemplateMessageVo();

        TemplateMessage templateMessage = getInfoByTempKey(templateNo);
        if(ObjectUtil.isNull(templateMessage) || StrUtil.isBlank(templateMessage.getContent())){
            return;
        }
        templateMessageVo.setTemplate_id(templateMessage.getTempId());

        HashMap<String, SendTemplateMessageItemVo> hashMap = new HashMap<>();
        for (Map.Entry<String, String> entry : temMap.entrySet()){
            hashMap.put(entry.getKey(), new SendTemplateMessageItemVo(entry.getValue()));
        }

        templateMessageVo.setData(hashMap);
        templateMessageVo.setTouser(openId);
        redisUtil.lPush(Constants.WE_CHAT_MESSAGE_KEY_PUBLIC, JSONObject.toJSONString(templateMessageVo));
    }

    /**
     * 发送小程序订阅消息
     * @param templateNo 模板消息编号
     * @param temMap 内容Map
     * @param openId 微信用户openId
     */
    @Override
    public void pushMiniTemplateMessage(String templateNo, HashMap<String, String> temMap, String openId) {
        TemplateMessage templateMessage = getInfoByTempKey(templateNo);
        if(ObjectUtil.isNull(templateMessage) || StrUtil.isBlank(templateMessage.getContent())){
            return;
        }

        ProgramTemplateMessageVo programTemplateMessageVo = new ProgramTemplateMessageVo();
        programTemplateMessageVo.setTemplate_id(templateMessage.getTempId());

        //组装关键字数据
        HashMap<String, SendProgramTemplateMessageItemVo> hashMap = new HashMap<>();
        temMap.forEach((key, value) -> hashMap.put(key, new SendProgramTemplateMessageItemVo(value)));

        programTemplateMessageVo.setData(hashMap);
        programTemplateMessageVo.setTouser(openId);
        redisUtil.lPush(Constants.WE_CHAT_MESSAGE_KEY_PROGRAM, JSONObject.toJSONString(programTemplateMessageVo));
    }

    /**
     * 获取小程序订阅模板编号
     * @param type 场景类型
     * 支付之前：beforePay
     * 		支付成功通知
     * 支付成功：afterPay
     * 		发货、配送、收货
     * 申请退款：refundApply
     * 		退款成功、拒绝退款
     * 充值之前：beforeRecharge
     * 		充值成功通知
     * 创建砍价：createBargain
     * 		砍价成功、失败通知
     * 参与拼团：pink
     * 		拼团成功、失败通知
     * 取消拼团：cancelPink
     * 		退款成功、拒绝退款
     * @return
     */
    @Override
    public List<TemplateMessage> getMiniTempList(String type) {
        LambdaQueryWrapper<TemplateMessage> lqw = new LambdaQueryWrapper<>();
        lqw.select(TemplateMessage::getName, TemplateMessage::getTempId);
        lqw.eq(TemplateMessage::getStatus, 1); //没有禁用的数据
        lqw.eq(TemplateMessage::getType, 0);
        switch (type) {
            case "beforePay":// 支付之前
                lqw.eq(TemplateMessage::getTempKey, Constants.WE_CHAT_PROGRAM_TEMP_KEY_ORDER_PAY);
                break;
            case "afterPay":// 支付成功
                lqw.in(TemplateMessage::getTempKey, Constants.WE_CHAT_PROGRAM_TEMP_KEY_EXPRESS, Constants.WE_CHAT_PROGRAM_TEMP_KEY_DELIVERY, Constants.WE_CHAT_PROGRAM_TEMP_KEY_ORDER_RECEIVING);
                break;
            case "refundApply":// 申请退款
                lqw.eq(TemplateMessage::getTempKey, "-1");
                break;
            case "beforeRecharge":// 充值之前
                lqw.eq(TemplateMessage::getTempKey, "-1");
                break;
            case "createBargain":// 创建砍价
                lqw.in(TemplateMessage::getTempKey, Constants.WE_CHAT_PROGRAM_TEMP_KEY_BARGAIN_SUCCESS);
                break;
            case "pink":// 参与拼团
                lqw.in(TemplateMessage::getTempKey, Constants.WE_CHAT_PROGRAM_TEMP_KEY_COMBINATION_SUCCESS);
                break;
            case "cancelPink":// cancelPink
                lqw.eq(TemplateMessage::getTempKey, "-1");
                break;
        }
        return dao.selectList(lqw);
    }

    /**
     * 查询单条数据
     * @param id Integer id
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    private TemplateMessage info(Integer id) {
        return getById(id);
    }

    /**
     * 根据tempKey获取信息
     * @param tempKey String 模板消息key
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return TemplateMessage
     */
    private TemplateMessage getInfoByTempKey(String tempKey) {
        LambdaQueryWrapper<TemplateMessage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TemplateMessage::getTempKey, tempKey);
        lambdaQueryWrapper.eq(TemplateMessage::getStatus, 1); //没有禁用的数据
        List<TemplateMessage> templateMessages = dao.selectList(lambdaQueryWrapper);
        if(null == templateMessages || templateMessages.size() < 1){
            return null;
        }

        return templateMessages.get(0);
    }
}

