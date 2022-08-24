package com.zbkj.service.service.impl;

import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.vo.CloudVo;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.zbkj.service.service.QiNiuService;
import com.zbkj.service.service.SystemAttachmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;


/**
 * AsyncServiceImpl 同步到云服务
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
public class QiNiuServiceImpl implements QiNiuService {
    private static final Logger logger = LoggerFactory.getLogger(QiNiuServiceImpl.class);

    @Lazy
    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 同步到七牛云
     * @param cloudVo CloudVo
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    @Async
    @Override
    public void upload(UploadManager uploadManager, CloudVo cloudVo, String upToken, String webPth, String localFile, Integer id) {
        try {
            logger.info("上传文件" + id + "开始：" + localFile);

            File file = new File(localFile);
            if(!file.exists()){
                logger.info("上传文件"+ id + localFile + "不存在：");
                return;
            }

            Response put = uploadManager.put(localFile, webPth, upToken);
            put.close();
            logger.info("上传文件" + id + " -- 结束：" + put.address);
            //更新数据库
            systemAttachmentService.updateCloudType(id, 2);
        } catch (QiniuException ex) {
            throw new CrmebException(ex.getMessage());
        }
    }

    @Override
    public void uploadFile(UploadManager uploadManager, CloudVo cloudVo, String upToken, String webPth, String localFile, File file) {
        try {
            logger.info("上传文件开始：" + localFile);

//            File file = new File(localFile);
            if(!file.exists()){
                logger.info("上传文件" + localFile + "不存在：");
                return;
            }

//            Response put = uploadManager.put(localFile, webPth, upToken);
            Response put = uploadManager.put(file, webPth, upToken);
            put.close();
            logger.info("上传文件 -- 结束：" + put.address);
        } catch (QiniuException ex) {
            throw new CrmebException(ex.getMessage());
        }
    }
}

