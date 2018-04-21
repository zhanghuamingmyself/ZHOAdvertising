package com.zhanghuaming.zhoadvertising.jsfuntion;

import android.content.Context;
import android.util.Log;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.zhanghuaming.myadvertising.jsfuntion.util.FileHelper;
import com.zhanghuaming.zhoadvertising.App;
import com.zhanghuaming.zhoadvertising.Config;
import com.zhanghuaming.zhoadvertising.jsfuntion.bean.BFileInfo;
import com.zhanghuaming.zhoadvertising.jsfuntion.bean.BFileList;
import com.zhanghuaming.zhoadvertising.util.ZipExtractorTask;


import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileMethod {
    private final static String TAG = "FileMethods";
    private Context mContext = null;
    private String basePah = Config.basePath;

    public FileMethod(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 获取文件列表(可获取目录)
     */

    @JavascriptInterface
    public String getFileLists(String dir) {
        File d = new File(basePah + dir);

        if (!d.exists() || !d.isDirectory()) {
            Log.i(TAG, "getList---" + d.getPath() + "--" + d.mkdirs());
            d.mkdirs();
        }
        File[] fList = d.listFiles();
        BFileList bean = new BFileList();
        bean.lists = new ArrayList<>();
        if (fList != null && fList.length != 0) {
            for (int i = 0; i < fList.length; i++) {
                if (!fList[i].isDirectory()) {
                    bean.lists.add(fList[i].getPath().replace(d.getPath() + "/", ""));
                } else {
                    bean.lists.add("/" + fList[i].getPath().replace(d.getPath() + "/", ""));
                }
            }

            return App.get().getGson().toJson(bean);
        } else return "{\"list\":[]}";

    }


    /**
     * 检查文件是否存在
     */

    @JavascriptInterface
    public int isExist(String fileName) {
        File d = new File(basePah + fileName);
        Log.i(TAG, "isExist-----" + d.getPath());
        if (d.exists()) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 获取文件/文件夹信息
     */

    @JavascriptInterface
    public String getFileInfo(String fileName) {
        BFileInfo bean = new BFileInfo();
        File f = new File(basePah + fileName);
        if (f.exists()) {
            bean.isExist = 1;
            if (f.isDirectory()) {
                bean.isDirectory = 1;
            } else {
                bean.isDirectory = 0;
            }
            if (f.isFile()) {
                bean.isFile = 1;
            } else {
                bean.isFile = 0;
            }
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                bean.length = f.length();
                bean.lastModified = f.lastModified();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            bean.isExist = 0;
        }
        return App.get().getGson().toJson(bean);
    }


    /**
     * 移动文件/文件夹
     */
    @JavascriptInterface
    public int moveFile(String fileName, String path) {
        if (FileHelper.move(basePah + fileName, basePah + path)) {
            return 1;
        } else {
            return 0;
        }

    }

    /**
     * 删除文件/文件夹
     */
    @JavascriptInterface
    public int deleteFile(String fileName) {
        File f = new File(basePah + fileName);
        if (f.isDirectory()) {
            return FileHelper.deleteDirWihtFile(f);
        } else if (f.delete()) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 解压文件
     * fileName：文件路径(相对于app根目录)
     * pathName：解压目录(相对于app根目录)
     */
    @JavascriptInterface
    public int unzip(String fileName, String pathName) {
        try {
            ZipExtractorTask task = new ZipExtractorTask(basePah + fileName, basePah + pathName, mContext, true);
            task.execute();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

}
