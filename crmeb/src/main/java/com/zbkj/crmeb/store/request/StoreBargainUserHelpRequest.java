package com.zbkj.crmeb.store.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author stivepeim
 * @title: StoreBargainUserHelpRequest
 * @projectName crmeb
 * @description: TODO
 * @date 2020/5/2815:39
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_bargain_user_help")
@ApiModel(value="StoreBargainUserHelp对象", description="砍价用户帮助表")
public class StoreBargainUserHelpRequest {
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
    private Integer addTime;
}
