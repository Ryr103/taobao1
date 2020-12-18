package com.example.http1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyGoodsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Context context;
    private List<Map<String, Object>> list = new ArrayList<>();
    private MyAdapter3 adapter;
    private RefreshLayout mReFreshLayout;
    private ImageButton Back;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_goods);
        Back = findViewById(R.id.back);
        recyclerView = findViewById(R.id.recyclerview3);
        adapter = new MyAdapter3(context,list);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
                String token = sharedPreferences.getString("token", null);
                final Request request = new Request.Builder()
                        .url("http://49.232.214.94/api/order")
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization", token)
                        .get()
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                             runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {


                        try {
                            String responseData = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseData);
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            JSONArray jsonArray_1 = jsonObject1.getJSONArray("orders");
                            for (int i = 0; i < jsonArray_1.length(); i++) {
                                JSONObject object = jsonArray_1.getJSONObject(i);
                                String goods_price = object.getString("goods_price");
                                String goods_count = object.getString("goods_count");
                                final String good_id = object.getString("good_id");
                                Map<String, Object> map = new HashMap<>();
                                map.put("goods_price", goods_price);
                                map.put("goods_count", goods_count);
                                map.put("goods_id", good_id);
                                list.add(map);
                            }



                                 runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recyclerView.setLayoutManager(new LinearLayoutManager(context));//垂直排列 , Ctrl+P
                                    recyclerView.setAdapter(new MyAdapter3(MyGoodsActivity.this,list));//绑定适配器
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });

            }
        });
        thread.start();
    }
}
