package com.zbkj.crmeb.wechat.service;

import com.alibaba.fastjson.JSONObject;
import com.zbkj.crmeb.front.response.UserRechargePaymentResponse;
import com.zbkj.crmeb.payment.vo.wechat.CreateOrderResponseVo;
import com.zbkj.crmeb.wechat.response.WeChatAuthorizeLoginGetOpenIdResponse;
import com.zbkj.crmeb.wechat.response.WeChatAuthorizeLoginUserInfoResponse;
import com.zbkj.crmeb.wechat.response.WeChatProgramAuthorizeLoginGetOpenIdResponse;
import com.zbkj.crmeb.wechat.vo.*;

import java.util.HashMap;
import java.util.List;

/**
 * WeChatPublicService 接口
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
public interface WeChatService {

    JSONObject get();

    JSONObject create(String data);

    JSONObject delete();

    JSONObject getSelf();

    JSONObject createSelf(String data);

    JSONObject deleteSelf(String menuId);

    void pushKfMessage(HashMap<String, Object> map);

    JSONObject createTags(String name);

    JSONObject getTagsList();

    JSONObject updateTags(String id, String name);

    JSONObject deleteTags(String id);

    JSONObject getUserListByTagsId(String id, String nextOpenId);

    JSONObject memberBatchTags(String id, String data);

    JSONObject memberBatchUnTags(String id, String data);

    JSONObject getTagsListByUserId(String openId);

    String getAuthorizeUrl();

    WeChatAuthorizeLoginGetOpenIdResponse authorizeLogin(String code);

    WeChatAuthorizeLoginUserInfoResponse getUserInfo(String openId, String token);

    Object getJsSdkConfig(String url);

    boolean sendPublicTempMessage(TemplateMessageVo templateMessage);

    boolean sendProgramTempMessage(TemplateMessageVo templateMessage);

    JSONObject getIndustry();

    String getUploadMedia();

    String getMedia();

    JSONObject getMediaInfo(String type, int offset, int count);

    int getMediaCount(String type);

    WeChatProgramAuthorizeLoginGetOpenIdResponse programAuthorizeLogin(String code);

    String qrCode(String page, String uri);

    UserRechargePaymentResponse response(CreateOrderResponseVo responseVo);

    List<ProgramCategoryVo> getProgramCategory();

    List<ProgramTempVo> getProgramPublicTempList(int page);

    List<ProgramTempKeywordsVo> getWeChatKeywordsByTid(Integer tid);

    String programAddMyTemp(ProgramAddMyTempVo programAddMyTempVo);

    void programDeleteMyTemp(String myTempId);
}