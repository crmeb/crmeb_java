package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户更新请求对象
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
@ApiModel(value="UserUpdateRequest", description="用户更新请求对象")
public class UserUpdateRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "uid")
    private Integer uid;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "生日")
    private String birthday;

    @ApiModelProperty(value = "身份证号码")
    private String cardId;

    @ApiModelProperty(value = "用户备注")
    private String mark;

    @ApiModelProperty(value = "状态是否正常， 0 = 禁止， 1 = 正常")
    @NotNull(message = "状态不能为空")
    private Boolean status;

    @ApiModelProperty(value = "详细地址")
    private String addres;

//    @ApiModelProperty(value = "等级")
//    private Integer level;

    @ApiModelProperty(value = "用户分组id")
    private String groupId;

    @ApiModelProperty(value = "用户标签id")
    private String tagId;

    @ApiModelProperty(value = "是否为推广员")
    @NotNull(message = "是否为推广员不能为空")
    private Boolean isPromoter;

}
