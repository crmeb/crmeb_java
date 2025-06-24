package com.zbkj.common.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 自定义组件上传图片请求对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
public class ShopUploadImgRequest {

    @ApiModelProperty(value = "0:media_id(目前只用于品牌申请品牌和类目)，1：返回链接, 2：返回微信支付media_id图片要求")
    private Integer respType;

    @ApiModelProperty(value = "0:图片流(resp_type=2需指定filename)，1:图片url")
    private Integer uploadType;

    @ApiModelProperty(value = "upload_type=1时必传")
    private String imgUrl;
}
