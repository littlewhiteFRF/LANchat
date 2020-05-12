package com.example.lanchat.Controller;

import com.example.lanchat.Model.CommunicateImageIdWithUDPModel;

import java.net.SocketException;

/**
 * 控制类（通过UDP发送头像ID）
 * 调用Model处理相关业务逻辑
 * 作者：方荣福
 * 时间：2020/5/10
 */
public class CommunicateImageIdWithUDPController {
    private CommunicateImageIdWithUDPModel model;

    /**
     * 构造函数
     * @param ipAddr
     */
    public CommunicateImageIdWithUDPController(String ipAddr){
        try {
            model = new CommunicateImageIdWithUDPModel(ipAddr);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息的方法
     * @param str
     */
    public void sendDataWithUDPSocket(String str){
        model.sendDataWithUDPSocket(str);
    }

    /**
     * 接收消息的方法
     */
    public void ServerReceviedByUdp(){
        model.ServerReceviedByUdp();
    }

    /**
     * 关闭断开socket
     */
    public void disconnect(){
        model.disconnect();
    }

}
