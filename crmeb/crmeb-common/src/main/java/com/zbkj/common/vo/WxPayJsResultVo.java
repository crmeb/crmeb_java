package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 微信调起支付参数对象
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@ApiModel(value="WxPayJsResultVo对象", description="微信调起支付参数对象")
public class WxPayJsResultVo {

    @ApiModelProperty(value = "微信分配的小程序ID")
    private String appId;

    @ApiModelProperty(value = "随机字符串，不长于32位")
    private String nonceStr;

    @ApiModelProperty(value = "统一下单接口返回的 prepay_id 参数值")
    private String packages;

    @ApiModelProperty(value = "签名类型，默认为MD5，支持HMAC-SHA256和MD5。")
    private String signType;

    @ApiModelProperty(value = "时间戳从1970年1月1日00:00:00至今的秒数,即当前的时间")
    private String timeStamp;

    @ApiModelProperty(value = "支付签名")
    private String paySign;

    @ApiModelProperty(value = "H5支付跳转链接")
    private String mwebUrl;

    @ApiModelProperty(value = "微信商户号")
    private String partnerid;

    @ApiModelProperty(value = "拉起收银台的ticket")
    private String ticket;
}
