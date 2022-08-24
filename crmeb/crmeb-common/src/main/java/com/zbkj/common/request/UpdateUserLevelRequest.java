package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 更新用户会员等级
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
@ApiModel(value="UpdateUserLevelRequest", description="更新用户会员等级对象")
public class UpdateUserLevelRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "uid")
    @NotNull(message = "用户id不能为空")
    private Integer uid;

    @ApiModelProperty(value = "会员等级")
    @NotNull(message = "等级id不能为空")
    private Integer levelId;

    @ApiModelProperty(value = "是否扣减积分，true-扣减，false-不扣减")
    private Boolean isSub;

}
