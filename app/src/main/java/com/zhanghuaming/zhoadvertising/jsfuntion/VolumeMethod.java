package com.zhanghuaming.zhoadvertising.jsfuntion;

import android.content.Context;
import android.media.AudioManager;

import com.zhanghuaming.myadvertising.jsfuntion.util.ControlHelper;
import com.zhanghuaming.zhoadvertising.Config;

import org.xwalk.core.JavascriptInterface;

public class VolumeMethod {
    private final static String TAG = "FileMethods";
    private Context mContext = null;
    private String basePah = Config.basePath;
    //定义AudioManager
    private AudioManager mgr;

    public VolumeMethod(Context mContext) {
        this.mContext = mContext;
        //实例化
        mgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }


    @JavascriptInterface
    public int getCurrentValue() {
        return  mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @JavascriptInterface
    public int setValue(int val) {
        // 调低音量
        mgr.setStreamVolume(AudioManager.STREAM_MUSIC,val,0);
        return 1;
    }
}
