package com.zbkj.common.response;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 微信公众号js-sdk响应对象
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
@ApiModel(value="WeChatJsSdkConfigResponse对象", description="微信公众号js-sdk响应对象对象")
public class WeChatJsSdkConfigResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "url")
    private String url;

    @ApiModelProperty(value = "jsApiTicket")
    private String jsApiTicket;

    @ApiModelProperty(value = "nonceStr")
    private String nonceStr;

    @ApiModelProperty(value = "timestamp")
    private Long timestamp;

    @ApiModelProperty(value = "signature")
    private String signature;

    @ApiModelProperty(value = "jsApiList")
    private List<String> jsApiList;

    @ApiModelProperty(value = "debug")
    private Boolean debug;

    @ApiModelProperty(value = "appid")
    private String appId;
}
