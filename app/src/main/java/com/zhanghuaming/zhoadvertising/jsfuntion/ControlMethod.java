package com.zhanghuaming.zhoadvertising.jsfuntion;

import android.content.Context;

import com.zhanghuaming.myadvertising.jsfuntion.util.ControlHelper;
import com.zhanghuaming.zhoadvertising.Config;
import com.zhanghuaming.zhoadvertising.util.IPUtils;
import com.zhanghuaming.zhoadvertising.util.NetStatus;

import org.xwalk.core.JavascriptInterface;

public class ControlMethod {
    private final static String TAG = "FileMethods";
    private Context mContext = null;
    private String basePah = Config.basePath;

    public ControlMethod(Context mContext) {
        this.mContext = mContext;
    }

    @JavascriptInterface
    public String getVersion() {
        return ControlHelper.getVerName(mContext);

    }

    @JavascriptInterface
    public String getServerAddress() {
        return IPUtils.getIpAddress(mContext);

    }




}
