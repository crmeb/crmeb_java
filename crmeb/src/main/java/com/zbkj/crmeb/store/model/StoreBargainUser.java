package com.zbkj.crmeb.store.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户参与砍价表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_bargain_user")
@ApiModel(value="StoreBargainUser对象", description="用户参与砍价表")
public class StoreBargainUser implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户参与砍价表ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    private Integer uid;

    @ApiModelProperty(value = "砍价商品id")
    private Integer bargainId;

    @ApiModelProperty(value = "砍价的最低价")
    private BigDecimal bargainPriceMin;

    @ApiModelProperty(value = "砍价金额")
    private BigDecimal bargainPrice;

    @ApiModelProperty(value = "砍掉的价格")
    private BigDecimal price;

    @ApiModelProperty(value = "状态 1参与中 2 活动结束参与失败 3活动结束参与成功")
    private Boolean status;

    @ApiModelProperty(value = "参与时间")
    private Integer addTime;

    @ApiModelProperty(value = "是否取消")
    private Boolean isDel;


}
