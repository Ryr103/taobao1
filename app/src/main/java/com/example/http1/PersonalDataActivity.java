package com.example.http1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonalDataActivity extends AppCompatActivity {
    private ImageButton Back;
    private TextView Save;
    private EditText User_Name;
    private EditText Sex;
    private EditText Info;
    private RoundedImageView Head;
    private Uri imageUri;//图片存储的路径
    private File file;//需要存储的图片文件

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_data);
        Back = findViewById(R.id.back);
        Save = findViewById(R.id.save);
        User_Name = findViewById(R.id.user_name);
        Sex = findViewById(R.id.sex);
        Info = findViewById(R.id.info);
        Head = findViewById(R.id.avatar);

        file = new File(Environment.getExternalStorageDirectory(), "headPicture.jpg");//新建一个文件（路径，文件名称）
        //Environment.getExternalStorageDirectory()为获取sd的根目录。（双sd卡获取外置sd卡，没有外置sd卡，则获取内部sd卡）
        imageUri = Uri.fromFile(file);


        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(i,1);


            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = User_Name.getText().toString();
                final String information = Info.getText().toString();
                String sex = Sex.getText().toString();
                boolean gender1 = false;
                if(sex=="男")
                {
                   gender1 = true;
                }
                if(sex=="女")
                {
                    gender1 = false;
                }

                final boolean finalGender = true;
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://49.232.214.94/api/user";
                        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
                        String token = sharedPreferences.getString("token",null);
                        OkHttpClient client = new OkHttpClient();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("name",userName);
                            jsonObject.put("sex",finalGender );
                            jsonObject.put("info",information);
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
                                        Toast.makeText(PersonalDataActivity.this, "失败", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(PersonalDataActivity.this, msg, Toast.LENGTH_SHORT).show();
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

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "http://49.232.214.94/api/user";
                SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PersonalDataActivity.this,"网络连接失败",Toast.LENGTH_SHORT).show();
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
                            final String info = jsonObject2.getString("info");
                            final Boolean sex = jsonObject2.getBoolean("sex");
                            final String head = jsonObject2.getString("head");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    User_Name.setText(name);
                                    Info.setText(info);
                                    if(sex == true){
                                        Sex.setText("男");
                                    }
                                    if(sex == false){
                                        Sex.setText("女");
                                    }
                                    Glide.with(PersonalDataActivity.this).load("http://49.232.214.94/api/img/"+head).into(Head);

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
                            Head.setImageURI(uri);
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
                                                    Toast.makeText(PersonalDataActivity.this,"上传失败",Toast.LENGTH_SHORT).show();
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
                                                        Toast.makeText(PersonalDataActivity.this, code+msg, Toast.LENGTH_SHORT).show();
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
