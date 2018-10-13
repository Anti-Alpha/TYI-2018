package com.chopikus.bizzarepizza;

import android.graphics.Bitmap;

public class OrderModel {

    String name;
    int id_;
    Bitmap image;
    Double price;
    String phone_number;
    public OrderModel(String name, Double price, int id_, Bitmap image, String phone_number) {
        this.name = name;
        this.price = price;
        this.id_ = id_;
        this.image=image;
        this.phone_number = phone_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Double getPrice() {
        return price;
    }
}