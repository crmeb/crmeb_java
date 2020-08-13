package com.zbkj.crmeb.marketing.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 优惠券表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_coupon")
@ApiModel(value="StoreCouponSearchRequest对象", description="优惠券模板搜索")
public class StoreCouponSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "优惠券类型 1 手动领取, 2 新人券, 3 赠送券")
    private Integer type;

    @ApiModelProperty(value = "使用类型 1 全场通用, 2 商品券, 3 品类券")
    private Integer useType;

    @ApiModelProperty(value = "状态（0：关闭，1：开启）")
    private Boolean status;

    @ApiModelProperty(value = "是否删除 状态（0：否，1：是）")
    private Boolean isDel;

    @ApiModelProperty(value = "所属商品id / 分类id")
    private String primaryKey;

}
