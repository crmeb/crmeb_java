package com.zbkj.crmeb.upload.service;

import com.zbkj.crmeb.upload.vo.FileResultVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * UploadService 接口
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
public interface UploadService {
    FileResultVo image(MultipartFile multipart, String model, Integer pid) throws IOException;

    FileResultVo file(MultipartFile multipart, String model, Integer pid) throws IOException;
}
