package com.zbkj.crmeb.wechat.request;

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
 * 微信二维码管理表
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
@TableName("eb_wechat_qrcode")
@ApiModel(value="WechatQrcode对象", description="微信二维码管理表")
public class WechatQrcodeSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "二维码类型")
    private String thirdType;

    @ApiModelProperty(value = "用户id")
    private Integer thirdId;

    @ApiModelProperty(value = "二维码参数")
    private String ticket;

    @ApiModelProperty(value = "二维码有效时间")
    private Integer expireSeconds;

    @ApiModelProperty(value = "状态")
    private Boolean status;

    @ApiModelProperty(value = "添加时间")
    private String addTime;

    @ApiModelProperty(value = "微信访问url")
    private String url;

    @ApiModelProperty(value = "微信二维码url")
    private String qrcodeUrl;

    @ApiModelProperty(value = "被扫的次数")
    private Integer scan;


}
