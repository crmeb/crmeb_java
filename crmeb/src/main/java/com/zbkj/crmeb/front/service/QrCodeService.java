package com.zbkj.crmeb.front.service;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
* QrCodeService 接口
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
public interface QrCodeService {
    Map<String, Object> get(JSONObject data) throws IOException;

    Map<String, Object> base64(String url);

    Map<String, Object> base64String(String text,int width, int height);

}
