package com.example.http1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class AddActivity extends AppCompatActivity {
    private ImageButton Back;
    private TextView Add;
    private EditText itemName;
    private EditText itemPrice;
    private EditText itemInfo;
    private EditText itemQuantity;
    private ImageView Avatar;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);
        Back = findViewById(R.id.back);
        Add = findViewById(R.id.add_new_2);
        itemName = findViewById(R.id.item_name);
        itemPrice = findViewById(R.id.item_price);
        itemInfo = findViewById(R.id.item_info);
        itemQuantity = findViewById(R.id.quantity);
        Avatar = findViewById(R.id.avatar);

        Avatar.setOnClickListener(new View.OnClickListener() {
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

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String item_Name = itemName.getText().toString();
                final String item_Information = itemInfo.getText().toString();
                final Float item_Price = Float.parseFloat(itemPrice.getText().toString());
                final Integer item_Quantity = Integer.parseInt((itemQuantity.getText().toString()));
                final String img = "http://49.232.214.94/api/img/6dc50be76bc6be3b98acc3698fee4f5e";

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
                            jsonObject.put("img",img);
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
                                        Toast.makeText(AddActivity.this, "失败", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(AddActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                            Avatar.setImageURI(uri);
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
                                    String url = "http://49.232.214.94/api/upload/head";
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
                                                    Toast.makeText(AddActivity.this,"上传失败",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                            String responseData = response.body().string();
                                            try {
                                                JSONObject jsonObject = new JSONObject(responseData);
                                                final String msg = jsonObject.getString("msg");
                                                final String code = jsonObject.getString("code");
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(AddActivity.this, code+msg, Toast.LENGTH_SHORT).show();
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

