package com.example.foodcafe.model;

public class NotificationModel{

    private String image,body;
    private Boolean readed;

    public NotificationModel(String image, String body, Boolean readed) {
        this.image = image;
        this.body = body;
        this.readed = readed;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean isReaded() {
        return readed;
    }

    public void setReaded(Boolean readed) {
        this.readed = readed;
    }
}