package com.chopikus.bizzarepizza;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder>{

    private RecyclerView recyclerView;
    private ArrayList<CartModel> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView count;
        ImageView imageViewIcon;
        TextView textViewId;
        LinearLayout layout;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.count = (TextView) itemView.findViewById(R.id.count);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
            this.textViewId = (TextView) itemView.findViewById(R.id.textViewId);
            this.layout = (LinearLayout) itemView.findViewById(R.id.layout);
        }
    }

    public CartAdapter(ArrayList<CartModel> data) {
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        view.setOnClickListener(CartActivity.myOnClickListener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView textViewName = holder.textViewName;
        final TextView count = holder.count;
        ImageView imageView = holder.imageViewIcon;
        TextView textViewId = holder.textViewId;
        textViewName.setText(dataSet.get(listPosition).getName());
        count.setText(dataSet.get(listPosition).count.toString()+" шт.");
        //editText.setFocusableInTouchMode(false);
        //editText.setFocusable(false);

        imageView.setImageBitmap(dataSet.get(listPosition).getImage());
        textViewId.setText(String.valueOf(dataSet.get(holder.getAdapterPosition()).id_));

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}