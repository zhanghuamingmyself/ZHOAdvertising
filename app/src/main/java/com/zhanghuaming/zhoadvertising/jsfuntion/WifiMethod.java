package com.zhanghuaming.zhoadvertising.jsfuntion;

import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.zhanghuaming.myadvertising.jsfuntion.util.WifiHelper;
import com.zhanghuaming.zhoadvertising.App;
import com.zhanghuaming.zhoadvertising.Config;
import com.zhanghuaming.zhoadvertising.jsfuntion.bean.NowWifiInfoBean;
import com.zhanghuaming.zhoadvertising.jsfuntion.bean.WifiInfoBean;
import com.zhanghuaming.zhoadvertising.jsfuntion.bean.WifiListBean;

import org.xwalk.core.JavascriptInterface;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class WifiMethod {

    private final static String TAG = "WifiMethod";
    private Context mContext = null;
    private String basePah = Config.basePath;

    public WifiMethod(Context mContext) {
        this.mContext = mContext;
    }

    @JavascriptInterface
    public String getWifiLists() {
        WifiHelper.openWifi(mContext);
        List<ScanResult> list = WifiHelper.getWifiList(mContext);
        WifiListBean wifiListBean = new WifiListBean();
        wifiListBean.wifiLists = new ArrayList<>();
        WifiInfoBean wifiInfoBean;
        for (int i = 0; i < list.size(); i++) {
            wifiInfoBean = new WifiInfoBean();
            wifiInfoBean.ssid = list.get(i).SSID;
            // wifiInfoBean.rssi = WifiManager.calculateSignalLevel(list.get(i).level,5);
            wifiInfoBean.rssi = list.get(i).level;
            wifiListBean.wifiLists.add(wifiInfoBean);
        }
        return App.get().getGson().toJson(wifiListBean);
    }

    @JavascriptInterface
    public int connectWifi(String wifiSsid, String wifiPassword) {
        if (WifiHelper.addNetwork(WifiHelper.createWifiConfig(wifiSsid, wifiPassword, 2))) {
            return 1;
        } else return 0;

    }

    @JavascriptInterface
    public String getCurrentWifiInfo() {
        NowWifiInfoBean nowWifiInfoBean = new NowWifiInfoBean();
        nowWifiInfoBean.wifiState = WifiHelper.checkState(mContext);
        nowWifiInfoBean.wifiInfo = new NowWifiInfoBean.Info();
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            WifiInfo wifiInfo = WifiHelper.getWifiInfo();
            nowWifiInfoBean.wifiInfo.bssid = wifiInfo.getBSSID();
            nowWifiInfoBean.wifiInfo.ipAddress = wifiInfo.getIpAddress();
            nowWifiInfoBean.wifiInfo.linkSpeed = wifiInfo.getLinkSpeed();
            nowWifiInfoBean.wifiInfo.macAddress = wifiInfo.getMacAddress();
            nowWifiInfoBean.wifiInfo.networkId = wifiInfo.getNetworkId();
            nowWifiInfoBean.wifiInfo.rssi = wifiInfo.getRssi();
            nowWifiInfoBean.wifiInfo.ssid = wifiInfo.getSSID();
        } else {
            nowWifiInfoBean.wifiInfo = null;
        }
        return App.get().getGson().toJson(nowWifiInfoBean);
    }

    @JavascriptInterface
    public int disconnectWifi(){
        return WifiHelper.disconnectWifi(WifiHelper.getWifiInfo().getNetworkId());
    }

    @JavascriptInterface
    public int clearWifi(String ssid){
        return  0;
    }
}
