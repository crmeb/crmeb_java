package com.zbkj.service.util.yly;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    public static String getMD5Str(String str) {
        String re = null;
        try {
            byte[] tem = str.getBytes();
            MessageDigest md5 = MessageDigest.getInstance("md5");
            md5.reset();
            md5.update(tem);
            byte[] encrypt = md5.digest();
            StringBuilder sb = new StringBuilder();
            for (byte t : encrypt) {
                String s = Integer.toHexString(t & 0xFF);
                if (s.length() == 1)
                    s = "0" + s;
                sb.append(s);
            }
            re = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (re.length() == 31)
            return "0" + re;
        return re;
    }

    public static boolean isNull(String content) {
        if (content != null && !"".equals(content))
            return false;
        return true;
    }

    public static String getTimestamp() {
        return String.valueOf(System.currentTimeMillis() / 1000L);
    }
}
