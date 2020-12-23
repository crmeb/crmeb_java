package com.zbkj.crmeb.pass.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 服务开通请求对象
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
@ApiModel(value = "ServiceOpenRequest对象", description = "服务开通请求对象")
public class ServiceOpenRequest {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "服务类型:sms,短信;copy,产品复制;expr_query,物流查询;expr_dump,电子面单", required = true)
    @NotBlank(message = "服务类型不能为空")
    private String type;

    @ApiModelProperty(value = "短信签名，短信开通必填")
    private String sign;

    @ApiModelProperty(value = "快递公司简称，电子面单开通必填")
    private String com;

    @ApiModelProperty(value = "快递公司模板Id、电子面单开通必填")
    private String tempId;

    @ApiModelProperty(value = "快递面单发货人姓名，电子面单开通必填")
    private String toName;

    @ApiModelProperty(value = "快递面单发货人电话，电子面单开通必填")
    private String toTel;

    @ApiModelProperty(value = "发货人详细地址，电子面单开通必填")
    private String toAddress;

    @ApiModelProperty(value = "电子面单打印机编号，电子面单开通必填")
    private String siid;

}
