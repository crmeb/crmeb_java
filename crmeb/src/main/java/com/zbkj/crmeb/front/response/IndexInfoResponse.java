package com.zbkj.crmeb.front.response;

import com.zbkj.crmeb.user.model.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 用户地址表
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-04-28
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

    @ApiModelProperty(value = "活动")
    private IndexInfoItemResponse info;

    @ApiModelProperty(value = "活动区域图片")
    private List<HashMap<String, Object>> activity;

    @ApiModelProperty(value = "首发新品广告图")
    private HashMap<String, Object> lovely;

    @ApiModelProperty(value = "首页促销单品")
    private List<ProductResponse> benefit;

    @ApiModelProperty(value = "热门榜单")
    private List<ProductResponse> likeInfo;

    @ApiModelProperty(value = "企业logo")
    private String logoUrl;

    @ApiModelProperty(value = "优惠券")
    private List<HashMap<String, Object>> couponList;

    @ApiModelProperty(value = "是否关注")
    private boolean subscribe = false;

    @ApiModelProperty(value = "首页超值爆款")
    private List<HashMap<String, Object>> explosiveMoney;

}
