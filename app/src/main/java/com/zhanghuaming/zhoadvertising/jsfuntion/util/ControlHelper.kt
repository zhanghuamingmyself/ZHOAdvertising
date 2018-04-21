package com.zhanghuaming.myadvertising.jsfuntion.util

import android.content.pm.PackageManager
import android.app.Activity
import android.content.Context
import android.net.wifi.ScanResult
import android.provider.Settings


class ControlHelper {
    companion object {


    @JvmStatic fun getVersionCode(mContext: Context): Int {
        var versionCode = 0
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionCode
    }

        /**
         * 获取版本号名称
         *
         * @param context 上下文
         * @return
         */
        @JvmStatic fun getVerName(context: Context): String {
            var verName = ""
            try {
                verName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return verName
        }
    /**
     * 2. SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * 3. SCREEN_BRIGHTNESS_MODE_MANUAL=0  为手动调节屏幕亮度
     */
    @JvmStatic  fun getScreenMode(mContext: Context): Int {
        var screenMode = 0
        try {
            screenMode = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE)
        } catch (localException: Exception) {

        }

        return screenMode
    }

    /**
     * 保存当前的屏幕亮度值，并使之生效
     */
    @JvmStatic fun setScreenBrightness(paramInt: Int,mActivity: Activity) {
        val localWindow = mActivity.getWindow()
        val localLayoutParams = localWindow.getAttributes()
        val f = paramInt / 255.0f
        localLayoutParams.screenBrightness = f
        localWindow.setAttributes(localLayoutParams)
    }

    /**
     * 设置当前屏幕亮度值  0--255
     */
    @JvmStatic fun saveScreenBrightness(paramInt: Int,mContext: Context) :Int{
        try {
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, paramInt)
            return 1
        } catch (localException: Exception) {
            localException.printStackTrace()
            return 0
        }

    }

    /**
     * 设置当前屏幕亮度的模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0  为手动调节屏幕亮度
     */
    @JvmStatic fun setScreenMode(paramInt: Int,mContext: Context) {
        try {
            Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt)
        } catch (localException: Exception) {
            localException.printStackTrace()
        }

    }

    /**
     * 获得当前屏幕亮度值  0--255
     */
    @JvmStatic fun getScreenBrightness(mContext: Context): Int {
        var screenBrightness = 255
        try {
            screenBrightness = Settings.System.getInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS)
        } catch (localException: Exception) {

        }

        return screenBrightness
    }
    }

}