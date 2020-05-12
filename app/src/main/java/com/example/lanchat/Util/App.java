package com.example.lanchat.Util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * 全局获取上下文context(包含一些全局变量)
 * 作者：方荣福
 * 时间：2020.5.7
 */
public class App extends Application {
    
    @SuppressLint("StaticFieldLeak")
    //上下文
    private static Context sContxet;

    //我的IP
    private static String sMyIP;

    //头像ID
    private static int imageId;

    //对方头像
    private static int otherImageId;

    @Override
    public void onCreate() {
        super.onCreate();
        sContxet = getApplicationContext();
    }

    /**
     * getContxet
     * @return
     */
    public static Context getContxet() {
        return sContxet;
    }

    /**
     * setContxet
     * @param sContxet
     */
    public static void setContxet(Context sContxet) {
        App.sContxet = sContxet;
    }

    /**
     * getsMyIP
     * @return
     */
    public static String getsMyIP() {
        return sMyIP;
    }

    /**
     * setMyIP
     * @param sMyIP
     */
    public static void setsMyIP(String sMyIP) {
        App.sMyIP = sMyIP;
    }

    /**
     * getImageId
     * @return
     */
    public static int getImageId() {
        return imageId;
    }

    /**
     * setImageId
     * @param imageId
     */
    public static void setImageId(int imageId) {
        App.imageId = imageId;
    }

    /**
     * getOtherImageId
     * @return
     */
    public static int getOtherImageId() {
        return otherImageId;
    }

    /**
     * setOtherImageId
     * @param otherImageId
     */
    public static void setOtherImageId(int otherImageId) {
        App.otherImageId = otherImageId;
    }
}
