package com.chopikus.bizzarepizza;

import android.graphics.Bitmap;

public class CartModel {

    String name;
    int id_;
    Bitmap image;
    Integer count;
    public CartModel(String name, Integer count, int id_, Bitmap image) {
        this.name = name;
        this.count = count;
        this.id_ = id_;
        this.image=image;
    }

    public String getName() {
        return name;
    }

    public Integer getCount() {
        return count;
    }

    public Bitmap getImage() {
        return image;
    }

    public int getId() {
        return id_;
    }
}