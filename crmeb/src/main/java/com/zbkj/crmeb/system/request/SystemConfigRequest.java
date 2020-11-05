package com.zbkj.crmeb.system.request;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zbkj.crmeb.system.vo.SystemConfigFormVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 配置表
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
@ApiModel(value="SystemConfigRequest对象", description="配置表")
public class SystemConfigRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "字段名称", required = true)
    @NotBlank(message = "请填写字段名称")
    private String name;

    @ApiModelProperty(value = "父类id")
    @Min(value = 0, message = "请选择分类")
    private Integer pid;

    @ApiModelProperty(value = "分组id", example = "0")
    private Integer groupId;

    @ApiModelProperty(value = "应用类型")
    @Min(value = 0, message = "请选择应用类型")
    private Integer type;

    @ApiModelProperty(value = "值")
    private SystemConfigFormVo value;

    @ApiModelProperty(value = "配置简介")
    private String info;

    @ApiModelProperty(value = "是否隐藏")
    private Boolean status;

    @ApiModelProperty(value = "配置简介")
    private String desc;
}
