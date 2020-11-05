package com.zbkj.crmeb.front.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 推广用户
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
@ApiModel(value="UserSpreadPeopleResponse对象", description="推广用户")
public class UserSpreadPeopleResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "一级推广人人数")
    private Integer total = 0;

    @ApiModelProperty(value = "二级推广人人数")
    private Integer totalLevel = 0;

    @ApiModelProperty(value = "推广人列表")
    private List<UserSpreadPeopleItemResponse> spreadPeopleList;

    @ApiModelProperty(value = "推广人总人数")
    private Integer count = 0;
}
