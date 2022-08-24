package com.zbkj.common.utils;

import cn.hutool.core.util.RandomUtil;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

/**
 * 上传工具类
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
public class UploadUtil {

    //服务器存储地址
//    private static String rootPath  = "/www/wwwroot/upload";
    private static String rootPath  = "";

    //类型
    private static String type = "/" + Constants.UPLOAD_TYPE_IMAGE;


    //模块
//    private static String modelPath = "/store";
    private static String modelPath = "/public";

    //扩展名
    private static String extStr = "png,jpg";

    //文件大小上限
    private static int size = 2;

    public static String getRootPath() {
        return rootPath;
    }

    public static void setRootPath(String rootPath) {
        UploadUtil.rootPath = (rootPath + "/").replace(" ", "").replace("//", "/");
    }

    public static String getType() {
        return type;
    }

    public static void setType(String type) {
        UploadUtil.type = type + "/";
    }

    public static String getModelPath() {
        return modelPath;
    }

    public static void setModelPath(String modelPath) {
        UploadUtil.modelPath = modelPath + "/";
    }

    public static String getExtStr() {
        return extStr;
    }

    public static void setExtStr(String extStr) {
        UploadUtil.extStr = extStr;
    }

    public static int getSize() {
        return size;
    }

    public static void setSize(int size) {
        UploadUtil.size = size;
    }

    /**
     * 根据文件的绝对路径创建一个文件对象.
     * @return 返回创建的这个文件对象
     * @author Mr.Zhang
     * @since 2020-05-08
     */
    public static File createFile(String filePath) throws IOException {
        // 获取文件的完整目录
        String fileDir = FilenameUtils.getFullPath(filePath);
        // 判断目录是否存在，不存在就创建一个目录
        File file = new File(fileDir);
        if (!file.isDirectory()) {
            //创建失败返回null
            if (!file.mkdirs()) {
                throw new CrmebException("文件目录创建失败...");
            }
        }
        // 判断这个文件是否存在，不存在就创建
        file = new File(filePath);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new CrmebException("目标文件创建失败...");
            }
        }
        return file;
    }

    /**
     * 生成文件文件名
     * @param fileName 文件名
     * @author Mr.Zhang
     * @since 2020-05-08
     */
    public static String getDestPath(String fileName) {
        //规则：  子目录/年/月/日.后缀名
        return getServerPath() + fileName;
    }

    public static String fileName(String extName){
        return CrmebUtil.getUuid() + RandomUtil.randomString(10) + "." + extName;
    }

    /**
     * 生成文件在的实际的路径
     * @author Mr.Zhang
     * @since 2020-05-08
     */
    public static String getServerPath() {
        // 文件分隔符转化为当前系统的格式
        return FilenameUtils.separatorsToSystem( getRootPath() + getWebPath());
    }

    /**
     * web目录可访问的路径
     * @author Mr.Zhang
     * @since 2020-05-08
     */
    public static String getWebPath() {
        // 文件分隔符转化为当前系统的格式
        return getModelPath() + DateUtil.nowDate(Constants.DATE_FORMAT_DATE).replace("-", "/") + "/";
//        return getType() + getModelPath() + DateUtil.nowDate(Constants.DATE_FORMAT_DATE).replace("-", "/") + "/";
    }
}
