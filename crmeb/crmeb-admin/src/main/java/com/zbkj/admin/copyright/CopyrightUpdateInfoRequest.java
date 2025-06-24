package com.zbkj.admin.copyright;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 编辑公司版权信息请求对象
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
@ApiModel(value = "CopyrightUpdateInfoRequest对象", description = "编辑公司版权信息请求对象")
public class CopyrightUpdateInfoRequest {

    @ApiModelProperty(value = "公司信息")
    @NotBlank(message = "公司信息不能为空")
    private String companyName;

    @ApiModelProperty(value = "公司图片")
//    @NotBlank(message = "公司版权图片不能为空")
    private String companyImage;
}
