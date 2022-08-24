package com.zbkj.common.model.sms;

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
 * 短信模板表
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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_sms_template")
@ApiModel(value="SmsTemplate对象", description="短信模板表")
public class SmsTemplate implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "短信模板id")
    private String tempId;

    @ApiModelProperty(value = "模板类型")
    private Integer tempType;

    @ApiModelProperty(value = "模板说明")
    private String title;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "模板编号")
    private String tempKey;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "短息内容")
    private String content;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;


}
