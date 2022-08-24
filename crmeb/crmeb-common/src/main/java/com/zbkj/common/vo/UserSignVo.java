package com.zbkj.common.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 签到记录对象
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
@ApiModel(value="UserSignVo对象", description="签到记录对象")
public class UserSignVo implements Serializable {

    private static final long serialVersionUID=1L;

    public UserSignVo(String title, Integer number, Date createDay) {
        this.title = title;
        this.number = number;
        this.createDay = createDay;
    }

    @ApiModelProperty(value = "签到说明")
    private String title;

    @ApiModelProperty(value = "获得积分")
    private Integer number;

    @ApiModelProperty(value = "签到日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date createDay;
}
