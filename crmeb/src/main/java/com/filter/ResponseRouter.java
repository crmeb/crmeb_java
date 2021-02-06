package com.filter;

import com.utils.SpringUtil;
import com.zbkj.crmeb.system.service.SystemAttachmentService;

/**
 * response路径处理
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
public class ResponseRouter {

    public String filter(String data, String path){
        boolean result = un().contains(path);
        if(result){
            return data;
        }

        if (!path.contains("api/admin/") && !path.contains("api/front/")) {
            return data;
        }


        //根据需要处理返回值
//        if(data.contains("image/") && !data.contains("data:image/png;base64")){
        if(data.contains("crmebimage/") && !data.contains("data:image/png;base64")){
            data = SpringUtil.getBean(SystemAttachmentService.class).prefixImage(data);
        }

        if(data.contains("file/")){
            data = SpringUtil.getBean(SystemAttachmentService.class).prefixFile(data);
        }

        return data;
    }

    public static String un(){
        return "";
    }
}
