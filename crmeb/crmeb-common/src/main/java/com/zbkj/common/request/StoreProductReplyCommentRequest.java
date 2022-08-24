package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 回复商品评论对象
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
@ApiModel(value="StoreProductReplyCommentRequest对象", description="回复商品评论对象")
public class StoreProductReplyCommentRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "评论id", required = true)
    @NotNull(message = "评论id不能为空")
    private Integer ids;

    @ApiModelProperty(value = "管理员回复内容", required = true)
    @NotBlank(message = "请填写评论内容")
    private String merchantReplyContent;
}
