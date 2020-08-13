package com.zbkj.crmeb.upload.vo;

import lombok.Data;

/**
 * <p>
 * 文件信息
 * </p>
 *
 * @author Mr.Zhang
 * @since 2020-05-06
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
