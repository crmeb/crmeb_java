package com.zbkj.common.response;

import com.zbkj.common.model.product.StoreProductAttr;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 拼团商品响应体
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
public class CombinationDetailResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "拼团列表")
    private List<StorePinkResponse> pinkList;

    @ApiModelProperty(value = "拼团成功列表")
    private List<StorePinkResponse> pinkOkList;

    @ApiModelProperty(value = "拼团完成的商品总件数")
    private Integer pinkOkSum;

    @ApiModelProperty(value = "拼团商品信息")
    private CombinationDetailH5Response storeCombination;

    @ApiModelProperty(value = "商品规格")
    private List<StoreProductAttr> productAttr;

    @ApiModelProperty(value = "商品规格值")
    private HashMap<String,Object> productValue;

    @ApiModelProperty(value = "收藏标识")
    private Boolean userCollect;

    @ApiModelProperty(value = "主商品状态:normal-正常，sellOut-售罄，soldOut-下架,delete-删除")
    private String masterStatus;
}
