package com.zbkj.crmeb.express.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 系统电子面单对象
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ExpressSheetVo对象", description="系统电子面单对象")
public class ExpressSheetVo {

    public ExpressSheetVo() {
    }

    public ExpressSheetVo(Integer exportId, String exportCom, String exportTempId, String exportToName, String exportToTel, String exportToAddress, String exportSiid) {
        this.exportId = exportId;
        this.exportCom = exportCom;
        this.exportTempId = exportTempId;
        this.exportToName = exportToName;
        this.exportToTel = exportToTel;
        this.exportToAddress = exportToAddress;
        this.exportSiid = exportSiid;
    }

    public ExpressSheetVo(Integer exportId, String exportCom, String exportTempId, String exportToName, String exportToTel, String exportToAddress, String exportSiid, Integer exportOpen) {
        this.exportId = exportId;
        this.exportCom = exportCom;
        this.exportTempId = exportTempId;
        this.exportToName = exportToName;
        this.exportToTel = exportToTel;
        this.exportToAddress = exportToAddress;
        this.exportSiid = exportSiid;
        this.exportOpen = exportOpen;
    }

    @ApiModelProperty(value = "快递公司简称，物流、电子面单开通必填")
    private Integer exportId;

    @ApiModelProperty(value = "快递公司简称，物流、电子面单开通必填")
    private String exportCom;

    @ApiModelProperty(value = "快递公司模板Id、电子面单开通必填")
    private String exportTempId;

    @ApiModelProperty(value = "快递面单发货人姓名，物流、电子面单开通必填")
    private String exportToName;

    @ApiModelProperty(value = "快递面单发货人电话，物流、电子面单开通必填")
    private String exportToTel;

    @ApiModelProperty(value = "发货人详细地址，物流、电子面单开通必填")
    private String exportToAddress;

    @ApiModelProperty(value = "电子面单打印机编号，物流、电子面单开通必填")
    private String exportSiid;

    @ApiModelProperty(value = "电子面单是否开启")
    private Integer exportOpen;

}
