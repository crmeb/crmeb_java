package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 上报支付的场景信息详情
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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CreateOrderH5SceneInfoDetailVo对象", description="上报支付的场景信息详情")
public class CreateOrderH5SceneInfoDetailVo {
    public CreateOrderH5SceneInfoDetailVo() {
    }

    public CreateOrderH5SceneInfoDetailVo(String url, String name) {
        this.wap_url = url;
        this.wap_name = name;
    }

    @ApiModelProperty(value = "场景类型", required = true)
    private String type = "Wap";

    @ApiModelProperty(value = "WAP网站URL地址", required = true)
    private String wap_url;

    @ApiModelProperty(value = "WAP 网站名", required = true)
    private String wap_name;
}
