package com.zhanghuaming.zhoadvertising.jsfuntion.bean;

import android.net.wifi.WifiInfo;

public class NowWifiInfoBean {
    public int wifiState;
    public Info wifiInfo;
    public static class Info{
        public String ssid = null;// [str] SSID地址
        public String bssid = null;// [str] BSSID地址
        public int ipAddress = 0; // [str] IP地址
        public String macAddress = null;// [str] MAC地址
        public int networkId = 0;// [str] 网络ID
        public int linkSpeed = 0; // [str] 连接速度
        public int rssi = 0;// [str] 信号强度
    }
}
