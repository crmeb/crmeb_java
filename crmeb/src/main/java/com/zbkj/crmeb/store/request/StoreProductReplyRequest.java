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
@ApiModel(value="StoreProductReply对象", description="评论表")
public class StoreProductReplyRequest implements Serializable {

    private static final long serialVersionUID=1L;

    private Integer id;

    @ApiModelProperty(value = "用户ID")
    private Integer uid;

    @ApiModelProperty(value = "订单ID")
    private Integer oid;

    @ApiModelProperty(value = "商品id", required = true)
    @Min(value = 1, message = "请选择商品")
    private Integer productId;

    @ApiModelProperty(value = "商品分数", example = "5", required = true)
    @Min(1)
    private Integer productScore;

    @ApiModelProperty(value = "服务分数", example = "5", required = true)
    @Min(1)
    private Integer serviceScore;

    @ApiModelProperty(value = "评论内容", required = true)
    @NotBlank(message = "请填写评论内容")
    private String comment;

    @ApiModelProperty(value = "评论图片", required = true)
    @NotBlank(message = "请上传评论图片")
    private String pics;

    @ApiModelProperty(value = "用户名称", required = true)
    @NotBlank(message = "请填写用户名")
    private String nickname;

    @ApiModelProperty(value = "用户头像", required = true)
    @NotBlank(message = "请上传头像")
    private String avatar;
}
