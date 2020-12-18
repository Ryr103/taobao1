package com.example.http1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    private TextView name;
    private TextView content;
    private Map map;
    private List<Map<String, Object>> list = new ArrayList<>();

    private Fragment mFragment1;
    private LinearLayout mFirstLayout,mSecondLayout,mThirdLayout;
    private ImageButton buttonShop;
    private ImageButton buttonPutOnSale;
    private ImageButton buttonPerson;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        content = findViewById(R.id.content);
        recyclerView = findViewById(R.id.recyclerview);

        initView();
        setView();

        //设置开始时显示的fragment
        android.app.FragmentManager fm = getFragmentManager();
        android.app.FragmentTransaction MfragmentTransactions = fm.beginTransaction();
        FirstFragment f1 = new FirstFragment();
        MfragmentTransactions.replace(R.id.fragment,f1);
        MfragmentTransactions.commit();
    }
    private void initView() {
        mFirstLayout = findViewById(R.id.Layout_1);
        mSecondLayout = findViewById(R.id.Layout_2);
        mThirdLayout = findViewById(R.id.Layout_3);
        buttonShop = findViewById(R.id.button_shop);
        buttonPutOnSale = findViewById(R.id.button_my_shop);
        buttonPerson = findViewById(R.id.me);
    }



    private void setView(){

        buttonShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.FragmentManager fm = getFragmentManager();
                android.app.FragmentTransaction MfragmentTransactions = fm.beginTransaction();
                FirstFragment f1 = new FirstFragment();
                MfragmentTransactions.replace(R.id.fragment,f1);
                MfragmentTransactions.commit();
            }
        });
        buttonPutOnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction MfragmentTransactions = fm.beginTransaction();
                SecondFragment f2 = new SecondFragment();
                MfragmentTransactions.replace(R.id.fragment,f2);
                MfragmentTransactions.commit();
            }
        });
        buttonPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction MfragmentTransactions = fm.beginTransaction();
                ThirdFragment f3 = new ThirdFragment();
                MfragmentTransactions.replace(R.id.fragment,f3);
                MfragmentTransactions.commit();
            }
        });
    }



}