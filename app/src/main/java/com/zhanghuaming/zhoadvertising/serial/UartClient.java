package com.zhanghuaming.zhoadvertising.serial;

import android.util.Log;


import com.zhanghuaming.zhoadvertising.Config;
import com.zhanghuaming.zhoadvertising.tcp.TcpBack;
import com.zhanghuaming.zhoadvertising.tcp.TcpHelper;
import com.zhanghuaming.zhoadvertising.util.GetCharAscii;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.zhanghuaming.zhoadvertising.util.GetCharAscii.Ascii2StringEN;


/**
 * Created by Administrator on 2016/12/8.
 */
public class UartClient {
    private String TAG = UartClient.class.getSimpleName();
    static UartClient mUartClient;
    boolean isRunning = false;
    private int num = 0;

    private String tcpIP = null;
    private int tcpPort = 0;
    private String tcpLoginMSG = null;
    public static InputStream is = null;
    public static OutputStream os = null;

    public static UartClient getInstance() {
        if (mUartClient == null) {
            mUartClient = new UartClient();
        }

        return mUartClient;
    }

    private UartClient() {
    }

    public static boolean sendMsg(byte[] buf) {
        if (os != null) {
            try {
                os.write(buf);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    class UartThread extends Thread {

        public UartThread() {
        }

        @Override
        public void run() {
            Log.d(TAG, "线程开了");
            try {
                is = UartUtils.getInstance().getInputStream();
                os = UartUtils.getInstance().getOutputStream();
                if (is == null ) {
                    isRunning = false;
                    Log.d(TAG, "is == null");
                    return;
                }
                if(os !=null){
                    String getIp = ":>1\n";
                    sendMsg(GetCharAscii.stringEN2Ascii(getIp));
                }else {
                    isRunning = false;
                    Log.d(TAG, "os == null");
                    return;
                }
                isRunning = true;
                byte[] cache = null;
                byte[] buf = new byte[100];
                int index = 0;
                Log.d(TAG, "进入while前");
                byte[] temp;
                while (isRunning) {
                    num++;
                    Log.d(TAG, "num=" + num);
                    try {
                        sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int ret = is.read(buf);
                    Log.d(TAG, "ret=" + ret);
                    Log.i(TAG, "串口单次内容----" + Ascii2StringEN(buf, index, ret));
                    if (ret > 0) {
                        if (cache != null) {
                            temp = new byte[cache.length + ret];
                            System.arraycopy(cache, 0, temp, 0, cache.length);
                            System.arraycopy(buf, 0, temp, cache.length, ret);
                            cache = temp;
                            Log.i(TAG, "全部数据为---" + Ascii2StringEN(cache, 0, cache.length));
                        } else {
                            cache = new byte[ret];
                            System.arraycopy(buf, 0, cache, 0, ret);
                            Log.i(TAG, "全部数据为---" + Ascii2StringEN(cache, 0, cache.length));
                        }

                        cache = analysis(cache);//解析部分
                        if(cache!=null) {
                        //    Log.i(TAG, "return ----cache--" + Ascii2StringEN(cache, 0, cache.length));
                        }
                    } else {
                        //不会执行，read会一直阻塞
                        Log.d(TAG, "read from uart empty");
                    }

                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                isRunning = false;
                Log.d(TAG, e.getMessage());

            }
            Log.e(TAG, "UartKeyBoardClient run end");
        }
    }

    public byte[] analysis(byte[] buf) {
        if (Config.mode == Config.modeLogin) {
            String msg = Ascii2StringEN(buf, 0, buf.length);
            //还要进行去头处理哦
            int indexStart = msg.indexOf(":>");
            if(indexStart == -1){
                return buf;
            } else if (indexStart != 0) {
                msg = msg.substring(indexStart, msg.length());
            }


            int indexEnd = msg.indexOf('\n');
            if (indexEnd != -1) {
                if (msg.charAt(2) == '1') {
                     TcpHelper.ip = msg.substring(3, indexEnd);
                    sendMsg(GetCharAscii.stringEN2Ascii(":>2\n"));
                } else if (msg.charAt(2) == '2') {
                    TcpHelper.port =Integer.parseInt(msg.substring(3, indexEnd));
                    initTcp();
                    sendMsg(GetCharAscii.stringEN2Ascii(":>3\n"));

                } else if (msg.charAt(2) == '3') {
                    //Login@SN52087@PFVSwbNhLR@V1
                    Log.i(TAG,"backlogin---"+msg.substring(3, indexEnd));

                    tcpLogin(msg.substring(3, indexEnd));
                    Config.mode = Config.modeRelay;
                    //serialBack.backLogin("Login@SN52087@PFVSwbNhLR@V1");
                } else return null;
                Log.i(TAG,"msg.length()"+msg.length()+"---"+"indexEnd"+(indexEnd + 2));
                if (msg.length() == (indexEnd + 2)) {
                    return null;
                } else {
                    byte[] temp = new byte[msg.length() - indexEnd - 1];
                    System.arraycopy(buf, indexEnd, temp, 0, msg.length() - indexEnd - 1);
                    return temp;
                }

            } else return buf;

        } else if (Config.mode == Config.modeRelay) {
            String msg = Ascii2StringEN(buf, 0, buf.length);
            msg+='\n';
            tcpHelper.send(msg);
        }
        return null;
    }

    public void start() {
        Log.e(TAG, "UartKeyBoardClient start()");
        if (!isRunning) {
            new UartThread().start();
        }
    }

    private TcpHelper tcpHelper;

    public void initTcp() {
        tcpHelper = TcpHelper.getInstance();
        tcpHelper.setTcpBack(new TcpBack() {
            @Override
            public void receive(String rec) {
                Log.i(TAG, "从服务器收到信息----" + rec+'\n');
                if (Config.mode == Config.modeLogin) {
                        Log.i(TAG,"mode is login");
                } else if (Config.mode == Config.modeRelay) {
                    sendMsg(GetCharAscii.stringEN2Ascii(rec+'\n'));
                }
            }

            @Override
            public void sendError(String e) {

            }

            @Override
            public void receiveError(String e) {

            }

            @Override
            public void errorBack(String e) {

            }


        });
//        tcpHelper.ip="zhi.zhenhuaonline.cn";
//        tcpHelper.port=2345;
        tcpHelper.start();


    }

    public void tcpLogin(String str) {
        tcpHelper.login(str);
    }

    public void testHeard() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    tcpHelper.send("Headt");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
