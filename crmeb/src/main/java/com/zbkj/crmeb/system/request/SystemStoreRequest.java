package com.zbkj.crmeb.system.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.constants.RegularConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 门店自提
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
@ApiModel(value="SystemStoreRequest对象", description="提货点")
public class SystemStoreRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "门店名称")
    @NotBlank(message = "请填写门店名称")
    private String name;

    @ApiModelProperty(value = "简介")
    private String introduction;

    @ApiModelProperty(value = "手机号码")
    @Pattern(regexp = RegularConstants.PHONE, message = "手机号码格式错误")
    private String phone;

    @ApiModelProperty(value = "提货点地址省市区")
    @NotBlank(message = "提货点地址")
    private String address;

    @ApiModelProperty(value = "详细地址")
    @NotBlank(message = "请填写详细地址")
    private String detailedAddress;

    @ApiModelProperty(value = "每日营业开关时间")
    private String dayTime;

    @ApiModelProperty(value = "门店logo")
    @NotBlank(message = "请上传门店logo")
    private String image;

    @ApiModelProperty(value = "纬度")
    @NotBlank(message = "请选择经纬度")
    private String latitude;

    @ApiModelProperty(value = "经度", example = "1,2")
    private String longitude;

    @ApiModelProperty(value = "核销有效日期")
    private String validTime;
}
