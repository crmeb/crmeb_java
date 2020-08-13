package com.zbkj.crmeb.payment.vo.wechat;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
