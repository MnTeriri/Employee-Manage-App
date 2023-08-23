package com.example.classdesign.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson2.JSON;
import com.example.classdesign.R;
import com.example.classdesign.ServerUtils;
import com.example.classdesign.model.User;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    private final boolean useThemestatusBarColor = false;
    //是否使用状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置
    private final boolean useStatusBarColor = true;

    private static User userInstance = null;
    private ImageView userImageView;
    private EditText uidEditText;
    private EditText pwdEditText;
    private Button loginButton;
    private CheckBox checkBox;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        setStatusBar();
        userImageView = findViewById(R.id.userImage);
        uidEditText = findViewById(R.id.userId);
        pwdEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        checkBox = findViewById(R.id.checkBox);
        loginButton.setOnClickListener(this);
        uidEditText.addTextChangedListener(this);
        sharedPreferences = getSharedPreferences("userInfo", MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user", "");
        if (!userJson.equals("")) {
            userInstance = JSON.parseObject(userJson, User.class);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        String uid = uidEditText.getText().toString();
        String password = pwdEditText.getText().toString();
        LoginActivity instance = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<NameValuePair> data = new ArrayList<>();
                    data.add(new BasicNameValuePair("uid", uid));
                    data.add(new BasicNameValuePair("password", password));
                    String json = ServerUtils.executeUrl("/UserServlet/login", data);
                    User user = JSON.parseObject(json, User.class);
                    if (user.getId() == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(instance, "登录失败！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        if(checkBox.isChecked()){
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("user", json);
                            editor.apply();
                        }
                        userInstance = user;
                        startActivity(intent);
                        finish();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }


    @Override
    public void afterTextChanged(Editable s) {
        String uid = s.toString();
        if (uid.trim().length() == 9) {
            new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    try {
                        List<NameValuePair> data = new ArrayList<>();
                        data.add(new BasicNameValuePair("uid", uid));
                        String json = ServerUtils.executeUrl("/UserServlet/selectUserByUid", data);
                        User user = JSON.parseObject(json, User.class);
                        String pictureString = user.getImageString();
                        if (pictureString != null) {
                            Bitmap bitmap = ServerUtils.decodeImageString(pictureString);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                                    roundedDrawable.setCornerRadius(100);
                                    userImageView.setImageDrawable(roundedDrawable);
                                }
                            });
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        } else {
            userImageView.setImageBitmap(null);
        }
    }

    private void setStatusBar() {
        //5.0及以上
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        //根据上面设置是否对状态栏单独设置颜色
        if (useThemestatusBarColor) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colortheme));//设置状态栏背景色
        } else {
            getWindow().setStatusBarColor(Color.TRANSPARENT);//透明
        }
        if (useStatusBarColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public static User getUserInstance() {
        return userInstance;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}