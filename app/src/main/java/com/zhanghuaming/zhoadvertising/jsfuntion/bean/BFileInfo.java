package com.zhanghuaming.zhoadvertising.jsfuntion.bean;

public class BFileInfo {

    public Long length ;// 文件大小（单位:B）
    public Long lastModified ; // 文件最后修改时间（时间戳）
    public int isFile = 0;// 是否是文件类型。[0]不是 [1]是
    public int isDirectory = 0; // 是否是文件夹类型。[0]不是 [1]是
    public int isExist = 0;//是否存在 [0]不是 [1]是

}
