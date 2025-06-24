package com.zbkj.common.response;

import lombok.Data;

/**
 * CopyrightResponse 站点版权
 */
@Data
public class CopyRightResponse {
    // 备案号
    public String copyrightIcpNumber = "";
    // 备案号连接
    public String copyrightIcpNumberUrl = "";
    // 网安备案
    public String copyrightInternetRecord = "";
    // 网安备案连接
    public String copyrightInternetRecordUrl = "";
}
