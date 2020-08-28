package com.zbkj.crmeb.sms;

import lombok.Data;

/**
 * @Classname SmsResultVo
 * @Description 短信发送状态同步 response 解析
 * @Date 8/28/20 2:52 下午
 * @Created by stivepeim
 */
@Data
public class SmsResultVo {
    private Integer resultcode;
    private String phone;
    private Integer id;
    private String _resultcode;
}
