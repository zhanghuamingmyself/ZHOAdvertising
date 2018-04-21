package com.zhanghuaming.zhoadvertising.jsfuntion;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;



import org.xwalk.core.JavascriptInterface;

public class NetworkMethod {
    private final static String TAG = "NetworkMethod";
    private Context mContext = null;

    public NetworkMethod(Context mContext) {
        this.mContext = mContext;
    }

    @JavascriptInterface
    public int getNetworkType(){
        return getNetworkState(mContext);
    }

    /**
     * 获取当前的网络状态是否可用
     *
     * @param context
     * @return
     */
    public static int getNetworkState(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取NetworkInfo对象
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        // 遍历每一个对象
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                // debug信息
                if (networkInfo.getTypeName().equals("WIFI")){
                    return 1;
                }
                Toast.makeText(context,"TypeName = " + networkInfo.getTypeName(),Toast.LENGTH_SHORT).show();
                // 网络状态可用
                return 2;
            }
        }
        // 没有可用的网络
        return 0;
    }

}
