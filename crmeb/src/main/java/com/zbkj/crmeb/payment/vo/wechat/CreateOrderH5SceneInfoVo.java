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
@ApiModel(value="CreateOrderH5SceneInfoVo对象", description="上报支付的场景信息")
public class CreateOrderH5SceneInfoVo {
    public CreateOrderH5SceneInfoVo() {
    }

    public CreateOrderH5SceneInfoVo(CreateOrderH5SceneInfoDetailVo h5Info) {
        this.h5_info = h5Info;
    }

    @ApiModelProperty(value = "h5_info", required = true)
    @JsonProperty(value = "h5_info")
    private CreateOrderH5SceneInfoDetailVo h5_info;
}
