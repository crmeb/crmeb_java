package com.zbkj.crmeb.front.response;

import com.zbkj.crmeb.store.response.StoreProductResponse;
import com.zbkj.crmeb.user.model.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;

/**
 * 砍价商品列表header响应对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="BargainHeaderResponse对象", description="砍价商品列表header响应对象")
public class BargainHeaderResponse {

    @ApiModelProperty(value = "参与砍价总人数")
    private Integer bargainTotal;

    @ApiModelProperty(value = "砍价成功列表（默认7条）")
    private List<HashMap<String, Object>> bargainSuccessList;

}
