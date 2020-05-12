package com.example.lanchat.Controller;

import com.example.lanchat.Model.CommunicateImageWithTCPModel;

import java.io.FileNotFoundException;
import java.net.SocketException;

/**
 * 控制类（通过TCP发送图片）
 * 调用Model处理相关业务逻辑
 * 作者：方荣福
 * 时间：2020/5/10
 */
public class CommunicateImageWithTCPController {
    private CommunicateImageWithTCPModel model;

    /**
     * 构造函数
     * @param ipAddr
     */
    public CommunicateImageWithTCPController(String ipAddr){
        try {
            model = new CommunicateImageWithTCPModel(ipAddr);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片转换为文件流并发送，客户端通过Socket发送
     * @param path
     * @throws FileNotFoundException
     */
    public void sendImage(String path) throws FileNotFoundException {
        model.sendImage(path);
    }

    /**
     * 接收图片，服务端通过ServerSocket接收
     */
    public void receiveImage() throws SocketException{
        model.receiveImage();
    }
}
