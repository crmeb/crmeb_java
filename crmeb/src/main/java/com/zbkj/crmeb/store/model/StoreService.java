package com.zbkj.crmeb.store.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 客服表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-06-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_service")
@ApiModel(value="StoreService对象", description="客服表")
public class StoreService implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "客服id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "客服uid")
    private Integer uid;

    @ApiModelProperty(value = "客服头像")
    private String avatar;

    @ApiModelProperty(value = "代理名称")
    private String nickname;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "0隐藏1显示")
    private Boolean status;

    @ApiModelProperty(value = "订单通知1开启0关闭")
    private Integer notify;

    @ApiModelProperty(value = "是否展示统计管理")
    private Boolean customer;


}
