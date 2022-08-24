package com.zbkj.service.service.impl;

import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.vo.CloudVo;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.*;
import com.zbkj.service.service.CosService;
import com.zbkj.service.service.SystemAttachmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.File;


/**
 * CosServiceImpl 同步到云服务
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class CosServiceImpl implements CosService {

    private static final Logger logger = LoggerFactory.getLogger(CosServiceImpl.class);

    @Lazy
    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Override
    public void uploadFile(CloudVo cloudVo, String webPth, String localFile, Integer id, COSClient cosClient) {

        logger.info("上传文件" + id + "开始：" + localFile);
        try {
            File file = new File(localFile);
            if(!file.exists()){
                logger.info("上传文件"+ id + localFile + "不存在：");
                return;
            }

            if(!cosClient.doesBucketExist(cloudVo.getBucketName())){
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(cloudVo.getBucketName());
                // 设置 bucket 的权限为 Private(私有读写), 其他可选有公有读私有写, 公有读写
                createBucketRequest.setCannedAcl(CannedAccessControlList.Private);

                try{
                    cosClient.createBucket(createBucketRequest);
                } catch (CosClientException serverException) {
                    serverException.printStackTrace();
                }
            }

            PutObjectRequest putObjectRequest = new PutObjectRequest(cloudVo.getBucketName(), webPth, file);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);

            logger.info("上传文件" + id + " -- 结束：" + putObjectResult.getETag());
        } catch (Exception e) {
            throw new CrmebException(e.getMessage());
        }
    }

    @Override
    public void uploadFile(CloudVo cloudVo, String webPth, String localFile, File file, COSClient cosClient) {
        logger.info("上传文件开始：" + localFile);
        try {
            if(!file.exists()){
                logger.info("上传文件" + localFile + "不存在：");
                return;
            }

            if(!cosClient.doesBucketExist(cloudVo.getBucketName())){
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(cloudVo.getBucketName());
                // 设置 bucket 的权限为 Private(私有读写), 其他可选有公有读私有写, 公有读写
                createBucketRequest.setCannedAcl(CannedAccessControlList.Private);

                try{
                    cosClient.createBucket(createBucketRequest);
                } catch (CosClientException serverException) {
                    serverException.printStackTrace();
                }
            }

            PutObjectRequest putObjectRequest = new PutObjectRequest(cloudVo.getBucketName(), webPth, file);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);

            logger.info("上传文件 -- 结束：" + putObjectResult.getETag());
        } catch (Exception e) {
            throw new CrmebException(e.getMessage());
        }
    }
}

