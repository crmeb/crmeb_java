package com.zbkj.crmeb.sms.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 短信发送记录表
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
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_sms_record")
@ApiModel(value="SmsRecord对象", description="短信发送记录表")
public class SmsRecord implements Serializable {

    private static final long serialVersionUID=1L;

    public SmsRecord() {

    }

    public SmsRecord(String uid, String phone, String content, String addIp, String template, Integer resultcode, Integer recordId, String message) {
        this.uid = uid;
        this.phone = phone;
        this.content = content;
        this.addIp = addIp;
        this.template = template;
        this.resultcode = resultcode;
        this.recordId = recordId;
        this.memo = message;
    }

    @ApiModelProperty(value = "短信发送记录编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "短信平台账号")
    private String uid;

    @ApiModelProperty(value = "接受短信的手机号")
    private String phone;

    @ApiModelProperty(value = "短信内容")
    private String content;

    @ApiModelProperty(value = "添加记录ip")
    private String addIp;

    @ApiModelProperty(value = "短信模板ID")
    private String template;

    @ApiModelProperty(value = "状态码 100=成功,130=失败,131=空号,132=停机,133=关机,134=无状态")
    private Integer resultcode;

    @ApiModelProperty(value = "发送记录id")
    private Integer recordId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "备注")
    private String memo;
}
