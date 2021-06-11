package com.zbkj.crmeb.system.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 设置用户等级表
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
@TableName("eb_system_user_level")
@ApiModel(value="SystemUserLevelRequest对象", description="设置用户等级表")
public class SystemUserLevelRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "等级id")
    private Integer id;

    @ApiModelProperty(value = "等级名称")
    @NotBlank(message = "等级名称不能为空")
    private String name;

    @ApiModelProperty(value = "达到多少升级经验")
    @NotNull(message = "等级经验不能为空")
    private Integer experience;

    @ApiModelProperty(value = "会员等级")
    @NotNull(message = "会员等级不能为空")
    @Min(value = 1)
    private Integer grade;

    @ApiModelProperty(value = "享受折扣")
    @NotNull(message = "折扣不能为空")
    @DecimalMin(value = "0.00", message = "请输入正确的金额")
    private BigDecimal discount;

    @ApiModelProperty(value = "会员图标")
    @NotBlank(message = "会员图标不能为空")
    private String icon;

    @ApiModelProperty(value = "会员卡背景")
//    @NotBlank(message = "会员卡背景不能为空")
    private String image;

    @ApiModelProperty(value = "是否显示 1=显示,0=隐藏")
    @NotNull(message = "是否现实不能为空")
    private Boolean isShow;

    @ApiModelProperty(value = "说明")
//    @NotNull(message = "说明不能为空")
    private String memo;
}
