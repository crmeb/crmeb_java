package com.zbkj.crmeb.front.response;

import com.zbkj.crmeb.marketing.model.StoreCoupon;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 首页信息Response
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="IndexInfoResponse对象", description="用户登录返回数据")
public class IndexInfoResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "首页banner滚动图")
    private List<HashMap<String, Object>> banner;

    @ApiModelProperty(value = "导航模块")
    private List<HashMap<String, Object>> menus;

    @ApiModelProperty(value = "新闻简报消息滚动")
    private List<HashMap<String, Object>> roll;

//    @ApiModelProperty(value = "活动")
//    private IndexInfoItemResponse info;

    // 待优化
//    @ApiModelProperty(value = "活动区域图片")
//    private List<HashMap<String, Object>> activity;

//    @ApiModelProperty(value = "首发新品广告图")
//    private HashMap<String, Object> lovely;

//    @ApiModelProperty(value = "首页促销单品")
//    private List<ProductResponse> benefit;

//    @ApiModelProperty(value = "热门榜单")
//    private List<ProductResponse> likeInfo;

    @ApiModelProperty(value = "企业logo")
    private String logoUrl;

    @ApiModelProperty(value = "优惠券")
    private List<StoreCoupon> couponList;

    @ApiModelProperty(value = "是否关注公众号")
    private boolean subscribe;

    @ApiModelProperty(value = "首页超值爆款")
    private List<HashMap<String, Object>> explosiveMoney;

    @ApiModelProperty(value = "首页精品推荐图片")
    private List<HashMap<String, Object>> bastBanner;

    @ApiModelProperty(value = "云智服H5 url")
    private String yzfUrl;

}
