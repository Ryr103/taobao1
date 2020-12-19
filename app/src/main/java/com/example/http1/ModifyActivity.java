package com.example.http1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModifyActivity extends AppCompatActivity {
    private ImageButton Back;
    private TextView Modify;
    private EditText itemName;
    private EditText itemPrice;
    private EditText itemInfo;
    private EditText itemQuantity;
    private ImageView itemPicture;
    private TextView goodID;
    private String hash;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify);
        Back = findViewById(R.id.back);
        Modify = findViewById(R.id.modify);
        itemName = findViewById(R.id.item_name_2);
        itemPrice = findViewById(R.id.item_price_2);
        itemInfo = findViewById(R.id.item_info_2);
        itemQuantity = findViewById(R.id.quantity2);
        itemPicture = findViewById(R.id.item_picture);
        goodID = findViewById(R.id.good_id);


        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String info = intent.getStringExtra("info");
        final Integer quantity = Integer.parseInt(intent.getStringExtra("quantity"));
        Float price = Float.parseFloat(intent.getStringExtra("price"));
        String img = intent.getStringExtra("img");
        final String good_id = intent.getStringExtra("good_id");
        itemName.setText(name);
        itemInfo.setText(info);
        itemPrice.setText(price.toString());
        itemQuantity.setText(quantity.toString());
        goodID.setText("第"+good_id+"号商品");
        Glide.with(this).load("http://49.232.214.94/api/img/"+img).into(itemPicture);


        itemPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(i,1);


            }
        });
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String item_Name = itemName.getText().toString();
                final String item_Information = itemInfo.getText().toString();
                final Float item_Price = Float.parseFloat(itemPrice.getText().toString());
                final Integer item_Quantity = Integer.parseInt((itemQuantity.getText().toString()));



                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://49.232.214.94/api/good";
                        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
                        String token = sharedPreferences.getString("token",null);
                        OkHttpClient client = new OkHttpClient();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("name",item_Name);
                            jsonObject.put("price", item_Price);
                            jsonObject.put("info",item_Information);
                            jsonObject.put("quantity",item_Quantity);
                            jsonObject.put("img",hash);
                            jsonObject.put("good_id",Integer.valueOf(good_id));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        MediaType type = MediaType.parse("application/json;charset=utf-8");
                        RequestBody body = RequestBody.create(type, String.valueOf(jsonObject));
                        final Request request = new Request.Builder()
                                .url(url)
                                .addHeader("Accept", "application/json")
                                .addHeader("Authorization",token)
                                .put(body)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ModifyActivity.this, "失败", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(ModifyActivity.this, msg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    finish();
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


    }

     @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //第一层switch
        switch (requestCode) {
            case 1:
                //第二层switch
                switch (resultCode) {
                    case RESULT_OK:
                        if (data != null) {
                            Uri uri = data.getData();
                            itemPicture.setImageURI(uri);
                            String[] arr = {MediaStore.Images.Media.DATA};
                            Cursor cursor = managedQuery(uri, arr, null, null, null);
                            int imgIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();
                            String imgPath = cursor.getString(imgIndex);
                            final File file = new File(imgPath);
                            if(!file.exists()){
                                Toast.makeText(this,"无文件",Toast.LENGTH_SHORT).show();
                            }


                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String url = "http://49.232.214.94/api/upload/good";
                                    SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
                                    String token = sharedPreferences.getString("token",null);
                                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                                    MultipartBody multipartBody = new MultipartBody.Builder()
                                            .setType(MultipartBody.FORM)
                                            .addFormDataPart("img", file.getName(), requestBody)
                                            .build();
                                    Request request = new Request.Builder()
                                            .url(url)
                                            .addHeader("Authorization",token)
                                            .addHeader("Accept","application/json")
                                            .post(multipartBody)
                                            .build();
                                    OkHttpClient client = new OkHttpClient();

                                    client.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(ModifyActivity.this,"上传失败",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                            String responseData = response.body().string();
                                            if(responseData==null){
                                                Toast.makeText(ModifyActivity.this, "图片太大", Toast.LENGTH_SHORT).show();
                                            }
                                            try {
                                                JSONObject jsonObject = new JSONObject(responseData);
                                                final String msg = jsonObject.getString("msg");
                                                JSONObject jsonobject = jsonObject.getJSONObject("data");
                                                hash  = jsonobject.getString("hash");
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(ModifyActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                        break;
                    case RESULT_CANCELED:
                        break;
                }
                break;
            default:
                break;
        }
    }

}
