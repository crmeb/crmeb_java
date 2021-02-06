package com.zbkj.crmeb.wechat.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 模板消息查询请求体
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
public class TemplateMessageSearchRequest {

    @ApiModelProperty(value = "模板名")
    private String name;

    @ApiModelProperty(value = "模板编号")
    private String tempKey;

    @ApiModelProperty(value = "模板ID")
    private String tempId;

    @ApiModelProperty(value = "状态, 0 正常，1 禁用")
    private Integer status = null;

    @ApiModelProperty(value = "0=订阅消息,1=微信模板消息")
    private Boolean type;
}
