package com.example.lanchat.Model;

import android.os.Environment;

import com.example.lanchat.Event.MessageEvent;
import com.example.lanchat.Util.PictureUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * 这是核心的通信类（图片）
 * 作者：方荣福
 * 时间：2020.5.8
 */
public class CommunicateImageWithTCPModel {
    // 消息类型为图片
    public static final int IMAGE = 1;

    //IP地址
    private String ipAddr;

    //客户端socket（发送）
    private Socket mSocket;

    //服务端socket（接收）
    private ServerSocket mServerSocket;

    byte data1[] = new byte[40*1024];

    /**
     * 对方的ip地址在构造方法中传入
     * @param ipAddr
     * @throws SocketException
     */
    public CommunicateImageWithTCPModel(String ipAddr) throws SocketException {
        this.ipAddr=ipAddr;
    }

    /**
     * 图片转换为文件流并发送，客户端通过Socket发送
     * @param path
     * @throws FileNotFoundException
     */
    public void sendImage(String path) throws FileNotFoundException {
        System.out.println("==================");
        //1、创建一个Socket，连接到服务器端、指定端口号。放在子线程中运行，否则会有问题。
        try {//指定ip地址和端口号
            mSocket = new Socket(ipAddr,1984);
            String targetPath = Environment.getExternalStorageDirectory().toString()+"/compressPic.jpg";
            System.out.println("=================="+targetPath);
            //调用压缩图片的方法，返回压缩后的图片path
            final String compressImage = PictureUtil.compressImage(path, targetPath, 10);
            File file = new File(compressImage);
            FileInputStream in = new FileInputStream(file);
            in.read(data1);
            if(mSocket != null){//获取输出流
                //2、调用Socket类的getOutputStream()获取输入输出流。
                OutputStream mOutStream = mSocket.getOutputStream();
                //3、发送
                mOutStream.write(data1);
                mOutStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("连接服务端成功！");
    }

    /**
     * 接收图片，服务端通过ServerSocket接收
     */
    public void receiveImage() throws SocketException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 1、开启服务
        try {    //开启服务、指定端口号
            mServerSocket = new ServerSocket(1984);
            System.out.println("开启服务端成功！");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("开启服务端失败！");
        }

        while (true){
            //2、调用ServerSocket的accept(),监听连接请求，如果客户端请求连接，则接收连接，返回Scoekt对象。
            try {
                mSocket = mServerSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //3、调用Socket类的getInputStream()和getOutputStream()获取输入输出流。
            //获取输入流
            try {
                InputStream mInStream = mSocket.getInputStream();
                //4、接收数据
                //循环执行read，用来接收数据。
                //数据存在buffer中，count为读取到的数据长度。
                byte[] buffer = new byte[40*1024];
                //int count = mInStream.read(buffer);
                mInStream.read(buffer);
                baos.write(buffer,0,buffer.length);
                if(baos.toByteArray() != null){
                    // 自定义事件（作为通信载体，可以发送数据）
                    MessageEvent messageEvent =new MessageEvent(IMAGE,baos.toByteArray());
                    // 在任意线程里发布事件：EventBus.getDefault()为事件发布者，而post()为发布动作
                    EventBus.getDefault().post(messageEvent);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //5、服务不再需要，则关闭服务
//        if(mServerSocket != null){
//            try {
//                mServerSocket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
