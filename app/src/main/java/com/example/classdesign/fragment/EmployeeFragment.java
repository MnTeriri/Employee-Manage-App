package com.example.classdesign.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson2.JSON;
import com.example.classdesign.R;
import com.example.classdesign.ServerUtils;
import com.example.classdesign.activity.LoginActivity;
import com.example.classdesign.adapter.EmployeeAdapter;
import com.example.classdesign.dialog.EmployeeDialog;
import com.example.classdesign.model.Employee;
import com.example.classdesign.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private User user;
    private ListView listView;
    private LinearLayout searchLayout;
    private EditText searchEditText;
    private Spinner countSpinner;
    private LinearLayout pageLayout;
    private TextView pageTextView;

    private String str = "";
    private int page = 1;
    private int maxPage = 1;//最大页数

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employee, container, false);
        listView = view.findViewById(R.id.employeeListView);
        searchLayout = view.findViewById(R.id.searchLayout);
        searchEditText = view.findViewById(R.id.searchEditText);
        countSpinner = view.findViewById(R.id.countSpinner);
        Button searchButton = view.findViewById(R.id.searchButton);
        pageLayout = view.findViewById(R.id.pageLayout);
        Button upButton = view.findViewById(R.id.upButton);
        pageTextView = view.findViewById(R.id.pageTextView);
        Button downButton = view.findViewById(R.id.downButton);
        FloatingActionButton addEmployeeButton = view.findViewById(R.id.addEmployeeButton);
        user = LoginActivity.getUserInstance();
        if (user.getFlag() == 0) {
            listView.setOnItemLongClickListener(this);
            searchLayout.setVisibility(View.VISIBLE);
            pageLayout.setVisibility(View.VISIBLE);
            addEmployeeButton.setVisibility(View.VISIBLE);
            searchButton.setOnClickListener(this);
            upButton.setOnClickListener(this);
            downButton.setOnClickListener(this);
            addEmployeeButton.setOnClickListener(this);
        }
        listView.setOnItemClickListener(this);
        displayListView();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addEmployeeButton) {
            EmployeeDialog dialog = new EmployeeDialog(this);
            dialog.show();
        } else if (v.getId() == R.id.upButton) {
            upButton();
        } else if (v.getId() == R.id.downButton) {
            downButton();
        } else if (v.getId() == R.id.searchButton) {
            searchButton();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Employee employee = (Employee) parent.getItemAtPosition(position);
        EmployeeDialog dialog = new EmployeeDialog(this, employee);
        dialog.show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("是否删除该员工？");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Employee employee = (Employee) parent.getItemAtPosition(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<NameValuePair> list = new ArrayList<>();
                            list.add(new BasicNameValuePair("eno", employee.getEno()));
                            String result = ServerUtils.executeUrl("/EmployeeServlet/deleteEmployee", list);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (result.equals("true")) {
                                        Toast.makeText(getContext(), "删除成功！", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "删除失败！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        displayListView();
                        dialog.dismiss();
                    }
                }).start();
            }
        });

        builder.setNegativeButton("否", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    public void displayListView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NameValuePair> list = new ArrayList<>();
                if (user.getFlag() == 0) {
                    list.add(new BasicNameValuePair("eno", searchEditText.getText().toString()));
                    list.add(new BasicNameValuePair("page", String.valueOf(page)));
                    list.add(new BasicNameValuePair("count", (String) countSpinner.getSelectedItem()));
                } else {
                    list.add(new BasicNameValuePair("eno", user.getEno()));
                    list.add(new BasicNameValuePair("page", "1"));
                    list.add(new BasicNameValuePair("count", "1"));
                }
                try {
                    String json = ServerUtils.executeUrl("/EmployeeServlet/selectEmployeeByLimit", list);
                    List<Employee> data = JSON.parseArray(json, Employee.class);
                    String count = ServerUtils.executeUrl("/EmployeeServlet/getSelectEmployeeCount", list);
                    maxPage = Integer.parseInt(count) / Integer.parseInt((String) countSpinner.getSelectedItem()) + 1;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EmployeeAdapter adapter = new EmployeeAdapter(getContext(), data);
                            listView.setAdapter(adapter);
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void upButton() {
        if (page == 1) {
            Toast.makeText(getContext(), "已经是第一页！", Toast.LENGTH_SHORT).show();
        } else {
            page--;
            displayListView();
            pageTextView.setText("第" + page + "页");
        }
    }

    private void downButton() {
        if (page == maxPage) {
            Toast.makeText(getContext(), "已经是最后一页！", Toast.LENGTH_SHORT).show();
        } else {
            page++;
            displayListView();
            pageTextView.setText("第" + page + "页");
        }
    }

    private void searchButton() {
        page = 1;
        displayListView();
        if (!str.equals(searchEditText.getText().toString())) {
            page = 1;
            str = searchEditText.getText().toString();
        }
        pageTextView.setText("第" + page + "页");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        countSpinner.setSelection(0);
    }
}