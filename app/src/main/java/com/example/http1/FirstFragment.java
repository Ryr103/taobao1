package com.example.http1;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FirstFragment extends Fragment {
    private RecyclerView recyclerView;
    private Context context;
    private List<Map<String, Object>> list = new ArrayList<>();
    private MyAdapter adapter;
    private RefreshLayout mReFreshLayout;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_1,container,false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        context = view.getContext();
        mReFreshLayout = view.findViewById(R.id.swipeReFreshLayout);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        final String url = "http://49.232.214.94/api/goods";
        adapter = new MyAdapter(getActivity(),list);

        mReFreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                list.clear();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient okHttpClient = new OkHttpClient();
                        final Request request = new Request.Builder()
                                .url(url)
                                .addHeader("Accept", "application/json")
                                .get()
                                .build();
                        Call call = okHttpClient.newCall(request);
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,"网络连接失败",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {


                                try {
                                    String responseData = response.body().string();
                                    JSONObject jsonObject = new JSONObject(responseData);
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                    JSONArray jsonArray_1 = jsonObject1.getJSONArray("goods");
                                    for(int i = 0; i < jsonArray_1.length(); i++) {
                                        JSONObject object =jsonArray_1.getJSONObject(i);
                                        String good_id = object.getString("good_id");

                                        String quantity = object.getString("quantity");
                                        String name = object.getString("name");
                                        String price = object.getString("price");
                                        String info =  object.getString("info");
                                        String img = object.getString("img");
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("name",name);
                                        map.put("price",price);
                                        map.put("info",info);
                                        map.put("img",img);
                                        map.put("good_id",good_id);
                                        map.put("quantity",quantity);
                                        list.add(map);
                                    }



                                    if(getActivity()==null)
                                        return;
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                                            recyclerView.setLayoutManager(layoutManager);
                                            recyclerView.setAdapter(new MyAdapter(getActivity(), list));//绑定适配器
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


                mReFreshLayout.finishRefresh();
            }
        });



        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                final Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Accept", "application/json")
                        .get()
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context,"网络连接失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {


                        try {
                            String responseData = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseData);
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            JSONArray jsonArray_1 = jsonObject1.getJSONArray("goods");
                            for(int i = 0; i < jsonArray_1.length(); i++) {
                                JSONObject object =jsonArray_1.getJSONObject(i);
                                String good_id = object.getString("good_id");
                                String quantity = object.getString("quantity");
                                String name = object.getString("name");
                                String price = object.getString("price");
                                String info =  object.getString("info");
                                String img = object.getString("img");
                                Map<String, Object> map = new HashMap<>();
                                map.put("name",name);
                                map.put("price",price);
                                map.put("info",info);
                                map.put("img",img);
                                map.put("good_id",good_id);
                                map.put("quantity",quantity);
                                list.add(map);
                            }



                            if(getActivity()==null)
                                return;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setAdapter(new MyAdapter(getActivity(), list));//绑定适配器
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
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));//垂直排列 , Ctrl+P
                    recyclerView.setAdapter((RecyclerView.Adapter) adapter);//绑定适配器
                }
            });

        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView.setLayoutManager(new LinearLayoutManager(context));//垂直排列 , Ctrl+P
                    recyclerView.setAdapter((RecyclerView.Adapter) adapter);//绑定适配器
                }
            });

        }
    }


}
