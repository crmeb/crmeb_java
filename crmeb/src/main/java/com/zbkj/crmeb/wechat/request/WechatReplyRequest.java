package com.zbkj.crmeb.wechat.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 微信关键字回复表
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
@TableName("eb_wechat_reply")
@ApiModel(value="WechatReplyRequest对象", description="微信关键字回复表")
public class WechatReplyRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "关键字id")
    private Integer id;

    @ApiModelProperty(value = "关键字")
    @NotBlank(message = "请填写关键字, 关注 = subscribe， 默认 = default")
    private String keywords;

    @ApiModelProperty(value = "回复类型 text=文本  image =图片  news =图文  voice =音频"  , example = "text")
    @NotBlank(message = "请选择回复类型")
    private String type;

    @ApiModelProperty(value = "回复数据")
    @NotBlank(message = "请填写回复数据")
    private String data;

    @ApiModelProperty(value = "回复状态 0=不可用  1 =可用", example = "1")
    @NotNull(message = "请选择回复状态")
    private Boolean status;
}
