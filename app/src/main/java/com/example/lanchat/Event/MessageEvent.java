package com.example.lanchat.Event;

/**
 * eventbus 事件（作为事件的载体）
 * 作者：方荣福
 * 时间：2020.5.7
 */
public class MessageEvent {
    private int type;
    private String words;//文字消息
    private byte[] image;//图片字节流
    private int imageId;//头像ID

    /**
     * 构造函数（文字消息事件）
     * @param type
     * @param words
     */
    public MessageEvent(int type, String words){
        this.type=type;
        this.words=words;
    }

    /**
     * 构造函数（图片消息事件）
     * @param type
     * @param image
     */
    public MessageEvent(int type, byte[] image) {
        this.type = type;
        this.image = image;
    }

    /**
     * 构造函数（头像ID消息事件）
     * @param type
     * @param imageId
     */
    public MessageEvent(int type, int imageId) {
        this.type = type;
        this.imageId = imageId;
    }

    /**
     * getType
     * @return
     */
    public int getType() {
        return type;
    }

    /**
     * getWords
     * @return
     */
    public String getWords() {
        return words;
    }

    /**
     * getImage
     * @return
     */
    public byte[] getImage() {
        return image;
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
}
