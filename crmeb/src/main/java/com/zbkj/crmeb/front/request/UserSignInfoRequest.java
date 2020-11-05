package com.zbkj.crmeb.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用户表 信息Request
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserSignInfoRequest对象", description="个人签到信息")
public class UserSignInfoRequest implements Serializable {

    private static final long serialVersionUID=1L;


    @ApiModelProperty(value = "是否统计签到|1=是,0=否")
    private Boolean sign = false;

    @ApiModelProperty(value = "是否统计积分使用情况|1=是,0=否")
    private Boolean integral = false;

    @ApiModelProperty(value = "是否统计签到和统计积分使用情况|1=是,0=否")
    private Boolean all = false;

}
