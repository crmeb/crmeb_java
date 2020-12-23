package com.zbkj.crmeb.wechat.service.impl;

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
import com.zbkj.crmeb.wechat.vo.SendTemplateMessageItemVo;
import com.zbkj.crmeb.wechat.vo.TemplateMessageIndustryVo;
import com.zbkj.crmeb.wechat.vo.TemplateMessageVo;
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

