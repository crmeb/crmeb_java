package com.zbkj.crmeb.bargain.model;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 砍价用户帮助表
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
@TableName("eb_store_bargain_user_help")
@ApiModel(value="StoreBargainUserHelp对象", description="砍价用户帮助表")
public class StoreBargainUserHelp implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "砍价用户帮助表ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "帮助的用户id")
    private Integer uid;

    @ApiModelProperty(value = "砍价商品ID")
    private Integer bargainId;

    @ApiModelProperty(value = "用户参与砍价表id")
    private Integer bargainUserId;

    @ApiModelProperty(value = "帮助砍价多少金额")
    private BigDecimal price;

    @ApiModelProperty(value = "添加时间")
    private Long addTime;


}
