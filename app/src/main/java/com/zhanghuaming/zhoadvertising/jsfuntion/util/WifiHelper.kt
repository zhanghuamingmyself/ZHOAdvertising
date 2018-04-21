package com.zhanghuaming.myadvertising.jsfuntion.util

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.widget.Toast
import android.net.wifi.WifiManager.WifiLock
import android.net.wifi.WifiInfo
import android.util.Log
import com.zhanghuaming.zhoadvertising.App


class WifiHelper {

    companion object {

        private val TAG: String = WifiHelper::class.java.simpleName
        private val WIFICIPHER_NOPASS = 0
        private val WIFICIPHER_WEP = 1
        private val WIFICIPHER_WPA = 2
        // 定义WifiManager对象
        private val mWifiManager: WifiManager? = App.get().applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        // 定义WifiInfo对象
        private var mWifiInfo: WifiInfo? = null
        // 扫描出的网络连接列表
        private var mWifiList: List<ScanResult>? = null
        // 网络连接列表
        private var mWifiConfiguration: List<WifiConfiguration>? = null
        // 定义一个WifiLock
        var mWifiLock: WifiLock? = null


        /**
         * 获取wifi列表
         */
        @JvmStatic
        fun getWifiList(context: Context): List<ScanResult> {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (!wifiManager.isWifiEnabled) {
                wifiManager.isWifiEnabled = true
            }
            wifiManager.startScan()
            val wifiList = wifiManager.scanResults
            return if (wifiList.size > 0) wifiList else ArrayList()
        }

        // 打开WIFI
        @JvmStatic
        fun openWifi(context: Context) {
            if (!mWifiManager!!.isWifiEnabled()) {
                mWifiManager!!.setWifiEnabled(true)
            } else if (mWifiManager!!.getWifiState() === WifiManager.WIFI_STATE_ENABLING) {
                Toast.makeText(context, "亲，Wifi正在开启，不用再开了", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "亲，Wifi已经开启,不用再开了", Toast.LENGTH_SHORT).show()
            }
        }

        // 关闭WIFI
        @JvmStatic
        fun closeWifi(context: Context) {
            if (mWifiManager!!.isWifiEnabled()) {
                mWifiManager!!.setWifiEnabled(false)
            } else if (mWifiManager!!.getWifiState() === WifiManager.WIFI_STATE_DISABLED) {
                Toast.makeText(context, "亲，Wifi已经关闭，不用再关了", Toast.LENGTH_SHORT).show()
            } else if (mWifiManager!!.getWifiState() === WifiManager.WIFI_STATE_DISABLING) {
                Toast.makeText(context, "亲，Wifi正在关闭，不用再关了", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "请重新关闭", Toast.LENGTH_SHORT).show()
            }
        }

        // 检查当前WIFI状态
        @JvmStatic
        fun checkState(context: Context): Int {
            if (mWifiManager!!.getWifiState() === WifiManager.WIFI_STATE_DISABLING) {
                Toast.makeText(context, "Wifi正在关闭", Toast.LENGTH_SHORT).show()
                return WifiManager.WIFI_STATE_DISABLING
            } else if (mWifiManager!!.getWifiState() === WifiManager.WIFI_STATE_DISABLED) {
                Toast.makeText(context, "Wifi已经关闭", Toast.LENGTH_SHORT).show()
                return WifiManager.WIFI_STATE_DISABLED
            } else if (mWifiManager!!.getWifiState() === WifiManager.WIFI_STATE_ENABLING) {
                Toast.makeText(context, "Wifi正在开启", Toast.LENGTH_SHORT).show()
                return WifiManager.WIFI_STATE_ENABLING
            } else if (mWifiManager!!.getWifiState() === WifiManager.WIFI_STATE_ENABLED) {
                Toast.makeText(context, "Wifi已经开启", Toast.LENGTH_SHORT).show()
                return WifiManager.WIFI_STATE_ENABLED
            } else {
                Toast.makeText(context, "没有获取到WiFi状态", Toast.LENGTH_SHORT).show()
                return 4
            }
        }

        // 锁定WifiLock
        @JvmStatic
        fun acquireWifiLock() {
            mWifiLock!!.acquire()
        }

        // 解锁WifiLock
        @JvmStatic
        fun releaseWifiLock() {
            // 判断时候锁定
            if (mWifiLock!!.isHeld()) {
                mWifiLock!!.acquire()
            }
        }

        // 创建一个WifiLock
        @JvmStatic
        fun creatWifiLock() {
            mWifiLock = mWifiManager!!.createWifiLock("Test")
        }

        // 得到配置好的网络
        @JvmStatic
        fun getConfiguration(): List<WifiConfiguration>? {
            return mWifiConfiguration
        }

        // 指定配置好的网络进行连接
        @JvmStatic
        fun connectConfiguration(index: Int) {
            // 索引大于配置好的网络索引返回
            if (index > mWifiConfiguration!!.size) {
                return
            }
            // 连接配置好的指定ID的网络
            mWifiManager!!.enableNetwork(mWifiConfiguration!!.get(index).networkId,
                    true)
        }

        @JvmStatic
        fun startScan(context: Context) {
            mWifiManager!!.startScan()
            // 得到扫描结果
            mWifiList = mWifiManager!!.getScanResults()
            // 得到配置好的网络连接
            mWifiConfiguration = mWifiManager!!.getConfiguredNetworks()
            if (mWifiList == null) {
                if (mWifiManager!!.getWifiState() === WifiManager.WIFI_STATE_ENABLED) {
                    Toast.makeText(context, "当前区域没有无线网络", Toast.LENGTH_SHORT).show()
                } else if (mWifiManager!!.getWifiState() === WifiManager.WIFI_STATE_ENABLING) {
                    Toast.makeText(context, "WiFi正在开启，请稍后重新点击扫描", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "WiFi没有开启，无法扫描", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 得到网络列表
        @JvmStatic
        fun getWifiList(): List<ScanResult>? {
            return mWifiList
        }

        // 查看扫描结果
        @JvmStatic
        fun lookUpScan(): StringBuilder {
            val stringBuilder = StringBuilder()
            for (i in 0 until mWifiList!!.size) {
                stringBuilder
                        .append("Index_" + (i + 1).toString() + ":")
                // 将ScanResult信息转换成一个字符串包
                // 其中把包括：BSSID、SSID、capabilities、frequency、level
                stringBuilder.append(mWifiList!!.get(i).toString())
                stringBuilder.append("/n")
            }
            return stringBuilder
        }

        // 得到MAC地址
        @JvmStatic
        fun getMacAddress(): String? {

            mWifiInfo = mWifiManager!!.connectionInfo
            return mWifiInfo!!.macAddress

        }

        // 得到接入点的BSSID
        @JvmStatic
        fun getBSSID(): String? {

            mWifiInfo = mWifiManager!!.connectionInfo
            return mWifiInfo!!.bssid

        }

        // 得到IP地址
        @JvmStatic
        fun getIPAddress(): Int? {

            mWifiInfo = mWifiManager!!.connectionInfo
            return mWifiInfo!!.ipAddress

        }

        // 得到连接的ID
        @JvmStatic
        fun getNetworkId(): Int? {

            mWifiInfo = mWifiManager!!.connectionInfo
            return mWifiInfo!!.networkId

        }

        // 得到WifiInfo的所有信息包
        @JvmStatic
        fun getWifiInfo(): WifiInfo? {

            mWifiInfo = mWifiManager!!.connectionInfo
            return mWifiInfo
        }

        // 添加一个网络并连接
        @JvmStatic
        fun addNetwork(wcg: WifiConfiguration): Boolean {
            val wcgID = mWifiManager!!.addNetwork(wcg)
            val b = mWifiManager!!.enableNetwork(wcgID, true)
            Log.i(TAG,"a--$wcgID")
            Log.i(TAG,"b--$b")
            return b
        }

        @JvmStatic
        fun isExist(ssid: String): WifiConfiguration? {
            val configs = mWifiManager!!.getConfiguredNetworks()

            for (config in configs) {
                if (config.SSID == "\"" + ssid + "\"") {
                    return config
                }
            }
            return null
        }

        @JvmStatic
        fun createWifiConfig(ssid: String, password: String, type: Int): WifiConfiguration {
            //初始化WifiConfiguration
            val config = WifiConfiguration()
            config.allowedAuthAlgorithms.clear()
            config.allowedGroupCiphers.clear()
            config.allowedKeyManagement.clear()
            config.allowedPairwiseCiphers.clear()
            config.allowedProtocols.clear()

            //指定对应的SSID
            config.SSID = "\"" + ssid + "\""

            //如果之前有类似的配置
            val tempConfig = isExist(ssid)
            if (tempConfig != null) {
                //则清除旧有配置
                mWifiManager!!.removeNetwork(tempConfig!!.networkId)
            }

            //不需要密码的场景
            if (type == WIFICIPHER_NOPASS) {
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                //以WEP加密的场景
            } else if (type == WIFICIPHER_WEP) {
                config.hiddenSSID = true
                config.wepKeys[0] = "\"" + password + "\""
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED)
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                config.wepTxKeyIndex = 0
                //以WPA加密的场景，自己测试时，发现热点以WPA2建立时，同样可以用这种配置连接
            } else if (type == WIFICIPHER_WPA) {
                config.preSharedKey = "\"" + password + "\""
                config.hiddenSSID = true
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                config.status = WifiConfiguration.Status.ENABLED
            }

            return config
        }

        // 断开指定ID的网络
        @JvmStatic
        fun disconnectWifi(netId: Int) :Int{

            if (mWifiManager!!.disableNetwork(netId) && mWifiManager!!.disconnect()) {
                return 1
            } else {
                return 0
            }
        }

        @JvmStatic
        fun removeWifi(netId: Int) {
            disconnectWifi(netId)
            mWifiManager!!.removeNetwork(netId)
        }


    }
}