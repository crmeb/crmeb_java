package com.zbkj.admin.filter;

import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.UploadConstants;
import com.zbkj.common.utils.SpringUtil;
import com.zbkj.service.service.SystemAttachmentService;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * response路径处理
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2025 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public class ResponseRouter {

    public String filter(String data, String path, CrmebConfig crmebConfig) {
        boolean result = un().contains(path);
        if (result) {
            return data;
        }

        //系统不用过滤的URL，针对数据而非token
        PathMatcher pathMatcher = new AntPathMatcher();
        for (String configUrl : crmebConfig.getIgnored()) {
            if(pathMatcher.match(path, configUrl)){
                return data;
            }
        }

        if (!path.contains("api/admin/") && !path.contains("api/front/")) {
            return data;
        }

        //根据需要处理返回值 && !data.contains("data:image/png;base64")
        if ((data.contains(UploadConstants.UPLOAD_FILE_KEYWORD + "/"))
                || data.contains(UploadConstants.DOWNLOAD_FILE_KEYWORD) || data.contains(UploadConstants.UPLOAD_AFTER_FILE_KEYWORD)) {
            if (data.contains(UploadConstants.DOWNLOAD_FILE_KEYWORD + "/" + UploadConstants.UPLOAD_MODEL_PATH_EXCEL)) {
                data = SpringUtil.getBean(SystemAttachmentService.class).prefixFile(data);
            } else if (data.contains(UploadConstants.UPLOAD_AFTER_FILE_KEYWORD + "/")) {
                data = SpringUtil.getBean(SystemAttachmentService.class).prefixUploadf(data);
            } else {
                data = SpringUtil.getBean(SystemAttachmentService.class).prefixImage(data);
            }
        }

        return data;
    }

    public static String un() {
        return "";
    }
}
