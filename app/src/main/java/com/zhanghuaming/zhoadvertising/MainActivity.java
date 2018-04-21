/*
 * Copyright © 2016 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhanghuaming.zhoadvertising;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.zhanghuaming.zhoadvertising.jsfuntion.BrightMethod;
import com.zhanghuaming.zhoadvertising.jsfuntion.ControlMethod;
import com.zhanghuaming.zhoadvertising.jsfuntion.DownLoadMethod;
import com.zhanghuaming.zhoadvertising.jsfuntion.FileMethod;
import com.zhanghuaming.zhoadvertising.jsfuntion.NetworkMethod;
import com.zhanghuaming.zhoadvertising.jsfuntion.VolumeMethod;
import com.zhanghuaming.zhoadvertising.jsfuntion.WifiMethod;
import com.zhanghuaming.zhoadvertising.serial.UartClient;
import com.zhanghuaming.zhoadvertising.util.IPUtils;

import org.xwalk.core.XWalkView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Yan Zhenjie on 2016/6/13.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private ServerManager mServerManager;

    private Button mBtnStart;
    private Button mBtnStop;
    private Button mBtnBrowser, btnPlay;
    private TextView mTvMessage;

    private Button mBtnTestDownLoad, mBtnPurseDownLoad, mBtnDelDownLoad;
    private String mRootUrl;
    XWalkView webView;

    private String llsApkFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        // AndServer run in the service.
        initServer();


        initSerial();



    }
    private UartClient uartClient;
    public void initSerial(){
        uartClient = UartClient.getInstance();
        uartClient.start();
    }
    private int downloadId1;
    void initView() {
        final VideoView videoView = (VideoView) findViewById(R.id.videoView);

        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(Uri.parse("http://"+ IPUtils.getIpAddress(this)+":8080/1.mp4"));
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(MainActivity.this, "播放完成了", Toast.LENGTH_SHORT).show();
            }
        });
        webView = (XWalkView) findViewById(R.id.webView);
        webView.addJavascriptInterface(new NetworkMethod(this), "network");
        webView.addJavascriptInterface(new FileMethod(this), "file");
        webView.addJavascriptInterface(new ControlMethod(this), "app");
        webView.addJavascriptInterface(new BrightMethod(this), "bright");
        webView.addJavascriptInterface(new VolumeMethod(this), "volume");
        webView.addJavascriptInterface(new DownLoadMethod(this,webView), "down");
        webView.addJavascriptInterface(new WifiMethod(this), "wifi");
        webView.loadUrl("http://ztn-test.com/public/static/my-advertising/kotlinhtml.html");
        mBtnStart = (Button) findViewById(R.id.btn_start);
        mBtnStop = (Button) findViewById(R.id.btn_stop);
        mBtnBrowser = (Button) findViewById(R.id.btn_browse);
        mTvMessage = (TextView) findViewById(R.id.tv_message);

        btnPlay = (Button) findViewById(R.id.play);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.start();
            }
        });
        mBtnStart.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
        mBtnBrowser.setOnClickListener(this);


        mBtnTestDownLoad = (Button) findViewById(R.id.testdownload);
        mBtnTestDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://cdn.llsapp.com/android/LLS-v4.0-595-20160908-143200.apk";
                String path = FileDownloadUtils.getDefaultSaveRootPath() + File.separator + "tmpdir1";
                Log.i(TAG,"下载地址"+path);
                 downloadId1 = createDownloadTask(url,path).start();
            }
        });
        mBtnPurseDownLoad = (Button) findViewById(R.id.puserdownload);
        mBtnPurseDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileDownloader.getImpl().pause(downloadId1);
            }
        });
        mBtnDelDownLoad = (Button) findViewById(R.id.deldownload);
        mBtnDelDownLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new File(llsApkFilePath).delete();
                new File(FileDownloadUtils.getTempPath(llsApkFilePath)).delete();
            }
        });
    }

    void initServer() {
        mServerManager = new ServerManager(this);
        mServerManager.register();
        mServerManager.startService();
        // startServer;
        mBtnStart.performClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mServerManager.unRegister();
    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        /**
//         * 激活WebView为活跃状态，能正常执行网页的响应
//         */
//        webView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        /**
//         * onPause动作通知内核暂停所有的动作，比如DOM的解析、plugin的执行、JavaScript执行
//         */
//        webView.onPause();
//    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_start: {
                mServerManager.startService();
                break;
            }
            case R.id.btn_stop: {
                mServerManager.stopService();
                break;
            }
            case R.id.btn_browse: {
                if (!TextUtils.isEmpty(mRootUrl)) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(Uri.parse(mRootUrl));
                    startActivity(intent);
                }
                break;
            }
        }
    }

    /**
     * Start notify.
     */
    public void serverStart(String ip) {
        mBtnStart.setVisibility(View.GONE);
        mBtnStop.setVisibility(View.VISIBLE);
        mBtnBrowser.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(ip)) {
            List<String> addressList = new LinkedList<>();
            mRootUrl = "http://" + ip + ":8080/";
            addressList.add(mRootUrl);
            addressList.add("http://" + ip + ":8080/login.html");
            addressList.add("http://" + ip + ":8080/image");
            addressList.add("http://" + ip + ":8080/download");
            addressList.add("http://" + ip + ":8080/upload");
            mTvMessage.setText(TextUtils.join("\n", addressList));
        } else {
            mRootUrl = null;
            mTvMessage.setText("server_ip_error");
        }
    }

    /**
     * Error notify.
     */
    public void serverError(String message) {
        Log.i(TAG, "--------------serverError==" + message);
        mRootUrl = null;
        mBtnStart.setVisibility(View.VISIBLE);
        mBtnStop.setVisibility(View.GONE);
        mBtnBrowser.setVisibility(View.GONE);
        mTvMessage.setText(message);
    }

    /**
     * Stop notify.
     */
    public void serverStop() {
        Log.i(TAG, "--------------serverStop");
        mRootUrl = null;
        mBtnStart.setVisibility(View.VISIBLE);
        mBtnStop.setVisibility(View.GONE);
        mBtnBrowser.setVisibility(View.GONE);
        mTvMessage.setText("server_stop_succeed");
    }


    private BaseDownloadTask createDownloadTask(String url,String path) {


        boolean isDir = true;

        return FileDownloader.getImpl().create(url)
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
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        Log.i(TAG, "-----------error---------" + e.getMessage());
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                        Log.i(TAG, "-----------connected---------" + soFarBytes);
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.paused(task, soFarBytes, totalBytes);
                        Log.i(TAG, "-----------paused---------" + soFarBytes);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        Log.i(TAG, "-----------completed---------");
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        super.warn(task);
                        Log.i(TAG, "-----------warn---------");
                    }
                });
    }


}
