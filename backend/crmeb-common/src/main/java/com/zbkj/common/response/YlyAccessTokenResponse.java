package com.zbkj.common.response;

import lombok.Data;

/** 易联云 获取AccessToken response
 * @program: crmeb
 * @author: 大粽子
 * @create: 2021-11-26 16:48
 **/
@Data
public class YlyAccessTokenResponse {
    private String error;
    private String error_description;
    private YlyAccessTokenBodyResponse body;
}
