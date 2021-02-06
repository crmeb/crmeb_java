package com.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.constants.Constants;
import com.exception.CrmebException;
import com.zbkj.crmeb.upload.vo.FileResultVo;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 上传工具类
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
public class UploadUtil {

    private static UploadUtil uploadUtil = new UploadUtil();

    //服务器存储地址
//    private static String rootPath  = "/www/wwwroot/upload";
    private static String rootPath  = "";

    //类型
//    private static String imagePath = "/image";
//    private static String type = "/image";
    private static String type = "/crmebimage";


    //模块
//    private static String modelPath = "/store";
    private static String modelPath = "/public";

    //扩展名
    private static String extStr = "png,jpg";

    //文件大小上限
    private static int size = 2;

    //是否压缩图片
    private static boolean isCompress = false;

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

    public static boolean isIsCompress() {
        return isCompress;
    }

    public static void setIsCompress(boolean isCompress) {
        UploadUtil.isCompress = isCompress;
    }

    /**
     * 获取单例
     * @author Mr.Zhang
     * @since 2020-05-08
     */
    public static UploadUtil getInstance() {
        if (uploadUtil == null) {
            uploadUtil = new UploadUtil();
        }
        return uploadUtil;
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
     * 判断文件扩展名是否合法
     * @param extName 文件的后缀名
     * @author Mr.Zhang
     * @since 2020-05-08
     */
    private static void isContains(String extName) {
        if (StringUtils.isNotEmpty(getExtStr())) {
            // 切割文件扩展名
            List<String> extensionList = CrmebUtil.stringToArrayStr(getExtStr());

            if (extensionList.size() > 0) {
                //判断
                if (!extensionList.contains(extName)) {
                    throw new CrmebException("上传文件的类型只能是：" + getExtStr());
                }
            } else {
                throw new CrmebException("上传文件的类型只能是：" + getExtStr());
            }
        }
    }

//    /**
//     * 图片压缩
//     * @param serverPath 图片的绝对路径
//     * @param childFile  子文件夹
//     * @param extName    文件的后缀
//     * @author Mr.Zhang
//     * @since 2020-05-08
//     */
//    private static String thumbnails(String serverPath, String childFile, String extName) throws IOException {
//        // 压缩后的相对路径文件名
//        String toFilePath = getDestPath(extName);
//
//        // scale：图片缩放比例
//        // outputQuality：图片压缩比例
//        // toFile：图片位置
//        // outputFormat：文件输出后缀名
//        // Thumbnails 如果用来压缩 png 格式的文件，会越压越大，
//        // 得把png格式的图片转换为jpg格式
//        if ("png".equalsIgnoreCase(extName)) {
//            // 由于outputFormat会自动在路径后加上后缀名，所以移除以前的后缀名
//            String removeExtensionFilePath = FilenameUtils.removeExtension(toFilePath);
//            Thumbnails.of(serverPath).scale(1f)
//                    .outputQuality(0.5f)
//                    .outputFormat("jpg")
//                    .toFile(getServerPath(removeExtensionFilePath));
//            toFilePath = removeExtensionFilePath + ".jpg";
//        } else {
//            Thumbnails.of(serverPath).scale(1f).outputQuality(0.5f)
//                    .toFile(getServerPath(toFilePath));
//        }
//
//        // 删除被压缩的文件
//        FileUtils.forceDelete(new File(serverPath));
//
//        return toFilePath;
//    }

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
        return getType() + getModelPath() + DateUtil.nowDate(Constants.DATE_FORMAT_DATE).replace("-", "/") + "/";
    }

    /**
     * 检测文件大小上限
     * @author Mr.Zhang
     * @since 2020-05-08
     */
    private static void checkSize(Long size) {
        // 文件分隔符转化为当前系统的格式
        float fileSize = (float)size / 1024 / 1024;
        String fs = String.format("%.2f", fileSize);
        if( fileSize > getSize()){
            throw new CrmebException("最大允许上传" + getSize() + " MB的文件, 当前文件大小为 " + fs + " MB");
        }
    }


    /**
     * 上传文件
     * @param multipartFile 上传的文件对象，必传
     * @author Mr.Zhang
     * @since 2020-05-08
     */
    private static FileResultVo saveFile(MultipartFile multipartFile) throws IOException {
        if (null == multipartFile || multipartFile.isEmpty()) {
            throw new CrmebException("上传的文件对象不存在...");
        }
        // 文件名
        String fileName = multipartFile.getOriginalFilename();
        System.out.println("fileName = " + fileName);
        // 文件后缀名
        String extName = FilenameUtils.getExtension(fileName);
        if (StringUtils.isEmpty(extName)) {
            throw new RuntimeException("文件类型未定义不能上传...");
        }

        if (fileName.length() > 99) {
            fileName = StrUtil.subPre(fileName, 90).concat(".").concat(extName);
        }

        //文件大小验证
        checkSize(multipartFile.getSize());

        // 判断文件的后缀名是否符合规则
        isContains(extName);

        //文件名
        String newFileName = fileName(extName);
        // 创建目标文件的名称，规则请看destPath方法
        String destPath = getDestPath(newFileName);
        // 创建文件
        File file = createFile(destPath);
        // 保存文件
        multipartFile.transferTo(file);

        // 拼装返回的数据
        FileResultVo result = new FileResultVo();
        //如果是图片，就进行图片处理
        if (BooleanUtils.isTrue(isIsCompress())) {
//            // 图片处理
//            String toFilePath = thumbnails(serverPath, childFile, extName);
//            // 得到处理后的图片文件对象
//            File file = new File(getServerPath());
//            // 压缩后的文件后缀名
//            String extExtName = FilenameUtils.getExtension(toFilePath);
//            // 源文件=源文件名.压缩后的后缀名
//            String extFileName = FilenameUtils.getBaseName(fileName) + "." + FilenameUtils.getExtension(toFilePath);
//            result.setFileSize(file.length());
//            result.setServerPath(toFilePath);
//            result.setFileName(extFileName);
//            result.setExtName(extExtName);
        } else {
            result.setFileSize(multipartFile.getSize());
            result.setFileName(fileName);
            result.setExtName(extName);
            result.setServerPath(destPath);
            result.setUrl(getWebPath() + newFileName);
            result.setType(multipartFile.getContentType());
        }
        return result;
    }

    /**
     * 上传
     * @param multipartFile 上传的文件对象，必传
     * @since 2020-05-08
     * @author Mr.Zhang
     */
    public static FileResultVo file(MultipartFile multipartFile) throws IOException {
        return saveFile(multipartFile);
    }
}
