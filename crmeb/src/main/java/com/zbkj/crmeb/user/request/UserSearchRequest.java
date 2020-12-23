package com.zbkj.crmeb.user.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
@ApiModel(value="User对象", description="用户表")
public class UserSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;


    @ApiModelProperty(value = "关键字")
    private String keywords;

    @ApiModelProperty(value = "时间")
    private String dateLimit;

    @ApiModelProperty(value = "用户分组")
    private String groupId;

    @ApiModelProperty(value = "用户标签")
    private String labelId;

    @ApiModelProperty(value = "用户登陆类型，h5 = h5， wechat = wechat，routine = routine", allowableValues = "range[h5,wechat,routine]")
    @NotBlank(message = "请选择用户登录类型")
    private String userType;

    @ApiModelProperty(value = "状态是否正常， 0 = 禁止， 1 = 正常")
    private Boolean status = null;

    @ApiModelProperty(value = "是否为推广员， 0 = 禁止， 1 = 正常")
    private Boolean isPromoter = null;

    @ApiModelProperty(value = "消费情况")
    private String payCount;

    @ApiModelProperty(value = "等级")
    private String level;

    //时间类型
    @ApiModelProperty(value = "访问情况， 0 = 全部， 1 = 首次， 2 = 访问过， 3 = 未访问", allowableValues = "range[0,1,2,3]")
    @NotNull(message = "访问情况不能为空")
    private Integer accessType = 0;

    @ApiModelProperty(value = "国家，中国CN，其他OTHER")
    private String country;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "性别，0未知，1男，2女，3保密")
    private String sex;
}
