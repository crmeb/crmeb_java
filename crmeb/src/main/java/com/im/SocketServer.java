package com.im;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Socket服务端
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
public class SocketServer {
    //用于保存客户端Socket

    public static HashMap<Integer, Socket> clientSocketList = new HashMap<>();
    public static HashMap<Integer, Integer> userPortList = new HashMap<>();
    //服务器端口
    public static final int port = 12345;
    //服务器主机名
    public static final String address = "127.0.0.1";
    //服务器接收消息，以日志形式记录，动态
    private static Logger log = Logger.getLogger("server");


    public static void main(String[] args) throws IOException{
        //创建ServerSocket监听
        ServerSocket serverSocket = new ServerSocket(port);
        log.log(Level.INFO, "服务器开启监听，端口：" + port);
        while (true) {
            Socket client = serverSocket.accept();//阻塞监听
            new SocketDoWith(client).start();//创建线程对其进行操作
            log.log(Level.INFO, "端口：" + client.getPort() + "接入服务器");
            clientSocketList.put(client.getPort(), client);
        }
    }

    //服务器处理客户端Socket消息线程
    static class SocketDoWith extends Thread {
        Logger log = Logger.getLogger("server");
        private Socket socket = null;

        public SocketDoWith(Socket socket) {
            this.socket = socket;
        }


        public void run() {
            if (socket == null) return;
            try {
                String readData;
                while (!socket.isClosed()) {
                    //将输入封装成对象流（处理起来更方便）
                    ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
                    readData = (String) oi.readObject();
                    //将输入作为日志打印
                    log.log(Level.INFO, "服务器接收内容：" + readData);

                    int port = socket.getPort();
                    MessageVo messageVo = JSONObject.parseObject(readData, MessageVo.class);

                    //看发送者是否在， 不在的加进去
                    if(!userPortList.containsKey(messageVo.getFromId())){
                        userPortList.put(messageVo.getFromId(), port);
                    }

                    //在线操作
                    if(userPortList.containsKey(messageVo.getToId())){
                        Socket client = clientSocketList.get(userPortList.get(messageVo.getToId()));
                        //把信息输出到，当前连接的所有客户端。
                        if (!client.isClosed()) {//防止发现送消息给，，断连客户端。
                            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                            oos.writeObject(messageVo.getMessage());
                            oos.flush();
//                            log.log(Level.INFO, "服务器发送内容：" + messageVo.getMessage());//+ client.toString() + "  "
                        } else {//断开的Socket就移除
                            clientSocketList.remove(port);//移除
                            log.log(Level.INFO, "服务器断开连接");
                        }
                    }
                }
            } catch (SocketException e) {
                log.log(Level.INFO, "客户端断开连接！！！" + e.getMessage());
            } catch (ClassNotFoundException | IOException e) {
                log.log(Level.INFO, e.getMessage());
            }
        }
    }
}