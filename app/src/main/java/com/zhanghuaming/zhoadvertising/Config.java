package com.zhanghuaming.zhoadvertising;

import android.os.Environment;

public class Config {
    public static String basePath = Environment.getExternalStorageDirectory().getPath() +"/ZTO/";
    public static final int BAUDRATE = 9600;//串口波特率
    public static final String TTY_PATH = "/dev/ttyS1";//串口位置;

    /**
     * 串口模式
     */
    public static int modeLogin = 1;
    public static final int modeRelay =2;
    public static int mode = 1;//模式
}
