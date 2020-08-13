package com.zbkj.crmeb.upload.service;

import com.qiniu.storage.UploadManager;
import com.zbkj.crmeb.upload.vo.CloudVo;

/**
* @author Mr.Zhang
* @Description StoreProductService 接口
* @since 2020-05-06
*/
public interface QiNiuService {
    void upload(UploadManager uploadManager, CloudVo cloudVo, String upToken, String webPth, String localFile, Integer id);
}