package com.zbkj.crmeb.front.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用户表 推广人 Request
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
@ApiModel(value="UserSpreadPeopleRequest对象", description="推广用户")
public class UserSpreadPeopleRequest implements Serializable {

    private static final long serialVersionUID=1L;


    @ApiModelProperty(value = "推荐人类型|0=一级|1=二级", allowableValues = "range[0,1]")
    @Range(min = 0, max = 1, message = "推荐人类型必须在 0（一级），1（二级） 中选择")
    private int grade = 0;

    @ApiModelProperty(value = "搜索关键字")
    private String keyword;

    @ApiModelProperty(value = "排序, 排序|childCount=团队排序,numberCount=金额排序,orderCount=订单排序", allowableValues = "range[childCount,numberCount,orderCount]")
    private String sortKey;

    @ApiModelProperty(value = "排序值 DESC ASC")
    private String isAsc = "DESC";
}
