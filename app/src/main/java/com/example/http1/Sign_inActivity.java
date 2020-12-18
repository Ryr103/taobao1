package com.example.http1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class Sign_inActivity extends AppCompatActivity {
    private TextView Regiter;
    private ImageButton Back;
    private ImageButton Sign_in;
    private EditText Account;
    private EditText Password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        Regiter = findViewById(R.id.register);

        Sign_in = findViewById(R.id.sign_in);
        Account = findViewById(R.id.account);
        Password = findViewById(R.id.password);


        Password.setTransformationMethod(PasswordTransformationMethod.getInstance()); //设置为密码输入框

        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        Account.setText(sharedPreferences.getString("account",null));
        Password.setText(sharedPreferences.getString("password",null));






        Regiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Sign_inActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });



        Sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String account = Account.getText().toString().trim();
                final String password = Password.getText().toString().trim();
                final String url = "http://49.232.214.94/api/login";

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OkHttpClient client = new OkHttpClient();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("Account", account);
                            jsonObject.put("password", password);
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
                                        Toast.makeText(Sign_inActivity.this, "失败", Toast.LENGTH_SHORT).show();
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
                                    String token = jsonObject1.getString("token");
                                    SharedPreferences sharedPreferences =getSharedPreferences("token", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor =sharedPreferences.edit();
                                    editor.putString("token",token);
                                    editor.commit();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Sign_inActivity.this, msg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Intent intent = new Intent(Sign_inActivity.this,MainActivity.class);
                                    startActivity(intent);
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
}
