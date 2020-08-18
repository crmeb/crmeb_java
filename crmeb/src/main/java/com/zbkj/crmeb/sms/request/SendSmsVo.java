package com.zbkj.crmeb.sms.request;

import lombok.Data;

/**
 * @Classname sendSmsVo
 * @Description 短信发送api第三方参数实体类
 * @Date 2020/6/17 2:55 下午
 * @Created by stivepeim
 */
@Data
public class SendSmsVo {
    private String uid;
    private String token;

    // 待发送短信手机号
    private String mobile;

    // 模版id
    private Integer template;

    // 发送参数
    private String param;

    private String content;

}
