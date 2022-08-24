package com.zbkj.common.exception;

/**
 *  Exception 拦截
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public enum ExceptionCodeEnum implements ExceptionHandler{
    // 数据操作错误定义
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    PRAM_NOT_MATCH(400, "参数不正确"),
    VALIDATE_FAILED(400, "参数检验失败"),
    UNAUTHORIZED(401, "未登录或token过期，请登录！"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "没有找到相关数据"),
    ERROR(500, "系统异常"),
    ;

    private long code;
    private String message;

    private ExceptionCodeEnum(long code, String message){
        this.code = code;
        this.message = message;
    }

    @Override
    public long getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
