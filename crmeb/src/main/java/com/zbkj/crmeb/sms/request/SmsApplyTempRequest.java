package com.zbkj.crmeb.sms.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 申请短信模板请求对象
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
@ApiModel(value="SmsApplyTempRequest对象", description="申请短信模板请求对象")
public class SmsApplyTempRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "模板标题")
    @NotBlank(message = "模板标题不能为空")
    private String title;

    @ApiModelProperty(value = "模板内容(仅内容，不需要签名)")
    @NotBlank(message = "模板内容不能为空")
    private String content;

    @ApiModelProperty(value = "模板类型：1验证码，2通知，3推广")
    @NotNull(message = "模板类型不能为空")
    @Range(min = 1, max = 3, message = "未知的模板类型")
    private Integer type;

}
