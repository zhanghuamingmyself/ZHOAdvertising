package com.zhanghuaming.zhoadvertising.jsfuntion;

import android.content.Context;

import com.zhanghuaming.myadvertising.jsfuntion.util.ControlHelper;
import com.zhanghuaming.zhoadvertising.Config;
import com.zhanghuaming.zhoadvertising.util.IPUtils;

import org.xwalk.core.JavascriptInterface;

public class BrightMethod {
    private final static String TAG = "FileMethods";
    private Context mContext = null;
    private String basePah = Config.basePath;

    public BrightMethod(Context mContext) {
        this.mContext = mContext;
    }


    @JavascriptInterface
    public int getCurrentValue() {
        return ControlHelper.getScreenBrightness(mContext);

    }
    @JavascriptInterface
    public int setValue(int val) {
        return ControlHelper.saveScreenBrightness(val,mContext);
    }
}
