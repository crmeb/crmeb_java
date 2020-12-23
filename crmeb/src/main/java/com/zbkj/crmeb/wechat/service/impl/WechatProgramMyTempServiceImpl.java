package com.zbkj.crmeb.wechat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.CrmebException;
import com.github.pagehelper.PageHelper;
import com.utils.CrmebUtil;
import com.utils.PinYinUtil;
import com.utils.RedisUtil;
import com.zbkj.crmeb.user.model.UserToken;
import com.zbkj.crmeb.user.service.UserTokenService;
import com.zbkj.crmeb.wechat.dao.WechatProgramMyTempDao;
import com.zbkj.crmeb.wechat.model.WechatProgramMyTemp;
import com.zbkj.crmeb.wechat.request.WechatProgramMyTempRequest;
import com.zbkj.crmeb.wechat.request.WechatProgramMyTempSearchRequest;
import com.zbkj.crmeb.wechat.service.WeChatService;
import com.zbkj.crmeb.wechat.service.WechatProgramMyTempService;
import com.zbkj.crmeb.wechat.vo.ProgramAddMyTempVo;
import com.zbkj.crmeb.wechat.vo.ProgramTempKeywordsVo;
import com.zbkj.crmeb.wechat.vo.ProgramTemplateMessageVo;
import com.zbkj.crmeb.wechat.vo.SendProgramTemplateMessageItemVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * WechatProgramMyTempServiceImpl 接口实现
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
public class WechatProgramMyTempServiceImpl extends ServiceImpl<WechatProgramMyTempDao, WechatProgramMyTemp> implements WechatProgramMyTempService {

    @Resource
    private WechatProgramMyTempDao dao;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private RedisUtil redisUtil;

    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhang
    * @since 2020-08-28
    * @return List<WechatProgramMyTemp>
    */
    @Override
    public List<WechatProgramMyTemp> getList(WechatProgramMyTempSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 WechatProgramMyTemp 类的多条件查询
        LambdaQueryWrapper<WechatProgramMyTemp> lambdaQueryWrapper = Wrappers.lambdaQuery();
        //模板标题模糊查询，其余精确查询
        if(StringUtils.isNotBlank(request.getTitle())){
            lambdaQueryWrapper.like(WechatProgramMyTemp::getTitle, request.getTitle());
        }

        if(StringUtils.isNotBlank(request.getTempId())){
            lambdaQueryWrapper.eq(WechatProgramMyTemp::getTempId, request.getTempId());
        }

        if(null != request.getStatus()){
            lambdaQueryWrapper.eq(WechatProgramMyTemp::getStatus, request.getStatus());
        }

        if(StringUtils.isNotBlank(request.getType())){
            lambdaQueryWrapper.eq(WechatProgramMyTemp::getType, request.getType());
        }
        if(null != request.getId()){
            lambdaQueryWrapper.eq(WechatProgramMyTemp::getId, request.getId());
        }

        lambdaQueryWrapper.orderByDesc(WechatProgramMyTemp::getUpdateTime);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 查询小程序我的模板信息
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-08-28
     */
    @Override
    public WechatProgramMyTemp info(Integer id) {
        WechatProgramMyTemp vo = getById(id);
        if(null == vo){
            throw new CrmebException("没有找到数据");
        }
        return vo;
    }

    /**
     * 禁用/禁用小程序我的模板
     * @param id Integer
     * @author Mr.Zhang
     * @since 2020-08-28
     */
    @Override
    public boolean updateStatus(Integer id, boolean status) {
        WechatProgramMyTemp vo = info(id);
        vo.setStatus(status);
        updateById(vo);
        return true;
    }

    /**
     * 添加我的模板
     * @param wechatProgramMyTempRequest WechatProgramMyTempRequest 添加我的模板参数
     * @author Mr.Zhang
     * @since 2020-06-01
     * @return String 添加至帐号下的模板id，发送小程序订阅消息时所需
     */
    @Override
    public boolean create(WechatProgramMyTempRequest wechatProgramMyTempRequest) {
        WechatProgramMyTemp wechatProgramMyTemp = new WechatProgramMyTemp();
        BeanUtils.copyProperties(wechatProgramMyTempRequest, wechatProgramMyTemp);

        //数据提交到微信
        String myTempId = addMyTemp(wechatProgramMyTemp);
        wechatProgramMyTemp.setTempId(myTempId);
        formatExtra(wechatProgramMyTemp);
        return save(wechatProgramMyTemp);
    }

    /**
     * 更新我的模板  由于微信无更新机制先删除，后添加
     * @param wechatProgramMyTempRequest WechatProgramMyTempRequest 添加我的模板参数
     * @author Mr.Zhang
     * @since 2020-06-01
     * @return String 添加至帐号下的模板id，发送小程序订阅消息时所需
     */
    @Override
    public boolean update(Integer id, WechatProgramMyTempRequest wechatProgramMyTempRequest) {
        WechatProgramMyTemp vo = getById(id);
        if(null == vo){
            throw new CrmebException("没有找到模板信息");
        }
        //删除微信已存在的数据
        deleteMyTemp(vo.getTempId());

        //数据提交到微信
        WechatProgramMyTemp wechatProgramMyTemp = new WechatProgramMyTemp();
        BeanUtils.copyProperties(wechatProgramMyTempRequest, wechatProgramMyTemp);
        wechatProgramMyTemp.setId(id);

        String myTempId = addMyTemp(wechatProgramMyTemp);
        wechatProgramMyTemp.setTempId(myTempId);
        formatExtra(wechatProgramMyTemp);

        return updateById(wechatProgramMyTemp);
    }




    @Override
    public void push(int myTempId, HashMap<String, String> map, Integer userId) {
        // 查询对应id并且状态启用的模版
        WechatProgramMyTempSearchRequest pram = new WechatProgramMyTempSearchRequest();
//        pram.setTempId(tempKey+"");
        pram.setId(myTempId);
        pram.setStatus(true);
        List<WechatProgramMyTemp> existMyTemps = getList(pram, new PageParamRequest());
        if(null == existMyTemps){
            return;
        }
        WechatProgramMyTemp wechatProgramMyTemp = null;
        if(existMyTemps.size() >= 1) wechatProgramMyTemp = existMyTemps.get(0);
//        WechatProgramMyTemp wechatProgramMyTemp = getById(tempKey);
        if(wechatProgramMyTemp == null || StringUtils.isBlank(wechatProgramMyTemp.getKid())){
            return;
        }

        //拿到三方登录绑定的token
        UserToken UserToken = userTokenService.getTokenByUserId(userId, Constants.THIRD_LOGIN_TOKEN_TYPE_PROGRAM);

        if(null == UserToken || StringUtils.isBlank(UserToken.getToken())){
            return;
        }

        ProgramTemplateMessageVo programTemplateMessageVo = new ProgramTemplateMessageVo();
        programTemplateMessageVo.setTemplate_id(wechatProgramMyTemp.getTempId());

        //组装关键字数据
        HashMap<String, SendProgramTemplateMessageItemVo> hashMap = new HashMap<>();
        List<ProgramTempKeywordsVo> programTempKeywordsVoList = JSONArray.parseArray(wechatProgramMyTemp.getExtra(), ProgramTempKeywordsVo.class);
        for (ProgramTempKeywordsVo programTempKeywordsVo : programTempKeywordsVoList) {
            if(StringUtils.isBlank(map.get(programTempKeywordsVo.getKey()))){
                continue;
            }
            hashMap.put(programTempKeywordsVo.getSendKey(), new SendProgramTemplateMessageItemVo(map.get(programTempKeywordsVo.getKey())));
        }

        programTemplateMessageVo.setData(hashMap);
        programTemplateMessageVo.setTouser(UserToken.getToken());
        redisUtil.lPush(Constants.WE_CHAT_MESSAGE_KEY_PROGRAM, JSONObject.toJSONString(programTemplateMessageVo));
    }

    /**
     * 同步我的模板到小程序
     * @param wechatProgramMyTemp WechatProgramMyTemp 需要同步的数据
     * @author Mr.Zhang
     * @since 2020-06-01
     * @return String 添加至帐号下的模板id，发送小程序订阅消息时所需
     */
    @Override
    @Async
    public void async(WechatProgramMyTemp wechatProgramMyTemp) {
        try{
            //删除微信已存在的数据
            deleteMyTemp(wechatProgramMyTemp.getTempId());

            //保存数据到微信
            String myTempId = addMyTemp(wechatProgramMyTemp);
            wechatProgramMyTemp.setTempId(myTempId);
            updateById(wechatProgramMyTemp);
        }catch (Exception e){
            //异常不处理
        }
    }

    /**
     * 查询所有的模板数据
     * @author Mr.Zhang
     * @since 2020-06-01
     * @return String 添加至帐号下的模板id，发送小程序订阅消息时所需
     */
    @Override
    public List<WechatProgramMyTemp> getAll() {
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setLimit(Constants.EXPORT_MAX_LIMIT);
        WechatProgramMyTempSearchRequest wechatProgramMyTempSearchRequest = new WechatProgramMyTempSearchRequest();
        wechatProgramMyTempSearchRequest.setStatus(true);
        return getList(wechatProgramMyTempSearchRequest, pageParamRequest);
    }

    /**
     * 更新应用场景
     * @param id Integer
     * @param type String
     * @author Mr.Zhang
     * @since 2020-08-28
     * @return true
     */
    @Override
    public boolean updateType(Integer id, String type) {
        WechatProgramMyTemp vo = info(id);
        vo.setType(type);
        updateById(vo);
        return true;
    }

    /**
     * 小程序我的模板添加
     * @param wechatProgramMyTemp WechatProgramMyTemp 添加我的模板参数
     * @author Mr.Zhang
     * @since 2020-06-01
     * @return String 添加至帐号下的模板id，发送小程序订阅消息时所需
     */
    private String addMyTemp(WechatProgramMyTemp wechatProgramMyTemp) {
        ProgramAddMyTempVo programAddMyTempVo = new ProgramAddMyTempVo(
                wechatProgramMyTemp.getTid(),
                CrmebUtil.stringToArray(wechatProgramMyTemp.getKid()),
                wechatProgramMyTemp.getSceneDesc()
        );
        String myTempId = weChatService.programAddMyTemp(programAddMyTempVo);
        if(StringUtils.isBlank(myTempId)){
            throw new CrmebException("微信没有返回模板id");
        }

        return myTempId;
    }

    /**
     * 小程序我的模板删除
     * @param myTempId String 我的模板id
     * @author Mr.Zhang
     * @since 2020-06-01
     */
    private void deleteMyTemp(String myTempId) {
        weChatService.programDeleteMyTemp(myTempId);
    }

    /**
     * 模板扩展字段 name转拼音，sendKey计算
     * @param wechatProgramMyTemp WechatProgramMyTemp 数据
     * @author Mr.Zhang
     * @since 2020-06-01
     */
    private void formatExtra(WechatProgramMyTemp wechatProgramMyTemp) {
        if(StringUtils.isBlank(wechatProgramMyTemp.getExtra())){
            throw new CrmebException("扩展字段必填");
        }

        HashMap<String, Integer> map = new HashMap<>();
        List<ProgramTempKeywordsVo> programTempKeywordsVoList = JSONArray.parseArray(wechatProgramMyTemp.getExtra(), ProgramTempKeywordsVo.class);
        if(programTempKeywordsVoList.size() < Constants.NUM_ONE){
            throw new CrmebException("扩展字段格式错误");
        }

        for (ProgramTempKeywordsVo programTempKeywordsVo : programTempKeywordsVoList) {
            programTempKeywordsVo.setSendKey(programTempKeywordsVo.getRule()+programTempKeywordsVo.getKid());
            programTempKeywordsVo.setKey(PinYinUtil.toPinyin(programTempKeywordsVo.getName())); //在代码里组织模板数据的时候map填充的key
        }

        wechatProgramMyTemp.setExtra(JSONObject.toJSONString(programTempKeywordsVoList));
    }
}

