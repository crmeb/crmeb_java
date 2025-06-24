package com.zbkj.common.response.pagelayout;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 页面设计底部导航响应对象
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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PageLayoutBottomNavigationResponse对象", description = "页面设计底部导航响应对象")
public class PageLayoutBottomNavigationResponse implements Serializable {

    private static final long serialVersionUID = 8350218800271787826L;

    @ApiModelProperty(value = "底部导航")
    private List<HashMap<String, Object>> bottomNavigationList;

    @ApiModelProperty(value = "是否自定义")
    private String isCustom;

}
