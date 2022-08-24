package com.zbkj.admin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.zbkj.common.constants.WeChatConstants;
import com.zbkj.common.utils.DateUtil;
import com.zbkj.common.utils.RedisUtil;
import com.zbkj.common.utils.XmlUtil;
import com.zbkj.common.model.article.Article;
import com.zbkj.admin.service.WeChatMessageService;
import com.zbkj.admin.vo.*;
import com.zbkj.common.model.wechat.WechatReply;
import com.zbkj.service.service.ArticleService;
import com.zbkj.service.service.SystemAttachmentService;
import com.zbkj.service.service.UserTokenService;
import com.zbkj.service.service.WechatReplyService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * 用户中心 服务实现类
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
@Data
@Service
public class WeChatMessageServiceImpl implements WeChatMessageService {
    private static final Logger logger = LoggerFactory.getLogger(WeChatMessageServiceImpl.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WechatReplyService wechatReplyService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    //接收人，被动回复消息的时候为发送人
    private String toUserName;

    //发送人，被动回复消息的时候为接收人
    private String fromUserName;

    //接收消息类型
    private String msgType;

    //接收消息内容
    private String content;

    //消息事件
    private String event;

    //事件key
    private String eventKey;

    //关键字回复对象
    private WechatReply wechatReply;


    /**
     * 处理微信推送过来的消息，并且组装成需要发送的数据，二次处理
     * @param request HttpServletRequest request请求
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return String
     */
    @Override
    public String init(HttpServletRequest request) {
        Map<String, String> map = XmlUtil.xmlToMap(request);

        setToUserName(map.get("ToUserName"));
        setFromUserName(map.get("FromUserName"));
        setMsgType(map.getOrDefault("MsgType", "text")); //如果没有类型，则按默认处理
        setContent(map.getOrDefault("Content", "default")); //如果没有内容，则按默认处理
        setEvent(map.getOrDefault("Event", ""));
        setEventKey(map.getOrDefault("EventKey", ""));



        //处理内容
        getReplyByContent();

        if(null == getWechatReply()){
            return "";
        }

        //设置需要回复的内容
        String response = setXml();

        logger.info("微信被动回复消息" + response);
        return response;
    }

    /**
     * 匹配关键字并且组装xml数据
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return String
     */
    private String setXml() {
        if(StringUtils.isBlank(getWechatReply().getType())){
            return "";
        }
        String type = getWechatReply().getType().toLowerCase();
        MessageReplyDataVo messageReplyDataVo = JSONObject.toJavaObject(JSONObject.parseObject(wechatReply.getData()), MessageReplyDataVo.class);

        switch (type){
            case WeChatConstants.WE_CHAT_MESSAGE_RESP_MESSAGE_TYPE_TEXT:
                MessageTextVo messageTextVo = new MessageTextVo(getFromUserName(), getToUserName(), messageReplyDataVo.getContent());
                return XmlUtil.objectToXml(messageTextVo);
            case WeChatConstants.WE_CHAT_MESSAGE_RESP_MESSAGE_TYPE_VOICE:
                return XmlUtil.objectToXml(new MessageVoiceVo(getFromUserName(), getToUserName(), new MessageVoiceItemVo(messageReplyDataVo.getMediaId())));
            case WeChatConstants.WE_CHAT_MESSAGE_RESP_MESSAGE_TYPE_IMAGE:
                return XmlUtil.objectToXml(new MessageImageVo(getFromUserName(), getToUserName(), new MessageImageItemVo(messageReplyDataVo.getMediaId())));
            case WeChatConstants.WE_CHAT_MESSAGE_RESP_MESSAGE_TYPE_NEWS:
                //文章
                return getNews(messageReplyDataVo.getArticleId());
            default:
                return "";
        }
    }

    /**
     * 组装文章消息
     * @param articleId Integer 文章id
     * @author Mr.Zhang
     * @since 2020-06-03
     * @return MessageVoiceVo
     */
    private String getNews(Integer articleId) {
        Article article = articleService.getById(articleId);
        if(null == article || article.getStatus() || article.getHide()){
            return "";
        }

        return "<xml>\n" +
                "  <ToUserName><![CDATA["+fromUserName+"]]></ToUserName>\n" +
                "  <FromUserName><![CDATA["+toUserName+"]]></FromUserName>\n" +
                "  <CreateTime>"+ DateUtil.getNowTime() +"</CreateTime>\n" +
                "  <MsgType><![CDATA[news]]></MsgType>\n" +
                "  <ArticleCount>1</ArticleCount>\n" +
                "  <Articles>\n" +
                "    <item>\n" +
                "      <Title><![CDATA["+article.getTitle()+"]]></Title>\n" +
                "      <Description><![CDATA["+article.getShareSynopsis()+"]]></Description>\n" +
                "      <PicUrl><![CDATA["+article.getImageInput()+"]]></PicUrl>\n" +
                "      <Url><![CDATA["+article.getUrl()+"]]></Url>\n" +
                "    </item>\n" +
                "  </Articles>\n" +
                "</xml>\n";
    }


    /**
     * 处理不同的消息类型
     * @author Mr.Zhang
     * @since 2020-06-03
     */
    private void getReplyByContent() {
        WechatReply wp = new WechatReply();
        switch (getMsgType()){
            case WeChatConstants.WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_EVENT:
                if(StringUtils.isBlank(getEvent())){
                    break;
                }
                switch (getEvent().toLowerCase()){
                    case WeChatConstants.WE_CHAT_MESSAGE_EVENT_TYPE_UNSUBSCRIBE:
                    case WeChatConstants.WE_CHAT_MESSAGE_EVENT_TYPE_SCAN:
                    case WeChatConstants.WE_CHAT_MESSAGE_EVENT_TYPE_LOCATION:
                    case WeChatConstants.WE_CHAT_MESSAGE_EVENT_TYPE_VIEW:
                        //暂时不处理
                        break;
                    case WeChatConstants.WE_CHAT_MESSAGE_EVENT_TYPE_SUBSCRIBE:
                        wp = wechatReplyService.getVoByKeywords(WeChatConstants.WE_CHAT_MESSAGE_EVENT_TYPE_SUBSCRIBE.toLowerCase());
                        break;
                    case WeChatConstants.WE_CHAT_MESSAGE_EVENT_TYPE_CLICK:
                        wp = wechatReplyService.getVoByKeywords(eventKey);
                        break;

                }
                break;
            case WeChatConstants.WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_TEXT:
                wp = wechatReplyService.getVoByKeywords(getContent());
                break;
            case WeChatConstants.WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_IMAGE:
            case WeChatConstants.WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_VOICE:
            case WeChatConstants.WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_VIDEO:
            case WeChatConstants.WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_LOCATION:
            case WeChatConstants.WE_CHAT_MESSAGE_REQ_MESSAGE_TYPE_LINK:
                //不需要处理
                break;
            default:
                break;
        }

        if(null == wp){
            //无效关键字回复
            wp = wechatReplyService.getVoByKeywords(WeChatConstants.WE_CHAT_MESSAGE_DEFAULT_CONTENT_KEY);
        }
        setWechatReply(wp);
    }
}
