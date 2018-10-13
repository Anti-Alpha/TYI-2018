package com.chopikus.bizzarepizza;

import android.graphics.Bitmap;

public class DataModel {

    String name;
    int id_;
    Bitmap image;
    Double price;
    public DataModel(String name, Double price,  int id_, Bitmap image) {
        this.name = name;
        this.price = price;
        this.id_ = id_;
        this.image=image;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Bitmap getImage() {
        return image;
    }

    public int getId() {
        return id_;
    }
}