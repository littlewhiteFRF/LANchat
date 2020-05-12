package com.example.lanchat.Model;

import android.text.TextUtils;

import com.example.lanchat.Event.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 这是核心的通信类（头像ID）
 * 作者：方荣福
 * 时间：2020.5.8
 */
public class CommunicateImageIdWithUDPModel {
    // 消息类型为头像ID
    public static final int IMAGE_ID = 3;

    //使用DatagramSocket进行基于UDP的Socket通信
    private DatagramSocket socket= new DatagramSocket(1983);

    //ip地址
    private String ipAddr;

    /**
     * 对方的ip地址在构造方法中传入
     * @param ipAddr
     * @throws SocketException
     */
    public CommunicateImageIdWithUDPModel(String ipAddr) throws SocketException {
        this.ipAddr=ipAddr;
    }

    /**
     * 发送消息的方法
     * @param str
     */
    public void sendDataWithUDPSocket(String str) {
        try {
            // 获得IP地址，创建服务地址对象
            InetAddress serverAddress = InetAddress.getByName(ipAddr);
            // 将文字信息转化成字节流数据
            byte data[] = str.getBytes();
            // 将字节流数据打包成一个数据包（数据，数据长度，IP地址，端口号）
            DatagramPacket packet = new DatagramPacket(data, data.length ,serverAddress ,10026);
            // 通过socket发送该数据包
            socket.send(packet);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收消息的方法
     */
    public void ServerReceviedByUdp(){
        DatagramSocket socket;
        try {
            socket = new DatagramSocket(10026);
            while (true){
                // 先定义内存空间，创建一个空的数据包
                byte data[] = new byte[4*1024];
                DatagramPacket packet = new DatagramPacket(data,data.length);
                // 将接受的数据存入数据包
                socket.receive(packet);
                // 将数据包转成String类型
                String result = new String(packet.getData(),packet.getOffset() ,packet.getLength());
                // 通过EventBus将数据传给wordsEvent bean对象，在Maintivity中通过wordsEvent获取数据并加以处理（实现对应的UI更新）
                if(!TextUtils.isEmpty(result)){
                    int imageId = Integer.parseInt( result );
                    MessageEvent messageEvent =new MessageEvent(IMAGE_ID,imageId);
                    EventBus.getDefault().post(messageEvent);
                }
                System.out.println("头像ID收到信息为："+result);
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 客户端接收服务端返回的数据(保留方法，方便后期扩展)
     */
    public void ReceiveServerSocketData() {
        DatagramSocket socket;
        try {
            socket = new DatagramSocket(1985);
            byte data[] = new byte[4 * 1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            socket.receive(packet);
            String result = new String(packet.getData(), packet.getOffset(),
                    packet.getLength());
            socket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭断开socket
     */
    public void disconnect(){
        socket.close();
        socket.disconnect();
    }
}
