package com.zbkj.crmeb.user.model;

import com.baomidou.mybatisplus.annotation.TableLogic;
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
 * 用户地址表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_address")
@ApiModel(value="UserAddress对象", description="用户地址表")
public class UserAddress implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户地址id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "收货人姓名")
    private String realName;

    @ApiModelProperty(value = "收货人电话")
    private String phone;

    @ApiModelProperty(value = "收货人所在省")
    private String province;

    @ApiModelProperty(value = "收货人所在市")
    private String city;

    @ApiModelProperty(value = "城市id")
    private Integer cityId;

    @ApiModelProperty(value = "收货人所在区")
    private String district;

    @ApiModelProperty(value = "收货人详细地址")
    private String detail;

    @ApiModelProperty(value = "邮编")
    private Integer postCode;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "是否默认")
    private Boolean isDefault;

    @TableLogic
    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;

    @ApiModelProperty(value = "创建时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;


}
