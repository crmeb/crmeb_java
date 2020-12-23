package com.zbkj.crmeb.upload.service;

import com.qcloud.cos.COSClient;
import com.zbkj.crmeb.upload.vo.CloudVo;

/**
 * CosService 接口
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
public interface CosService {
    void upload(CloudVo cloudVo, String webPth, String localFile, Integer id, COSClient cosClient);
}