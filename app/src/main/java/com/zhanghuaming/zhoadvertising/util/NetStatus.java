package com.zhanghuaming.zhoadvertising.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.zhanghuaming.zhoadvertising.App;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/2/6.
 */
public class NetStatus {

    private static final String TAG = NetStatus.class.getSimpleName();
    ///测试网络

    static boolean mWifiConnected ;
    static boolean mNetValid ;

    public static Handler getHandler() {
        return mHandler;
    }

    public static void setHandler(Handler mHandler) {
        NetStatus.mHandler = mHandler;
    }

    static Handler mHandler;

    public static boolean isWifiConnected() {
        return mWifiConnected;
    }


    public static boolean isNetValid() {
        return mNetValid;
    }

    public static void getNetStatus(){
        mWifiConnected = isNetworkConnected(App.get());
        if(mWifiConnected){
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    getNetConnect();
                }
            }.start();
        }
    }
   static boolean getNetConnect(){
        try {
            URL url = new URL("http://www.baidu.com");
            HttpURLConnection httpConn =(HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(6 * 1000); // 连接超时为10秒
            httpConn.setReadTimeout(3 * 1000);// 连接超时为3秒
            httpConn.connect();
            InputStream is = httpConn.getInputStream();
            byte[] buf = new byte[16];
            int len = is.read(buf);
            Log.i(TAG,"len:" + len);
            httpConn.disconnect();
            mNetValid = true;
            sendMsg();
            return  true;

        } catch (Exception e) {
            e.printStackTrace();
            sendMsg();
            mNetValid = false;
            return false;
        }

    }
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    static void sendMsg(){
        if(mHandler != null){
//            mHandler.sendEmptyMessage(UIService.MSG_NET_STATE);

        }
    }
}
