package com.example.http1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

public class MyAdapter3 extends RecyclerView.Adapter<MyAdapter3.ViewHolder>{
    private List<Map<String, Object>> list;
    private Context context;

    public MyAdapter3(Context context, List<Map<String, Object>> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public MyAdapter3.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_2,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter3.ViewHolder holder, final int position) {


        holder.Name.setText("NO:"+list.get(position).get("goods_id").toString()+"号商品");
        holder.TotalPrice.setText("¥"+Float.parseFloat(list.get(position).get("goods_price").toString())*Float.parseFloat(list.get(position).get("goods_count").toString()));
        holder.Count.setText("共"+list.get(position).get("goods_count").toString()+"件");

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView Name;
        private TextView TotalPrice;
        private TextView Count;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.good_id_2);
            Count = itemView.findViewById(R.id.good_count);
            TotalPrice = itemView.findViewById(R.id.total_price);
        }
    }
}
