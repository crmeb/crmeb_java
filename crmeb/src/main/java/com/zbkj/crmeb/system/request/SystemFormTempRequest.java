package com.zbkj.crmeb.system.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 表单模板
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
@TableName("eb_system_form_temp")
@ApiModel(value="SystemFormTempRequest对象", description="表单模板")
public class SystemFormTempRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "表单名称", required = true)
    @NotBlank(message = "请填写表单名称")
    private String name;

    @ApiModelProperty(value = "表单简介", required = true)
    @NotBlank(message = "请填写表单简介")
    private String info;

    @ApiModelProperty(value = "表单内容", required = true)
    @NotBlank(message = "请填写表单内容")
    private String content;

}
