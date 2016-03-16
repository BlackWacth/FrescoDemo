package com.hua.frescodemo.model;

/**
 * Created by ZHONG WEI  HUA on 2016/3/16.
 */
public class ImageModel {
    private String thumb;
    private String expand;

    public ImageModel(String thumb, String expand) {
        this.thumb = thumb;
        this.expand = expand;
    }

    public String getThumb() {
        return thumb;
    }

    public String getExpand() {
        return expand;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public void setExpand(String expand) {
        this.expand = expand;
    }

    @Override
    public String toString() {
        return "ImageModel{" +
                "thumb='" + thumb + '\'' +
                ", expand='" + expand + '\'' +
                '}';
    }
}
