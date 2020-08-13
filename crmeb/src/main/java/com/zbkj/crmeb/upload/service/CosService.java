package com.zbkj.crmeb.upload.service;

import com.qcloud.cos.COSClient;
import com.zbkj.crmeb.upload.vo.CloudVo;

/**
* @author Mr.Zhang
* @Description StoreProductService 接口
* @since 2020-05-06
*/
public interface CosService {
    void upload(CloudVo cloudVo, String webPth, String localFile, Integer id, COSClient cosClient);
}