package com.zbkj.service.util.yly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

class HttpRequest {
    public static String sendGet(String url, Map<String, String> paramMap) {
        String param = forMap(paramMap);
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URLConnection connection = getUrlConnection(urlNameString);
            connection.connect();
            Map<String, List<String>> map = connection.getHeaderFields();
            for (String key : map.keySet())
                System.out.println(key + "--->" + map.get(key));
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
                result = result + line;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    public static String sendPost(String url, Map<String, String> paramMap) {
        String param = forMap(paramMap);
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URLConnection conn = getUrlConnection(url);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
                result.append(line);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    private static URLConnection getUrlConnection(String url) throws IOException {
        URL realUrl = new URL(url);
        URLConnection conn = realUrl.openConnection();
        conn.setConnectTimeout(30000);
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        return conn;
    }

    private static String forMap(Map<String, String> paramMap) {
        String reqStr = "";
        if (null != paramMap && !paramMap.isEmpty()) {
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                System.out.println("key = " + (String)entry.getKey() + ", value = " + (String)entry.getValue());
                reqStr = (String)entry.getKey() + "=" + (String)entry.getValue() + "&" + reqStr;
            }
            reqStr = reqStr.substring(0, reqStr.length() - 1);
        }
        return reqStr;
    }
}
