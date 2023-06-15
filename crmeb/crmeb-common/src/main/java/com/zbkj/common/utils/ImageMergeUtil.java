package com.zbkj.common.utils;

import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.vo.ImageMergeUtilVo;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 * 图片工具类
 */
@Data
@Component
public class ImageMergeUtil {
    public static BufferedImage bufferedImage; //背景图片
    public static String model = "merge";
    public static String ext = "jpg";

    /**
     * 合并生成新的图片文件
     * @param list List<ImageMergeUtilVo> 图片集合
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    public static String drawWordFile(List<ImageMergeUtilVo> list){
        buildImage(list);
        if(bufferedImage == null){
            return null;
        }

        try {
            UploadUtil.setModelPath(model);
            UploadUtil.setExtStr(ext);
//            UploadUtil.setType(Constants.UPLOAD_TYPE_IMAGE);

            //文件名
            String newFileName = UploadUtil.fileName(ext);
            // 创建目标文件的名称，规则请看destPath方法
            String destPath = UploadUtil.getDestPath(newFileName);
            // 创建文件
            File file = UploadUtil.createFile(destPath);
            ImageIO.write(bufferedImage, ext, file);
            return destPath;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static void drawImage(ImageMergeUtilVo imageMergeUtilVo){
        String fileName = getFileSource(imageMergeUtilVo.getPath());
        if(StringUtils.isBlank(fileName)){
            return;
        }
        try{
            File file = new File(fileName);
            BufferedImage read = ImageIO.read(file);

            if(bufferedImage == null || "".equals(bufferedImage)){
                imageMergeUtilVo.setX(0);
                imageMergeUtilVo.setY(0);
                //创建新的画布，宽高以第一个图为准
                bufferedImage = new BufferedImage(read.getWidth(), read.getHeight(), BufferedImage.TYPE_INT_BGR);
            }

            Graphics graphics = bufferedImage.getGraphics();
            //在画布上绘制背景图
            graphics.drawImage(read, imageMergeUtilVo.getX(), imageMergeUtilVo.getX(), read.getWidth(), read.getHeight(), null);
            file.delete();
        }catch (Exception e){
            //合成图片xx失败
            throw new CrmebException("合成图片 + " + fileName + "失败");
        }
    }

    /**
     * 合并生成新的图片流
     * @param list List<ImageMergeUtilVo> 图片集合
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    private static void buildImage(List<ImageMergeUtilVo> list){
        if(list.size() < 2){
            throw new CrmebException("至少需要2张图片才可以做合并");
        }

        for (ImageMergeUtilVo imageMergeUtilVo : list) {
            drawImage(imageMergeUtilVo);
        }
    }



    /**
     * 根据图片路径输出File流
     * @param url String 文件地址
     * @author Mr.Zhang
     * @since 2020-05-06
     */
    private static String getFileSource(String url){
        if(!url.contains("http")){
            return url;
        }

        try {
            URL urlInfo = new URL(url);
            // 打开连接
            URLConnection con = urlInfo.openConnection();
            // 输入流
            InputStream is = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;

            String fileName = UploadUtil.fileName(ext);
            // 输出的文件流
            OutputStream os = new FileOutputStream(fileName);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            // 完毕，关闭所有链接
            os.close();
            is.close();

            return fileName;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
