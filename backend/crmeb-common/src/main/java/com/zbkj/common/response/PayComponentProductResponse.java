package com.zbkj.common.response;

import com.zbkj.common.model.wechat.video.PayComponentProductSku;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 组件商品表
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2024 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="PayComponentProductResponse对象", description="组件商品详情响应对象")
public class PayComponentProductResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "主键ID，商家自定义商品ID")
    private Integer id;

    @ApiModelProperty(value = "主商品ID")
    private Integer primaryProductId;

    @ApiModelProperty(value = "交易组件平台内部商品ID")
    private Integer componentProductId;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "绑定的小程序商品路径")
    private String path;

    @ApiModelProperty(value = "轮播图,多张")
    private String headImg;

    @ApiModelProperty(value = "商品资质图片,多张")
    private String qualificationPics;

    @ApiModelProperty(value = "第三级类目ID")
    private Integer thirdCatId;

    @ApiModelProperty(value = "品牌id")
    private Integer brandId;

    @ApiModelProperty(value = "线上状态：0-初始值，5-上架，11-自主下架，13-违规下架/风控系统下架")
    private Integer status;

    @ApiModelProperty(value = "商品草稿状态:1-未审核，2-审核中，3-审核失败，4-审核成功")
    private Integer editStatus;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

    @ApiModelProperty(value = "运费模板ID")
    private Integer tempId;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "销量")
    private Integer sales;

    @ApiModelProperty(value = "单位名称")
    private String unitName;

    @ApiModelProperty(value = "反多少积分")
    private Integer giveIntegral;

    @ApiModelProperty(value = "规格 0单 1多")
    private Boolean specType;

    @ApiModelProperty(value = "虚拟销量")
    private Integer ficti;

    @ApiModelProperty(value = "是否单独分佣")
    private Boolean isSub;

    @ApiModelProperty(value = "sku数组")
    private List<PayComponentProductSku> skuList;
}
