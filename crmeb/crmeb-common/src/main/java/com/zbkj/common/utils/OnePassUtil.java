package com.zbkj.common.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @Auther: 大粽子
 * @Date: 2023/8/29 12:14
 * @Description: 描述对应的业务场景
 */
public class OnePassUtil {
    private static final String ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";

    private static final String SECRET_KEY = "user-AppSecret-key";

    /**
     * 一号通商家寄件 回调数据揭秘方法
     * @param encryptedData 回调加密数据
     * @return 揭秘数据
     * @throws Exception 异常信息
     */
    public static String decrypt(String encryptedData) throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] ivBytes = new byte[16];
        byte[] encryptedBytes = new byte[decodedBytes.length - 16];
        System.arraycopy(decodedBytes, 0, ivBytes, 0, 16);
        System.arraycopy(decodedBytes, 16, encryptedBytes, 0, encryptedBytes.length);

        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.substring(0, 32).getBytes("UTF-8"), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

//    public static void main(String[] args) {
//        try {
//            // 加密字符串
//            String encryptedString = "85Z0kFc/X0EXUUZes5Wdni9jXOuZiWb5uRGZrk+48dkoourTAXBO4jZR8au8vXqFKKyqpTo/7Detwww9p8HdVN4cCD4wh/S3ic1jCTPJVI8cEowGAFoYNAi5mPOg4y8F/T1U8lqyL7TxrYx6P5KRoSwFbjj9z0e9rHvh/fWtaA/XirRLebVqA3zzYigKPx3m";
//
//            // 解密字符串
//            String decryptedString = decrypt(encryptedString);
//            System.out.println("解密后的字符串: " + decryptedString);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
