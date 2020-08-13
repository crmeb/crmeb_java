package com.zbkj.crmeb.upload.service;

import com.zbkj.crmeb.upload.vo.FileResultVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
* @author Mr.Zhang
* @Description StoreProductService 接口
* @since 2020-05-06
*/
public interface UploadService {
    FileResultVo image(MultipartFile multipart, String model, Integer pid) throws IOException;

    FileResultVo file(MultipartFile multipart, String model, Integer pid) throws IOException;
}
