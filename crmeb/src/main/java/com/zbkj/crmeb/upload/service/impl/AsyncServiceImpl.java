package com.zbkj.crmeb.upload.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.constants.Constants;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.zbkj.crmeb.system.model.SystemAttachment;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.upload.service.AsyncService;
import com.zbkj.crmeb.upload.service.CosService;
import com.zbkj.crmeb.upload.service.OssService;
import com.zbkj.crmeb.upload.service.QiNiuService;
import com.zbkj.crmeb.upload.vo.CloudVo;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * AsyncServiceImpl 同步到云服务
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
@Service
@Data
public class AsyncServiceImpl implements AsyncService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncServiceImpl.class);


    @Autowired
    private SystemConfigService systemConfigService;


    @Autowired
    private OssService ossService;

    @Autowired
    private CosService cosService;

    @Autowired
    private QiNiuService qiNiuService;

    private CloudVo cloudVo = new CloudVo();

    // 读取不同配置的前缀
    private String pre;

    // 当前使用的云服务Url
    private String leftBase = null;

    /**
     * 图片上传
     * @param systemAttachmentList List<SystemAttachment> 附件列表
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    @Override
    public void async(List<SystemAttachment> systemAttachmentList){
        if(systemAttachmentList.size() < 1){
            return;
        }
        try{
            //图片上传类型 1本地 2七牛云 3OSS 4COS, 默认本地，任务轮询数据库放入云服务
            String uploadType = systemConfigService.getValueByKeyException("uploadType");
            //获取配置信息
            Integer type = Integer.parseInt(uploadType);
            switch (type){
                case 2:
                    pre = "qn";
                    setConf(type);
                    qCloud(systemAttachmentList);
                    break;
                case 3:
                    pre = "al";
                    setConf(type);
                    oss(systemAttachmentList);
                    break;
                case 4:
                    pre = "tx";
                    setConf(type);
                    cos(systemAttachmentList);
                    break;
                default:
                    pre = "local";
                    break;
            }
        }catch (Exception e){
            //TODO
        }
    }

    private void setConf(Integer uploadType) {
        cloudVo.setDomain(systemConfigService.getValueByKeyException(pre+"UploadUrl"));
        if(uploadType > 1){
            cloudVo.setAccessKey(systemConfigService.getValueByKeyException(pre+"AccessKey"));
            cloudVo.setSecretKey(systemConfigService.getValueByKeyException(pre+"SecretKey"));
            cloudVo.setBucketName(systemConfigService.getValueByKeyException(pre+"StorageName"));
            cloudVo.setRegion(systemConfigService.getValueByKeyException(pre+"StorageRegion"));
        }
    }

    /**
     * 同步到腾讯云oss
     * @param systemAttachmentList List<SystemAttachment> 附件列表
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    private void cos(List<SystemAttachment> systemAttachmentList) {
        if(systemAttachmentList.size() < 1){
            return;
        }

        // 1 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(cloudVo.getAccessKey(), cloudVo.getSecretKey());
        // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new com.qcloud.cos.region.Region(cloudVo.getRegion()));
        // 3 生成 cos 客户端。
        COSClient cosClient = new COSClient(cred, clientConfig);


        try{
            String webPath = systemConfigService.getValueByKey(Constants.UPLOAD_ROOT_PATH_CONFIG_KEY);
            for (SystemAttachment systemAttachment : systemAttachmentList) {
                logger.info("AsyncServiceImpl.cos.id " + systemAttachment.getAttId());
//                cosService.upload(cloudVo, systemAttachment.getSattDir(), systemAttachment.getAttDir(), systemAttachment.getAttId(), cosClient);
                cosService.upload(cloudVo, systemAttachment.getSattDir(), webPath + systemAttachment.getSattDir(), systemAttachment.getAttId(), cosClient);
            }
        }catch (Exception e){
            logger.error("AsyncServiceImpl.cos.fail " + e.getMessage());
        }finally {
            cosClient.shutdown();
        }
    }

    /**
     * 同步到七牛云
     * @param systemAttachmentList List<SystemAttachment> 附件列表
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    private void qCloud(List<SystemAttachment> systemAttachmentList) {
        if(systemAttachmentList.size() < 1){
            return;
        }

        try{
            // 构造一个带指定Zone对象的配置类, 默认华东
            Configuration cfg = new Configuration(Region.huadong());
            if(cloudVo.getRegion().equals("huabei")){
                cfg = new Configuration(Region.huabei());
            }
            if(cloudVo.getRegion().equals("huanan")){
                cfg = new Configuration(Region.huanan());
            }
            if(cloudVo.getRegion().equals("beimei")){
                cfg = new Configuration(Region.beimei());
            }
            if(cloudVo.getRegion().equals("dongnanya")){
                cfg = new Configuration(Region.xinjiapo());
            }

            // 其他参数参考类注释
            UploadManager uploadManager = new UploadManager(cfg);
            // 生成上传凭证，然后准备上传

            Auth auth = Auth.create(cloudVo.getAccessKey(), cloudVo.getSecretKey());
            String upToken = auth.uploadToken(cloudVo.getBucketName());

            String webPath = systemConfigService.getValueByKey(Constants.UPLOAD_ROOT_PATH_CONFIG_KEY);
            for (SystemAttachment systemAttachment : systemAttachmentList) {
                logger.info("AsyncServiceImpl.qCloud.id " + systemAttachment.getAttId());
//                qiNiuService.upload(uploadManager, cloudVo, upToken,
//                        systemAttachment.getSattDir(), systemAttachment.getAttDir(), systemAttachment.getAttId());   //异步处理
                qiNiuService.upload(uploadManager, cloudVo, upToken,
                        systemAttachment.getSattDir(), webPath + systemAttachment.getSattDir(), systemAttachment.getAttId());   //异步处理
            }
        }catch (Exception e){
            logger.error("AsyncServiceImpl.qCloud.fail " + e.getMessage());
        }
    }


    /**
     * 同步到阿里云oss
     * @param systemAttachmentList List<SystemAttachment> 附件列表
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    private void oss(List<SystemAttachment> systemAttachmentList){
        if(systemAttachmentList.size() < 1){
            return;
        }
        try{
            String webPath = systemConfigService.getValueByKey(Constants.UPLOAD_ROOT_PATH_CONFIG_KEY);
            for (SystemAttachment systemAttachment : systemAttachmentList) {
                logger.info("AsyncServiceImpl.oss.id " + systemAttachment.getAttId());
//                ossService.upload(cloudVo, systemAttachment.getSattDir(),  systemAttachment.getAttDir(),
//                        systemAttachment.getAttId()); //异步处理
                ossService.upload(cloudVo, systemAttachment.getSattDir(),  webPath + systemAttachment.getSattDir(),
                        systemAttachment.getAttId()); //异步处理
            }
        }catch (Exception e){
            logger.error("AsyncServiceImpl.oss fail " + e.getMessage());
        }
    }

    @Override
    public String getCurrentBaseUrl() {
        if(leftBase != null){
            return leftBase;
        }
        String uploadType = systemConfigService.getValueByKeyException("uploadType");
        //获取配置信息
        int type = Integer.parseInt(uploadType);
        String pre = "local";
        switch (type){
            case 2:
                pre = "qn";
                break;
            case 3:
                pre = "al";
                break;
            case 4:
                pre = "tx";
                break;
            default:
                break;
        }

        return systemConfigService.getValueByKey(pre+"UploadUrl");
    }
}

