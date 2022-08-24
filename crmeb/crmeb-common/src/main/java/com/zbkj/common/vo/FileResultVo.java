package com.zbkj.common.vo;

import lombok.Data;
/**
 * 文件信息
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
@Data
public class FileResultVo{

    // 文件名
    private String fileName;

    // 扩展名
    private String extName;

    // 文件大小，字节
    private Long fileSize;

    // 文件存储在服务器的相对地址
//    private String serverPath;

    //可供访问的url 域名根据配置存储，代替了上面serverPath 的功能
    private String url;

    //类型
    private String type;
}
