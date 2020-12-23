package com.zbkj.crmeb.user.request;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户表
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
@TableName("eb_user")
@ApiModel(value="UserRequest对象", description="用户表")
public class UserRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "真实姓名")
    @NotNull
    private String realName;

    @ApiModelProperty(value = "生日")
    @NotNull
    private String birthday;

    @ApiModelProperty(value = "身份证号码")
    @NotNull
    private String cardId;

    @ApiModelProperty(value = "用户备注")
    private String mark;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "状态是否正常， 0 = 禁止， 1 = 正常")
    @NotNull
    private Boolean status;

    @ApiModelProperty(value = "详细地址")
    private String addres;

    @ApiModelProperty(value = "等级")
    private Integer level;

    @ApiModelProperty(value = "用户分组id")
    private String groupId;

    @ApiModelProperty(value = "用户标签id")
    private String tagId;

    @ApiModelProperty(value = "是否为推广员")
    private Boolean isPromoter;

}
