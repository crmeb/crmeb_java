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
public class CloudVo {

    //域名空间
    private String domain;

    //accessKey
    private String accessKey;

    //secretKey
    private String secretKey;

    //bucketName
    private String bucketName;

//    //webPath web访问的url地址，云服务会自动创建相对应的文件夹
//    private String webPath;
//
//    //服务器存储的文件绝对路径
//    private String filePath;
//
//    //图片id
//    private Integer id;

    //节点
    private String region;
}
