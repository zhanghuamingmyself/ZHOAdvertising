package com.zhanghuaming.zhoadvertising.tcp;

import android.util.Log;


import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by zhang on 2018/2/22.
 */

public class TcpHelper{
    public static int reconnectNum = 0;
    private static final String TAG = TcpHelper.class.getSimpleName();
    public static String ip = null;
    public static int port = 2345;
    private static Socket client = null;
    private static PrintStream out = null;//获取Socket的输出流，用来发送数据到服务端
    public static boolean status = true; //接收死循环
    private static DataInputStream input = null;
    private static TcpBack tcpBack;
    private static TcpHelper tcpHelper = null;
    private static boolean isConnect = false;
    public static TcpHelper getInstance() {
        if (tcpHelper == null) {
            return tcpHelper = new TcpHelper();
        } else return tcpHelper;
    }

    public static void setTcpBack(TcpBack back) {
        tcpBack = back;
    }

    public static boolean isReConnect = false;
    private static Subscription TimerSubscribe;
    public static void start() {
        TimerSubscribe = Observable.interval(0, 60000, TimeUnit.MILLISECONDS)//延时 ，每间隔，时间单位
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if(isConnect){
                            isConnect = false;
                        }else {
                            close();
                        }
                    }
                });
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        client = new Socket();
                        Log.i(TAG, "tcp ip---" + ip + "---port---" + port+"---loginInfo---"+loginInfo);
                        client.connect(new InetSocketAddress(ip, port), 3000);
                        client.setSoTimeout(0);
                        if (isReConnect) {
                            Log.i(TAG, "重新登录");
                            Thread.sleep(30000);
                            send(loginInfo);
                            isReConnect = false;
                            status = true;
                        }
                        Log.i(TAG, "tcp 状态client is null--" + (client == null) + "---client isClose--" + client.isClosed() + "---client.isConnect---" + client.isConnected() + "---client isInputShutdown----" + client.isInputShutdown() + "---client isOutputShutdown----" + client.isOutputShutdown());
                        while (status) {
                            if (client != null) {
                                try {
                                    try {
                                        client.sendUrgentData(0xFF); // 发送心跳包
                                    } catch (Exception e) {
                                        close();
                                        Thread.sleep(30000);
                                        e.printStackTrace();
                                    }
                                    input = new DataInputStream(client.getInputStream());
                                    out = new PrintStream(client.getOutputStream());
                                    if (input == null || out == null) {
                                        close();
                                    }
                                    byte[] buffer;
                                    buffer = new byte[input.available()];
                                    if (buffer.length != 0) {
                                        Log.i(TAG, "length=" + buffer.length);
                                        input.read(buffer);
                                        String three = new String(buffer);
                                        Log.i(TAG, "内容=" + three);
                                        isConnect = true;
                                        if (-1!=three.indexOf("RetryLogin")) {
                                            send(loginInfo);
                                        } else {
                                            tcpBack.receive(three + '\n');
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "客户端异常:" + e.getMessage());
                                    close();
                                }
                            } else {
                                try {
                                    Log.e(TAG, "client is null");
                                    close();
                                    Thread.sleep(30000);

                                } catch (Exception e) {
                                    Log.e(TAG, "tcp线程异常" + e.getMessage());
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "123Exception" + e.getMessage());
                        try {
                            close();
                            Thread.sleep(30000);
                        } catch (InterruptedException e1) {
                            close();
                            Log.e(TAG, "InterruptedException" + e1.getMessage());
                        }
                    }
                }
            }
        }).start();
    }

    private static String loginInfo = null;

    public void login(String login) {
        loginInfo = login;
        Log.i(TAG, "登录信息为" + login);
        send(login);
    }

    public static void close() {//不是彻底关闭的意思哦
        reconnectNum++;
        Log.i(TAG, "关闭socket" + reconnectNum);
        status = false;
        isReConnect = true;
        try {
            if (input != null) {
                input.close();
            }
            if (out != null) {
                out.close();
            }
            if (client != null) {
                client.close(); //只关闭socket，其关联的输入输出流也会被关闭
            }
            isConnect = true;
        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
        }
    }

    public static void send(String msg) {
        //发送数据到服务端
        try {
            out = new PrintStream(client.getOutputStream());
        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
            close();
        }
        if (out != null) {
            try {
                Log.i(TAG, "输入信息：" + msg);
                out.write(msg.getBytes());
                //  isConnect = true;
            } catch (Exception e) {
                Log.e(TAG, "输入信息error" + e.getMessage());
                close();
                tcpBack.sendError(e.getMessage());
            }
        } else {
            Log.i(TAG, "out is null");
            close();
            tcpBack.sendError(TcpBack.sendClose);
        }
    }


}
