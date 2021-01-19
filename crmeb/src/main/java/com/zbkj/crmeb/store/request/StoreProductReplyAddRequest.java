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
@ApiModel(value="StoreProductReplyAddRequest对象", description="评论表")
public class StoreProductReplyAddRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单ID， 移动端必须传递此参数")
    private Integer oid;

    @ApiModelProperty(value = "用户id， 后端必须传递此参数")
    private Integer userId;

    @ApiModelProperty(value = "商品id", required = true)
    @Min(value = 1, message = "请选择商品")
    private Integer productId;

    @ApiModelProperty(value = "商品 属性id")
    private String unique;

    @ApiModelProperty(value = "商品分数", example = "5", required = true)
    @Min(value = 1, message = "商品分数必须大于1")
    private Integer productScore;

    @ApiModelProperty(value = "服务分数", example = "5", required = true)
    @Min(value = 1, message = "服务分数必须大于1")
    private Integer serviceScore;

    @ApiModelProperty(value = "评论内容", required = true)
    @NotBlank(message = "请填写评论内容")
    private String comment;

    @ApiModelProperty(value = "评论图片", required = true)
    private String pics;

    @ApiModelProperty(value = "评论人头像 [虚拟评论参数]")
    private String avatar;

    @ApiModelProperty(value = "评论人昵称 [虚拟评论参数]")
    private String nickname;

    @ApiModelProperty(value = "商品规格属性值，多规格时用英文逗号拼接")
    private String sku;
}
