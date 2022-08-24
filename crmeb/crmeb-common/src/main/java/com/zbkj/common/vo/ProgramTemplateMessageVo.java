package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.HashMap;

/**
 * 微信模板发送类
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
@ApiModel(value="SendTemplateMessageVo对象", description="微信模板发送类")
public class ProgramTemplateMessageVo {
    @ApiModelProperty(value = "OPENID", required = true)
    private String touser;

    @ApiModelProperty(value = "模板ID", required = true)
    private String template_id;

    @ApiModelProperty(value = "模板跳转链接小程序地址")
    private String page;

    @ApiModelProperty(value = "发送内容", required = true)
    private HashMap<String, SendProgramTemplateMessageItemVo> data;

    @ApiModelProperty(value = "跳转小程序类型：developer为开发版；trial为体验版；formal为正式版；默认为正式版")
    private String miniprogram_state = "formal";

    @ApiModelProperty(value = "进入小程序查看”的语言类型，支持zh_CN(简体中文)、en_US(英文)、zh_HK(繁体中文)、zh_TW(繁体中文)，默认为zh_CN\n")
    private String lang = "zh_CN";
}
