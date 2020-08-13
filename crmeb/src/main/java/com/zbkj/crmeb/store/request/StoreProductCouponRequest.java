package com.zbkj.crmeb.store.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author stivepeim
 * @title: StoreProductCouponRequest
 * @projectName crmeb
 * @description: 产品和优惠券关联
 * @date 2020/8/711:07
 */

@Data
public class StoreProductCouponRequest {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "优惠劵id")
    private Integer issueCouponId;

    @ApiModelProperty(value = "添加时间")
    private Integer addTime;
}
