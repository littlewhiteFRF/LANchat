package com.example.lanchat.Bean;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * 消息体
 * 作者：方荣福
 * 时间：2020.5.7
 */
public class Msg {
    public static final int RECEIVED = 0;//收到一条消息
    public static final int SENT = 1;//发出一条消息
    public static final int TEXT = 2;//消息内容为文字
    public static final int IMAGE = 3;//消息内容为图片
    private int type;//消息的类型
    private int contentType;//消息内容的类型
    private int imageId;//头像id
    private String content;//消息的文字内容
    private Uri imageUri;//消息的图片Uri
    private Bitmap imageBitmap;//消息的图片Bitmap

    /**
     * 构造函数（文字消息）
     * @param type
     * @param contentType
     * @param imageId
     * @param content
     */
    public Msg(int type, int contentType, int imageId, String content) {
        this.type = type;
        this.contentType = contentType;
        this.imageId = imageId;
        this.content = content;
    }

    /**
     * 构造函数（图片消息）
     * @param type
     * @param contentType
     * @param imageId
     * @param imageUri
     */
    public Msg(int type, int contentType, int imageId,  Uri imageUri) {
        this.type = type;
        this.contentType = contentType;
        this.imageId = imageId;
        this.imageUri = imageUri;
    }

    /**
     * 构造函数（头像ID消息）
     * @param type
     * @param contentType
     * @param imageId
     * @param imageBitmap
     */
    public Msg(int type, int contentType, int imageId,  Bitmap imageBitmap) {
        this.type = type;
        this.contentType = contentType;
        this.imageId = imageId;
        this.imageBitmap = imageBitmap;
    }

    /**
     * getType
     * @return
     */
    public int getType() {
        return type;
    }

    /**
     * setType
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * getContentType
     * @return
     */
    public int getContentType() {
        return contentType;
    }

    /**
     * setContentType
     * @param contentType
     */
    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    /**
     * getImageId
     * @return
     */
    public int getImageId() {
        return imageId;
    }

    /**
     * setImageId
     * @param imageId
     */
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    /**
     * getContent
     * @return
     */
    public String getContent() {
        return content;
    }

    /**
     * setContent
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * getImageUri
     * @return
     */
    public Uri getImageUri() {
        return imageUri;
    }

    /**
     * setImageUri
     * @param imageUri
     */
    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    /**
     * getImageBitmap
     * @return
     */
    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    /**
     * setImageBitmap
     * @param imageBitmap
     */
    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
}
