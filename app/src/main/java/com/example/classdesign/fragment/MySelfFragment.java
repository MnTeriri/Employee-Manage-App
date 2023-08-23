package com.example.classdesign.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson2.JSON;
import com.example.classdesign.R;
import com.example.classdesign.ServerUtils;
import com.example.classdesign.activity.LoginActivity;
import com.example.classdesign.adapter.MySelfAdapter;
import com.example.classdesign.model.User;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MySelfFragment extends Fragment implements AdapterView.OnItemClickListener {
    private User user = null;
    private ImageView imageView;
    private TextView uidTextView;
    private TextView unameTextView;
    private SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myself, container, false);
        MySelfAdapter adapter = new MySelfAdapter(view.getContext());
        ListView listView = view.findViewById(R.id.myselfListView);
        user = LoginActivity.getUserInstance();
        imageView = view.findViewById(R.id.uImage);
        uidTextView = view.findViewById(R.id.uid);
        unameTextView = view.findViewById(R.id.uname);
        sharedPreferences = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);

        uidTextView.setText(user.getUid());
        unameTextView.setText(user.getUname());
        System.out.println(user.getUname());
        Bitmap bitmap = ServerUtils.decodeImageString(user.getImageString());
        RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), bitmap);
        roundedDrawable.setCornerRadius(60);
        imageView.setImageDrawable(roundedDrawable);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return view;
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("user");
        editor.commit();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void updatePassword() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        AlertDialog dialog = builder.create();
        View view = View.inflate(getContext(), R.layout.dialog_password, null);
        dialog.setView(view);
        EditText beforePassword=view.findViewById(R.id.beforePassword);
        EditText password=view.findViewById(R.id.password);
        EditText afterPassword=view.findViewById(R.id.afterPassword);
        Button updateButton=view.findViewById(R.id.updateButton);
        Button cancelButton=view.findViewById(R.id.cancelButton);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str1=beforePassword.getText().toString();
                String str2=password.getText().toString();
                String str3=afterPassword.getText().toString();
                if(str2.equals(str3)){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<NameValuePair> list=new ArrayList<>();
                                list.add(new BasicNameValuePair("uid",user.getUid()));
                                list.add(new BasicNameValuePair("password",str1));
                                String json = ServerUtils.executeUrl("/UserServlet/login", list);
                                User u= JSON.parseObject(json, User.class);
                                if(u.getId()!=null){
                                    list.clear();
                                    list.add(new BasicNameValuePair("uid",user.getUid()));
                                    list.add(new BasicNameValuePair("password",str2));
                                    String result = ServerUtils.executeUrl("/UserServlet/updateUserPassword", list);
                                    if(result.equals("true")){
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(),"修改成功，请重新登录！",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        Thread.sleep(1000);
                                        logout();
                                    }else {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(),"修改失败！",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                }else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(),"原密码错误！",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } catch (IOException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }).start();
                }else {
                    Toast.makeText(getContext(),"两次输入密码不同！",Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String str = (String) parent.getItemAtPosition(position);
        if (str.equals("退出登录")) {
            logout();
        } else if (str.equals("修改密码")) {
            updatePassword();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}