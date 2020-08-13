package com.zbkj.crmeb.user.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户等级记录表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_level")
@ApiModel(value="UserLevel对象", description="用户等级记录表")
public class UserLevelRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "等级vip")
    private Integer levelId;

    @ApiModelProperty(value = "会员等级")
    private Integer grade;

    @ApiModelProperty(value = "过期时间")
    private Integer validTime;

    @ApiModelProperty(value = "是否永久")
    private Boolean isForever;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "0:禁止,1:正常")
    private Boolean status;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "是否已通知")
    private Boolean remind;

    @ApiModelProperty(value = "是否删除,0=未删除,1=删除")
    private Boolean isDel;

    @ApiModelProperty(value = "添加时间")
    private Integer addTime;

    @ApiModelProperty(value = "享受折扣")
    private Integer discount;


}
