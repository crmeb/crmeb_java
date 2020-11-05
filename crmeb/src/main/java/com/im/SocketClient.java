package com.im;

import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  SocketClient 客户端
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2020 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
public class SocketClient {
    public static final int port = SocketServer.port;
    public static final String address = SocketServer.address;
    private static Logger log = Logger.getLogger("client");
    //为了方便使用直接定义为静态属性，，调用很方便。
    private static Socket client = null;
    //给客户端起个名字
    private static String clientName = "客服";
    private static Integer fromId = 1;
    private static Integer toId = 10001;

    private static MessageVo messageVo = new MessageVo(fromId, toId, clientName +"说：正在连接服务器");

    public static void main(String[] args) throws IOException, InterruptedException {
        log.log(Level.INFO, "客户端" + clientName + "开始链接，端口：" + port);
        client = new Socket(address, port);
        new SendThread().start();
        new ReceiveThread().start();
        sendSocketServer(JSONObject.toJSONString(messageVo), client);//发送消息
        while (!client.isClosed()) {
            Thread.sleep(1000);
        }
//        //测试连接数
//        Integer nowTime = DateUtil.getNowTime();
//        int i = 1;
//        try{
//            while(true){
//                log.log(Level.INFO, "客户端" + clientName + "开始链接，端口：" + port);
//                client = new Socket(address, port);
//                new SendThread().start();
//                new ReceiveThread().start();
//                sendSocketServer(JSONObject.toJSONString(messageVo), client);//发送消息
//                i++;
//            }
//        }catch (Exception | OutOfMemoryError e){
//            log.log(Level.INFO, "服务器断开连接, " + e.getMessage());
//        } finally {
//            int totalTime = DateUtil.getNowTime() - nowTime;
//            int avg = i / totalTime;
//            log.log(Level.INFO, "服务器断开连接, 耗时：" + totalTime + ", 平均每一秒：" + avg);
//        }
    }

    /**
     * 发送Socket的socket
     * @param outStr 待发送信息
     * @param client 客户端Socket
     */
    private static void sendSocketServer(String outStr, Socket client) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
            oos.writeObject(outStr);
            oos.flush();//刷新，将流发出去
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送信息线程
     */
    static class SendThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!client.isClosed()) {//插入停止条件
                Scanner in = new Scanner(System.in);//接收输入
                String inputStr = in.next();
                if (inputStr != null) {
                    messageVo.setMessage(inputStr);
                    sendSocketServer(JSONObject.toJSONString(messageVo), client);//发送消息
                }
            }
        }
    }

    /**
     * 接受信息线程，，运行在后台，，等待输入信息
     */
    static class ReceiveThread extends Thread {
        @SneakyThrows
        @Override
        public void run() {
            super.run();
            try {
                InputStream serverInputStream = client.getInputStream();
                while (!client.isClosed() && serverInputStream != null) {//插入停止条件
                    ObjectInputStream ois = new ObjectInputStream(serverInputStream);
                    String inputStr = (String) ois.readObject();
                    //获取输出流，经过测试发现，输入流貌似是阻塞，，也就是没有输入时，
                    // 他就停在这里了，，一直等着输入，，所以无需加入Thread.sleep().
                    System.out.println(inputStr);
                }
            } catch (SocketException e) {
                log.log(Level.INFO, "服务器断开连接！！！");
                try {
                    client.close();//服务器断开连接此时需要关闭客户端连接
                } catch (IOException e1) {
                    log.log(Level.FINE, e1.getMessage());

                }
            } catch (IOException | ClassNotFoundException e2) {
                log.log(Level.FINE, e2.getMessage());
            }finally {
                log.log(Level.FINE, "服务器断开连接！！！");
                client.close();
            }
        }
    }
}
