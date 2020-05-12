package com.example.lanchat.Model;

import com.example.lanchat.Event.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 这是核心的通信类（图片）
 * 作者：方荣福
 * 时间：2020.5.7
 */
public class CommunicateImageWithUDPModel {
    // 消息类型为图片
    public static final int IMAGE = 1;

    //使用DatagramSocket进行基于UDP的Socket通信
    private DatagramPacket datagramPacket;

    //指定端口号
    private DatagramSocket socket2= new DatagramSocket(1984);

    byte data1[] = new byte[8192];

    byte data2[] = new byte[8192];

    private FileInputStream in;

    //ip地址
    private String ipAddr;

    /**
     * 对方的ip地址在构造方法中传入
     * @param ipAddr
     * @throws SocketException
     */
    public CommunicateImageWithUDPModel(String ipAddr) throws SocketException {
        this.ipAddr=ipAddr;
    }

    /**
     * 图片转换为文件流分片并发送
     * @param path
     * @throws FileNotFoundException
     */
    public void sendImage(String path) throws FileNotFoundException {

        try {
            File file = new File(path);
            in = new FileInputStream(file);
            int n = -1;
            while((n=in.read(data1))!=-1){
                in.hashCode();
                // 指定接收端的socket的ip地址和端口号
                datagramPacket = new DatagramPacket(data1,data1.length,InetAddress.getByName(ipAddr),10024);
                socket2 = new DatagramSocket();
                socket2.send(datagramPacket);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        socket2.close();

        // 发送图片结束标志，发送时需要将字符串转换成字节流
        String end = ";!";
        try {
            datagramPacket = new DatagramPacket(end.getBytes(),end.getBytes().length,InetAddress.getByName(ipAddr),10024);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            socket2 = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            socket2.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket2.close();
        System.out.println("-->发送图片结束");
    }

    /**
     * 收到图片
     * @throws SocketException
     */
    public void receiveImage() throws SocketException{
        DatagramSocket socket;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        socket = new DatagramSocket(10024);
        while(true){
            datagramPacket = new DatagramPacket(data2, data2.length);
            try {
                socket.receive(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 对每一个接收的包数据转成字符串，如果开头为";!"则表示接收结束，跳出循环
            String msg  = new String(datagramPacket.getData(),0,datagramPacket.getLength());
            if(msg.startsWith(";!")){
                System.out.println("-->接收到所有数据");
                break;
            }
            baos.write(datagramPacket.getData(),0,datagramPacket.getLength());
        }

        try {
            if(baos.toByteArray() != null){
                // 自定义事件（作为通信载体，可以发送数据）
                MessageEvent messageEvent =new MessageEvent(IMAGE,baos.toByteArray());
                // 在任意线程里发布事件：EventBus.getDefault()为事件发布者，而post()为发布动作
                EventBus.getDefault().post(messageEvent);
            }
            System.out.println("图片收到信息为："+baos.toByteArray().toString());
            baos.flush();
            baos.close();
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
        socket2.close();
        socket2.disconnect();
    }

}
