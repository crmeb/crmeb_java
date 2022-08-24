package com.zbkj.common.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 拼团列表响应体
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
public class StorePinkAdminListResponse {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "拼团ID")
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "拼团总人数")
    private Integer people;

    @ApiModelProperty(value = "开始时间")
    private String addTime;

    @ApiModelProperty(value = "结束时间")
    private String stopTime;

    @ApiModelProperty(value = "团长id 0为团长")
    private Integer kId;

    @ApiModelProperty(value = "状态1进行中2已完成3未完成")
    private Integer status;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "几人参团")
    private Integer countPeople;

    @ApiModelProperty(value = "拼团商品")
    private String title;
}
