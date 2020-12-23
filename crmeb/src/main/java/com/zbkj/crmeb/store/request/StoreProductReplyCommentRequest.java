package com.zbkj.crmeb.store.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 评论表
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
@TableName("eb_store_product_reply")
@ApiModel(value="StoreProductReplyCommentRequest对象", description="评论表")
public class StoreProductReplyCommentRequest implements Serializable {

    private static final long serialVersionUID=1L;
    @ApiModelProperty(value = "管理员回复内容", required = true)
    @NotBlank(message = "ids不能为空")
    private String ids;

    @ApiModelProperty(value = "管理员回复内容", required = true)
    @NotBlank(message = "请填写评论内容")
    private String merchantReplyContent;
}
