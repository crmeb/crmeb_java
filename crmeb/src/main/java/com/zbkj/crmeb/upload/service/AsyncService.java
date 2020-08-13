package com.zbkj.crmeb.upload.service;

import com.zbkj.crmeb.system.model.SystemAttachment;
import java.util.List;

/**
* @author Mr.Zhang
* @Description StoreProductService 接口
* @since 2020-05-06
*/
public interface AsyncService {
    void async(List<SystemAttachment> systemAttachmentList);

    String getCurrentBaseUrl();
}