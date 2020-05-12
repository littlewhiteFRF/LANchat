package com.example.lanchat.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.example.lanchat.Bean.Msg;
import com.example.lanchat.R;

import java.util.List;

/**
 * 该工具类，包含许多其他杂七杂八的小功能函数
 */
public class OtherUtil {
    /**
     * 初始化消息数据
     * @param msgList
     */
    private void initMsg(List<Msg> msgList) {
        Msg msg1 = new Msg(Msg.RECEIVED,Msg.TEXT,App.getImageId(),"I miss you!");
        msgList.add(msg1);

        Msg msg2 = new Msg(Msg.SENT,Msg.TEXT,App.getImageId(),"I miss you,too!");
        msgList.add(msg2);

        Msg msg3 = new Msg(Msg.RECEIVED,Msg.TEXT,App.getImageId(),"I will come back soon!");
        msgList.add(msg3);

    }

    /**
     * 获取连上wifi后的IP地址
     * @param context
     * @return
     */
    public static String getWifiIp(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        return ip;
    }

    /**
     * 整型转IP
     * @param ipInt
     * @return
     */
    public static String intToIp(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * 获取头像
     * @param imageId
     * @return
     */
    public static Bitmap initHeadImage(int imageId, Context mContext){
        Bitmap image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
        switch (imageId) {
            case 0:
                image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_0);
                break;
            case 1:
                image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_1);
                break;
            case 2:
                image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_2);
                break;
            case 3:
                image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_3);
                break;
            case 4:
                image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_4);
                break;
            case 5:
                image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_5);
                break;
            case 6:
                image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_6);
                break;
            case 7:
                image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_7);
                break;
            case 8:
                image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_8);
                break;
            case 9:
                image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_9);
                break;
            case 10:
                image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_10);
                break;
            case 11:
                image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_11);
                break;
            case 12:
                image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_12);
                break;
            case 13:
                image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_13);
                break;
            case 14:
                image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_14);
                break;
            case 15:
                image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_15);
                break;
            case 16:
                image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_16);
                break;
            case 17:
                image = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.iv_17);
                break;
            default:

                break;
        }
        return image;
    }
}
