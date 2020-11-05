package com.zbkj.crmeb.upload.service;

import com.zbkj.crmeb.upload.vo.CloudVo;

/**
 * @author Mr.Zhang
 * @Description StoreProductService 接口
 * @since 2020-05-06
 */
public interface OssService {
    void upload(CloudVo cloudVo, String webPth, String localFile, Integer id);
}
