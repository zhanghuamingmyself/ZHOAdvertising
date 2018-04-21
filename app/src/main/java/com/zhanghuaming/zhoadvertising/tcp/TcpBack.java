package com.zhanghuaming.zhoadvertising.tcp;

/**
 * Created by zhang on 2018/2/23.
 */

public interface TcpBack {
    String sendClose = "out close";
     void receive(String rec);
    void sendError(String e);
    void receiveError(String e);
    void errorBack(String e);
}
