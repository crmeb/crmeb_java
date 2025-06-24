package com.zbkj.admin.copyright;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 版权信息响应对象
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
@ApiModel(value = "CopyrightInfoResponse对象", description = "版权信息响应对象")
public class CopyrightInfoResponse {

    @ApiModelProperty(value = "管理端API域名")
    private String domainUrl;

    @ApiModelProperty(value = "项目版本号")
    private String version;

    @ApiModelProperty(value = "版权标签")
    private Integer label;

    @ApiModelProperty(value = "授权码")
    private String authCode;

    @ApiModelProperty(value = "版权状态：-2=API域名未配置 -1=未提交，0-待审核，1-授权成功，2-审核失败")
    private Integer status;

    @ApiModelProperty(value = "公司信息")
    private String companyName;

    @ApiModelProperty(value = "公司图片")
    private String companyImage;

    @ApiModelProperty(value = "版权码")
    private String copyright;
}
