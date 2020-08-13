package com.zbkj.crmeb.sms.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 短信发送记录表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_sms_record")
@ApiModel(value="SmsRecord对象", description="短信发送记录表")
public class SmsRecordRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "短信平台账号")
    private String uid;

    @ApiModelProperty(value = "接受短信的手机号")
    private String phone;

    @ApiModelProperty(value = "状态码 null=全部,100=成功,130=失败,131=空号,132=停机,133=关机,134=无状态, 134=无状态, 400黑名单")
    private Integer resultCode;

    @ApiModelProperty(value = "发送记录id")
    private Integer recordId;


}
