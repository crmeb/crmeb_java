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
public class FileResultVo{

    // 文件名
    private String fileName;

    // 扩展名
    private String extName;

    // 文件大小，字节
    private Long fileSize;

    // 文件存储在服务器的相对地址
    private String serverPath;

    //可供访问的url
    private String url;

    //类型
    private String type;
}
