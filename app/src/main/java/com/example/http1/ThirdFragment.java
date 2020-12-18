package com.example.http1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Fragment;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ThirdFragment extends Fragment {
    private TextView Sign_in;
    private LinearLayout PersonalData;
    private TextView Use_ID;
    private RoundedImageView Head;
    private LinearLayout MyGoods;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_3,container,false);
        Sign_in = view.findViewById(R.id.sign_in);
        PersonalData = view.findViewById(R.id.personalData);
        Use_ID = view.findViewById(R.id.User_id);
        Head = view.findViewById(R.id.head);
        MyGoods = view.findViewById(R.id.my_goods);
        Sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),Sign_inActivity.class);
                getActivity().startActivity(intent);
            }
        });
        MyGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MyGoodsActivity.class);
                getActivity().startActivity(intent);
            }
        });
        PersonalData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),PersonalDataActivity.class);
                getActivity().startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        String i=sharedPreferences.getString("account", null);
        if(i != null)
        {
            Sign_in.setText("重新登录");
        }



        return view;
    }

    @Override
    public void onResume() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://49.232.214.94/api/user";
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
                String token = sharedPreferences.getString("token",null);
                OkHttpClient client = new OkHttpClient();
                final Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization",token)
                        .get()
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"网络连接失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String responseData = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            JSONObject jsonObject2 = jsonObject1.getJSONObject("user");
                            final String name = jsonObject2.getString("name");
                            final String head = jsonObject2.getString("head");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Use_ID.setText(name);
                                    Glide.with(getActivity()).load("http://49.232.214.94/api/img/"+head).into(Head);
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
        super.onResume();
    }


}
