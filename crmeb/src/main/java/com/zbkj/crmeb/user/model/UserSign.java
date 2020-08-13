package com.zbkj.crmeb.user.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 签到记录表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_sign")
@ApiModel(value="UserSign对象", description="签到记录表")
public class UserSign implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "签到说明")
    private String title;

    @ApiModelProperty(value = "获得积分")
    private Integer number;

    @ApiModelProperty(value = "剩余积分")
    private Integer balance;

    @ApiModelProperty(value = "类型，1积分，2经验")
    private Integer type;

    @ApiModelProperty(value = "签到日期")
    private Date createDay;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;


}
