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

public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.ViewHolder>{
    private List<Map<String, Object>> list;
    private Context context;

    public MyAdapter2(Context context, List<Map<String, Object>> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public MyAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter2.ViewHolder holder, final int position) {
        holder.Name.setText(list.get(position).get("name").toString());
        holder.Info.setText(list.get(position).get("info").toString());
        holder.Price.setText("¥"+list.get(position).get("price").toString());
        holder.Item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ModifyActivity.class);
                Object good_id =  list.get(position).get("good_id");
                Object quantity = list.get(position).get("quantity");
                Object name = list.get(position).get("name");
                Object info = list.get(position).get("info");
                Object price = list.get(position).get("price");
                Object img = list.get(position).get("img");
                intent.putExtra("img",img.toString());
                intent.putExtra("good_id",good_id.toString());
                intent.putExtra("quantity",quantity.toString());
                intent.putExtra("name",name.toString());
                intent.putExtra("info",info.toString());
                intent.putExtra("price",price.toString());
                context.startActivity(intent);


            }
        });
        Glide.with(context).load("http://49.232.214.94/api/img/"+list.get(position).get("img").toString()).into(holder.Img);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView Name;
        private TextView Price;
        private ImageView Img;
        private LinearLayout Item;
        private TextView Info;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.name);
            Price = itemView.findViewById(R.id.price);
            Img = itemView.findViewById(R.id.picture);
            Item = itemView.findViewById(R.id.item);
            Info = itemView.findViewById(R.id.info_1);
        }
    }
}
