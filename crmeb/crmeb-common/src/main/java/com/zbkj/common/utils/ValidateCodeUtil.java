package com.zbkj.common.utils;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.druid.util.Base64;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

/**
 * 验证码生成工具类
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
@Component
public class ValidateCodeUtil {
    private static Validate validate = null;                  //验证码类，用于最后返回此对象，包含验证码图片base64和真值
    private static Random random = new Random();              //随机类，用于生成随机参数
    private static String randString = "0123456789abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";//随机生成字符串的取值范围

    private static int width = 80;     //图片宽度
    private static int height = 34;    //图片高度
    private static int stringNum = 4;  //字符的数量
    private static int lineSize = 40;  //干扰线数量

    /**
     * 将构造函数私有化 禁止new创建
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    private ValidateCodeUtil() {
        super();
    }

    /**
     * 获取随机字符
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return String
     */
    private static String getRandomChar(int index) {
        //获取指定位置index的字符，并转换成字符串表示形式
        return String.valueOf(randString.charAt(index));
    }

    /**
     * 获取随机指定区间的随机数
     * @param min (指定最小数)
     * @param max (指定最大数)
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return String
     */
    private static int getRandomNum(int min,int max) {
        return RandomUtil.randomInt(min, max);
    }

    /**
     * 获得字体
     */
    private static Font getFont() {
        return new Font("Fixedsys", Font.CENTER_BASELINE, 25);  //名称、样式、磅值
    }

    /**
     * 获得颜色
     * @param frontColor 覆盖颜色
     * @param backColor 背景色
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return Color
     */
    private static Color getRandColor(int frontColor, int backColor) {
        if(frontColor > 255)
            frontColor = 255;
        if(backColor > 255)
            backColor = 255;

        int red = frontColor + random.nextInt(backColor - frontColor - 16);
        int green = frontColor + random.nextInt(backColor - frontColor -14);
        int blue = frontColor + random.nextInt(backColor - frontColor -18);
        return new Color(red, green, blue);
    }

    /**
     * 绘制字符串,返回绘制的字符串
     * @param graphics 获得BufferedImage对象的Graphics对象
     * @param randomString 随机字符串
     * @param i 坐标倍数
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return string
     */
    private static String drawString(Graphics graphics, String randomString, int i) {
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setFont(getFont());   //设置字体
        g2d.setColor(new Color(random.nextFloat(), random.nextFloat(), random.nextFloat()));//设置颜色
        String randChar = getRandomChar(random.nextInt(randString.length()));
        randomString += randChar;   //组装
        int rot = getRandomNum(1,10);
        g2d.translate(random.nextInt(3), random.nextInt(3));
        g2d.rotate(rot * Math.PI / 180);
        g2d.drawString(randChar, 13*i, 20);
        g2d.rotate(-rot * Math.PI / 180);
        return randomString;
    }

    /**
     * 绘制干扰线
     * @param graphics 获得BufferedImage对象的Graphics对象
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    private static void drawLine(Graphics graphics) {
        //起点(x,y)  偏移量x1、y1
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(13);
        int yl = random.nextInt(15);
        graphics.setColor(new Color(random.nextFloat(), random.nextFloat(), random.nextFloat()));
        graphics.drawLine(x, y, x + xl, y + yl);
    }

    /**
     * 生成Base64图片验证码
     * @author Mr.Zhang
     * @since 2020-04-16
     * @return String
     */
    public static Validate getRandomCode() {
        validate = validate == null ? new Validate() : validate;

        // BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics Graphics = image.getGraphics();// 获得BufferedImage对象的Graphics对象
        Graphics.fillRect(0, 0, width, height);//填充矩形
        Graphics.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 18));//设置字体
        Graphics.setColor(getRandColor(110, 133));//设置颜色
        //绘制干扰线
        for(int i = 0; i <= lineSize; i++) {
            drawLine(Graphics);
        }
        //绘制字符
        String randomString = "";
        for(int i = 1; i <= stringNum; i++) {
            randomString = drawString(Graphics, randomString, i);
            validate.setValue(randomString);
        }

        Graphics.dispose();//释放绘图资源
        ByteArrayOutputStream bs = null;
        try {
            bs = new ByteArrayOutputStream();
            ImageIO.write(image, "png", bs);//将绘制得图片输出到流
            String imgSrc = Base64.byteArrayToBase64(bs.toByteArray());
            validate.setBase64Str(imgSrc);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                bs = null;
            }
        }
        return validate;
    }

    /**
     * 验证码类
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    public static class Validate implements Serializable{
        private static final long serialVersionUID = 1L;
        private String Base64Str;		//Base64 值
        private String value;			//验证码值

        public String getBase64Str() {
            return Base64Str;
        }
        public void setBase64Str(String base64Str) {
            Base64Str = base64Str;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
    }

}
