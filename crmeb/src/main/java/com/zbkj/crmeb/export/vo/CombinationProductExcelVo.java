package com.zbkj.crmeb.export.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 拼团商品ExeclVo对象
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
public class CombinationProductExcelVo {

    @ApiModelProperty(value = "编号")
    private Integer id;

    @ApiModelProperty(value = "拼团名称")
    private String title;

    @ApiModelProperty(value = "原价")
    private BigDecimal otPrice;

    @ApiModelProperty(value = "拼团价")
    private BigDecimal price;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "拼团人数")
    private Integer countPeople;

    @ApiModelProperty(value = "参与人数")
    private Integer countPeopleAll;

    @ApiModelProperty(value = "成团数量")
    private Integer countPeoplePink;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "商品状态")
    private String isShow;

    @ApiModelProperty(value = "拼团结束时间")
    private String stopTime;
}
