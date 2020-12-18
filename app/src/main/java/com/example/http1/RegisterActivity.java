package com.example.http1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.JsonToken;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private ImageButton Back;
    private EditText Account;
    private EditText Password_1;
    private EditText Password_2;
    private ImageButton Sign_in;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Back =findViewById(R.id.back);
        Account = findViewById(R.id.account);
        Password_1 = findViewById(R.id.password_1);
        Password_2 = findViewById(R.id.password_2);
        Sign_in = findViewById(R.id.sign_in);

        Password_1.setTransformationMethod(PasswordTransformationMethod.getInstance());
        Password_2.setTransformationMethod(PasswordTransformationMethod.getInstance());


        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String account = Account.getText().toString().trim();
                final String password1 = Password_1.getText().toString().trim();
                String password2 = Password_2.getText().toString().trim();
                final String url = "http://49.232.214.94/api/register";
                if(!password1.equals(password2)){Toast.makeText(RegisterActivity.this,"两次输入密码不一致!",Toast.LENGTH_SHORT).show();}
                else {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient client = new OkHttpClient();
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("Account", account);
                                jsonObject.put("password", password1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            MediaType type = MediaType.parse("application/json;charset=utf-8");
                            RequestBody body = RequestBody.create(type, String.valueOf(jsonObject));
                            final Request request = new Request.Builder()
                                    .url(url)
                                    .addHeader("Accept", "application/json")
                                    .post(body)
                                    .build();

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(RegisterActivity.this, "失败", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }

                                @Override
                                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                    String responseData = response.body().string();
                                    try {
                                        JSONObject jsonObject = new JSONObject(responseData);
                                        final String msg = jsonObject.getString("msg");
                                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor =sharedPreferences.edit();
                                        String integer = "1";
                                        editor.putString("account",account);
                                        editor.putString("password",password1);
                                        editor.putString("loginStatus",integer);
                                        editor.commit();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
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
            }
        });
    }
}
