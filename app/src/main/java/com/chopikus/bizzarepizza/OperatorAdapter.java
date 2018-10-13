package com.chopikus.bizzarepizza;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class OperatorAdapter extends RecyclerView.Adapter<OperatorAdapter.MyViewHolder> {

    private ArrayList<OrderModel> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewVersion;
        ImageView imageViewIcon;
        TextView textViewId;
        TextView textViewPhone;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.textViewVersion = (TextView) itemView.findViewById(R.id.textViewVersion);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
            this.textViewId = (TextView) itemView.findViewById(R.id.textViewId);
            this.textViewPhone = (TextView) itemView.findViewById(R.id.textViewPhone);

        }
    }

    public OperatorAdapter(ArrayList<OrderModel> data) {
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_layout, parent, false);
        view.setOnClickListener(OperatorActivity.myOnClickListener);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView textViewName = holder.textViewName;
        TextView textViewVersion = holder.textViewVersion;
        ImageView imageView = holder.imageViewIcon;
        TextView textViewId = holder.textViewId;
        TextView textViewPhone = holder.textViewPhone;
        textViewName.setText(dataSet.get(listPosition).getName());
        textViewName.setTextSize(18);
        textViewVersion.setText(dataSet.get(listPosition).getPrice().toString()+" грн.");
        imageView.setImageBitmap(dataSet.get(listPosition).getImage());
        textViewId.setText(String.valueOf(dataSet.get(listPosition).id_));
        textViewPhone.setText(dataSet.get(listPosition).phone_number);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}