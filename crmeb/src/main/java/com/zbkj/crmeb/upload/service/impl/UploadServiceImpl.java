package com.zbkj.crmeb.upload.service.impl;

import com.constants.Constants;
import com.utils.UploadUtil;
import com.zbkj.crmeb.system.service.SystemAttachmentService;
import com.zbkj.crmeb.system.service.SystemConfigService;
import com.zbkj.crmeb.upload.service.UploadService;
import com.zbkj.crmeb.upload.vo.FileResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


/**
 * UploadServiceImpl 接口实现
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
public class UploadServiceImpl implements UploadService {
    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 图片上传
     * @param multipart MultipartFile 上传文件流
     * @param model String 上传模块
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    @Override
    public FileResultVo image(MultipartFile multipart, String model, Integer pid) throws IOException {
        setImageUpload(model);
        FileResultVo file = UploadUtil.file(multipart);
        //文件入库
        file.setType(file.getType().replace("image/", ""));
        systemAttachmentService.create(file, pid);
        return file;
    }

    /**
     * 图片文件
     * @param multipart MultipartFile 上传文件流
     * @param model String 上传模块
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    @Override
    public FileResultVo file(MultipartFile multipart, String model, Integer pid) throws IOException {
        setFileUpload(model);
        FileResultVo file = UploadUtil.file(multipart);

        //文件入库
        file.setType(file.getType().replace("file/", ""));
        systemAttachmentService.create(file, pid);
        return file;
    }

    /**
     * 设置上传图片参数
     * @param model String 上传模块
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    private void setImageUpload(String model){
        UploadUtil.setRootPath(systemConfigService.getValueByKey(Constants.UPLOAD_ROOT_PATH_CONFIG_KEY));
        UploadUtil.setModelPath(model);
        UploadUtil.setExtStr(systemConfigService.getValueByKey(Constants.UPLOAD_IMAGE_EXT_STR_CONFIG_KEY));
        UploadUtil.setSize(Integer.parseInt(systemConfigService.getValueByKey(Constants.UPLOAD_IMAGE_MAX_SIZE_CONFIG_KEY)));
        UploadUtil.setType(Constants.UPLOAD_TYPE_IMAGE);
    }

    /**
     * 设置上传文件参数
     * @param model String 上传模块
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    private void setFileUpload(String model){
        UploadUtil.setRootPath(systemConfigService.getValueByKey(Constants.UPLOAD_ROOT_PATH_CONFIG_KEY));
        UploadUtil.setModelPath(model);
        UploadUtil.setExtStr(systemConfigService.getValueByKey(Constants.UPLOAD_FILE_EXT_STR_CONFIG_KEY));
        UploadUtil.setSize(Integer.parseInt(systemConfigService.getValueByKey(Constants.UPLOAD_FILE_MAX_SIZE_CONFIG_KEY)));
        UploadUtil.setType(Constants.UPLOAD_TYPE_FILE);
    }
}

