package com.zhanghuaming.zhoadvertising.jsfuntion;

import android.content.Context;
import android.util.Log;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.zhanghuaming.zhoadvertising.App;
import com.zhanghuaming.zhoadvertising.Config;
import com.zhanghuaming.zhoadvertising.jsfuntion.bean.TaskStatus;

import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DownLoadMethod {
    private final static String TAG = "FileMethods";
    private Context mContext = null;
    private String basePah = Config.basePath;
    private XWalkView webView = null;
    private static Map<Integer , String> Mstatues = new HashMap<>();
    private static Map<Integer,String>MtotalBytes = new HashMap<>();
    private static Map<Integer,String>MPath = new HashMap<>();
    public DownLoadMethod(Context mContext,XWalkView webView) {
        this.mContext = mContext;
        this.webView = webView;
    }


    /**
     * 建立下载任务
     * @param url
     * @param saveName
     * @param callback
     */
    @JavascriptInterface
    public int createTask(String url, String saveName, String callback){
        return createDownloadTask(url,Config.basePath+saveName,callback);
    }

    /**
     * 获取下载任务状态
     */
    @JavascriptInterface
    public String checkTaskStatus(int taskId){
        try {
            TaskStatus status = new TaskStatus();
            status.downLength = Mstatues.get(taskId);
            status.length = MtotalBytes.get(taskId);
            status.taskId = "" + taskId;
            return App.get().getGson().toJson(status);
        }catch (Exception e){
            e.printStackTrace();
            TaskStatus status = new TaskStatus();
            status.downLength = "0";
            status.length = "0";
            status.taskId = "0";
            return App.get().getGson().toJson(status);
        }

    }

    /**
     * 取消下载任务
     * cancelTask
     * @return
     */
    @JavascriptInterface
    public int cancelTask(int taskId){
        try {
            FileDownloader.getImpl().pause(taskId);
            new File(MPath.get(taskId)).delete();
            new File(FileDownloadUtils.getTempPath(MPath.get(taskId))).delete();
            return 1;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }

    private int createDownloadTask(String url, String path, final String callback) {
        boolean isDir = false;

        final int id = FileDownloader.getImpl().create(url)
                .setPath(path, isDir)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
                .setListener(new FileDownloadSampleListener() {

                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.pending(task, soFarBytes, totalBytes);
                        Log.i(TAG, "-----------pending---------" + soFarBytes);
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.progress(task, soFarBytes, totalBytes);
                        Log.i(TAG, "-----------progress---------" + soFarBytes);
                        Mstatues.put(task.getId(),""+soFarBytes);
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        Log.i(TAG, "-----------error---------" + e.getMessage());
                        DownLoadMethod.this.webView.loadUrl("javascript:" + callback+"("+task.getId()+", 0)");
                        MPath.remove(task.getId());
                        Mstatues.remove(task.getId());
                        MtotalBytes.remove(task.getId());
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                        Log.i(TAG, "-----------connected---------" + soFarBytes);
                        MtotalBytes.put(task.getId(),""+totalBytes);
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.paused(task, soFarBytes, totalBytes);
                        Log.i(TAG, "-----------paused---------" + soFarBytes);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        String js = "javascript:" + callback+"("+task.getId()+", 1)";
                        Log.i(TAG, "-----------completed---------"+js);
                        DownLoadMethod.this.webView.loadUrl(js);
                        MPath.remove(task.getId());
                        Mstatues.remove(task.getId());
                        MtotalBytes.remove(task.getId());
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        super.warn(task);
                        Log.i(TAG, "-----------warn---------");
                    }
                }).start();
        MPath.put(id,path);
        return id;
    }


}
