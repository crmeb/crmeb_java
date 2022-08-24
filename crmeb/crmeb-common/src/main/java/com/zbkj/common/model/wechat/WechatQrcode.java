package com.zbkj.common.model.wechat;

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
 * 微信二维码管理表
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
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
public class WechatQrcode implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "微信二维码ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

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

    @ApiModelProperty(value = "微信访问url")
    private String url;

    @ApiModelProperty(value = "微信二维码url")
    private String qrcodeUrl;

    @ApiModelProperty(value = "被扫的次数")
    private Integer scan;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;


}
