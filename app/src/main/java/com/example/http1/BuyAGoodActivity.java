package com.example.http1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

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

public class BuyAGoodActivity extends AppCompatActivity {
    private ImageButton Back;
    private TextView Name;
    private TextView Info;
    private TextView Quantity;
    private TextView Price;
    private TextView Good_id;
    private ImageView Img;
    private ImageButton Add;
    private ImageButton Del;
    private EditText Quantity2;
    private ImageButton Purchase;



    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_a_good);
        Back = findViewById(R.id.back_5);
        Name = findViewById(R.id.good_name);
        Info = findViewById(R.id.good_info);
        Price = findViewById(R.id.price_1);
        Quantity = findViewById(R.id.quantity_1);
        Img = findViewById(R.id.img_2);
        Add = findViewById(R.id.add_2);
        Del = findViewById(R.id.del);
        Quantity2 = findViewById(R.id.quantity_2);
        Purchase = findViewById(R.id.purchase);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String info = intent.getStringExtra("info");
        final Integer quantity = Integer.parseInt(intent.getStringExtra("quantity"));
        Float price = Float.parseFloat(intent.getStringExtra("price"));
        String img = intent.getStringExtra("img");
        final String good_id = intent.getStringExtra("good_id");
        Name.setText("NO."+good_id+": "+name);
        Info.setText(info);
        Price.setText("¥"+price.toString());
        Quantity.setText("剩"+quantity.toString()+"件");
        if(!img.equals("")){
            String i = img.substring(0,4);
            if (i.equals("http")){
                Glide.with(this).load(img).into(Img);
            }else{
                Glide.with(this).load("http://49.232.214.94/api/img/"+img).into(Img);
            }
        }

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer purchase_quantity = Integer.parseInt(Quantity2.getText().toString());
                if (purchase_quantity==0) {
                    Toast.makeText(BuyAGoodActivity.this, "不能再少了", Toast.LENGTH_SHORT).show();
                }else{
                    purchase_quantity=purchase_quantity-1;
                    Quantity2.setText(purchase_quantity.toString());
                }
            }
        });
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer purchase_quantity = Integer.parseInt(Quantity2.getText().toString());
                if (purchase_quantity==quantity) {
                    Toast.makeText(BuyAGoodActivity.this, "不能再多了", Toast.LENGTH_SHORT).show();
                }else{
                    purchase_quantity=purchase_quantity+1;
                    Quantity2.setText(purchase_quantity.toString());
                }
            }
        });
        Purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder  = new AlertDialog.Builder(BuyAGoodActivity.this);
                builder.setTitle("生成订单" ) ;
                builder.setMessage("确认购买吗?" ) ;
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String url = "http://49.232.214.94/api/order";
                        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
                        final String token = sharedPreferences.getString("token",null);

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                OkHttpClient client = new OkHttpClient();
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("good_id", Integer.valueOf(good_id));
                                    jsonObject.put("goods_count",Integer.parseInt(Quantity2.getText().toString()));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                MediaType type = MediaType.parse("application/json;charset=utf-8");
                                RequestBody body = RequestBody.create(type, String.valueOf(jsonObject));
                                final Request request = new Request.Builder()
                                        .url(url)
                                        .addHeader("Accept", "application/json")
                                        .addHeader("Authorization",token)
                                        .post(body)
                                        .build();

                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(BuyAGoodActivity.this, "失败", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                    }

                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                        String responseData = response.body().string();
                                        try {
                                            JSONObject jsonObject = new JSONObject(responseData);
                                            final String msg = jsonObject.getString("msg");
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(BuyAGoodActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                });
                builder.setNegativeButton("算了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
    }
}
