package com.zbkj.common.request;

import com.zbkj.common.annotation.StringContains;
import com.zbkj.common.constants.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

/**
 * 一号通用量记录请求对象
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
@ApiModel(value = "OnePassUserRecordRequest对象", description = "一号通用量记录请求对象")
public class OnePassUserRecordRequest {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录类型:sms,短信;copy,产品复制;expr_query,物流查询;expr_dump,电子面单", required = true)
    @NotBlank(message = "记录类型不能为空")
    @StringContains(limitValues = {"sms","copy","expr_query","expr_dump"}, message = "未知的记录类型")
    private String type;

    @ApiModelProperty(value = "短信状态(短信类型时必传)：0发送中，1成功，2失败，3全部")
    @Range(min = 0, max = 3, message = "未知的短信状态")
    private Integer status;

    @ApiModelProperty(value = "页码", example = Constants.DEFAULT_PAGE + "")
    private int page = Constants.DEFAULT_PAGE;

    @ApiModelProperty(value = "每页数量", example = Constants.DEFAULT_LIMIT + "")
    private int limit = Constants.DEFAULT_LIMIT;

}
