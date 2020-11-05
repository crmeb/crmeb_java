package com.zbkj.crmeb.store.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class StoreProductReplySearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户ID， 多个逗号分割")
    private String uid;

    @ApiModelProperty(value = "订单ID")
    private Integer oid;

    @ApiModelProperty(value = "商品id, 多个逗号分割")
    private String productId;

    @ApiModelProperty(value = "商品名称, 关键字，产品编号")
    private String productSearch;

    @ApiModelProperty(value = "0未删除1已删除")
    private Boolean isDel;

    @ApiModelProperty(value = "0未回复1已回复")
    private Boolean isReply;

    @ApiModelProperty(value = "用户名称(支持模糊搜索)")
    private String nickname;

    @ApiModelProperty(value = "时间区间")
    private String dateLimit;

    @ApiModelProperty(value = "类型")
    private int type;

    @ApiModelProperty(value = "星数")
    private int star = 0;
}
