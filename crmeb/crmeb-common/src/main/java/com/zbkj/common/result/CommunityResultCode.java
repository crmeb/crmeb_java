package com.zbkj.common.result;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.exception.BusinessExceptionAssert;

/**
 * @ClassName CommunityResultCode
 * @Description 社区模块响应状态码
 * @Author HZW
 * @Date 2023/3/7 12:20
 * @Version 1.0
 */
public enum CommunityResultCode implements BusinessExceptionAssert {

    COMMUNITY_CATEGORY_EXIST(9101, "社区分类已存在"),
    COMMUNITY_CATEGORY_NOT_EXIST(9102, "社区分类不存在"),
    COMMUNITY_CATEGORY_ID_NULL(9103, "请选择社区分类"),
    COMMUNITY_CATEGORY_USING(9104, "社区分类使用中，请先删除关联的社区内容"),

    COMMUNITY_TOPIC_NAME_EXIST(9201, "社区话题已存在"),
    COMMUNITY_TOPIC_NOT_EXIST(9202, "社区话题不存在"),
    COMMUNITY_TOPIC_ID_NULL(9203, "请选择社区话题"),
    COMMUNITY_TOPIC_RECOMMEND_NUM(9204, "社区话题推荐数量达到上限"),

    COMMUNITY_NOTE_NOT_EXIST(9301, "社区笔记不存在"),
    COMMUNITY_NOTE_AUDIT_STATUS_EXCEPTION(9302, "社区笔记审核状态异常"),
    COMMUNITY_NOTE_AUTHOR_NOTE_EXIST(9303, "社区笔记作者不存在"),
    COMMUNITY_NOTE_REPLY_IS_NOT_OPEN(9304, "社区笔记评论未打开"),

    COMMUNITY_REPLY_NOT_EXIST(9401, "社区评论不存在"),
    COMMUNITY_REPLY_AUDIT_STATUS_EXCEPTION(9302, "社区评论审核状态异常"),
    COMMUNITY_REPLY_PLAT_CLOSE(9304, "社区评论已关闭"),
    ;

    CommunityResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 响应状态码
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String message;
    /**
     * 响应信息补充
     */
    private Object[] msgParams;


    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public CommunityResultCode setCode(Integer code) {
        this.code = code;
        return this;
    }

    @Override
    public String getMessage() {
        if (ArrayUtil.isNotEmpty(msgParams)) {
            return StrUtil.format(message, msgParams);
        }
        return message;
    }

    @Override
    public CommunityResultCode setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public IResultEnum setMsgParams(Object... msgParams) {
        this.msgParams = msgParams;
        return this;
    }

    @Override
    public Object[] getMsgParams() {
        return msgParams;
    }
}
