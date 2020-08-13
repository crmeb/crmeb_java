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
public class IndexProductBannerResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "顶部轮播图")
    private List<HashMap<String, Object>> banner;

    @ApiModelProperty(value = "商品")
    private List<ProductResponse> list;
}
