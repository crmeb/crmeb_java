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
 * 拼团表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_pink")
@ApiModel(value="StorePink对象", description="拼团表")
public class StorePink implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "订单id 生成")
    private String orderId;

    @ApiModelProperty(value = "订单id  数据库")
    private Integer orderIdKey;

    @ApiModelProperty(value = "购买商品个数")
    private Integer totalNum;

    @ApiModelProperty(value = "购买总金额")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "拼团商品id")
    private Integer cid;

    @ApiModelProperty(value = "商品id")
    private Integer pid;

    @ApiModelProperty(value = "拼图总人数")
    private Integer people;

    @ApiModelProperty(value = "拼团商品单价")
    private BigDecimal price;

    @ApiModelProperty(value = "开始时间")
    private String addTime;

    private String stopTime;

    @ApiModelProperty(value = "团长id 0为团长")
    private Integer kId;

    @ApiModelProperty(value = "是否发送模板消息0未发送1已发送")
    private Boolean isTpl;

    @ApiModelProperty(value = "是否退款 0未退款 1已退款")
    private Boolean isRefund;

    @ApiModelProperty(value = "状态1进行中2已完成3未完成")
    private Boolean status;


}
