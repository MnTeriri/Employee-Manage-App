package com.example.classdesign.fragment;

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
import com.example.classdesign.adapter.SalaryAdapter;
import com.example.classdesign.dialog.SalaryDialog;
import com.example.classdesign.model.Salary;
import com.example.classdesign.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SalaryFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
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
        View view = inflater.inflate(R.layout.fragment_salary, container, false);
        listView = view.findViewById(R.id.salaryListView);
        searchLayout = view.findViewById(R.id.searchLayout);
        searchEditText = view.findViewById(R.id.searchEditText);
        countSpinner = view.findViewById(R.id.countSpinner);
        Button searchButton = view.findViewById(R.id.searchButton);
        pageLayout = view.findViewById(R.id.pageLayout);
        Button upButton = view.findViewById(R.id.upButton);
        pageTextView = view.findViewById(R.id.pageTextView);
        Button downButton = view.findViewById(R.id.downButton);
        FloatingActionButton addSalaryButton = view.findViewById(R.id.addSalaryButton);
        user = LoginActivity.getUserInstance();
        if (user.getFlag() == 0) {
            searchLayout.setVisibility(View.VISIBLE);
            pageLayout.setVisibility(View.VISIBLE);
            addSalaryButton.setVisibility(View.VISIBLE);
            listView.setOnItemClickListener(this);
            searchButton.setOnClickListener(this);
            upButton.setOnClickListener(this);
            downButton.setOnClickListener(this);
            addSalaryButton.setOnClickListener(this);
        }
        displayListView();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addSalaryButton) {
            SalaryDialog dialog = new SalaryDialog(this);
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
        Salary salary = (Salary) parent.getItemAtPosition(position);
        SalaryDialog dialog = new SalaryDialog(this, salary);
        dialog.show();
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

    public void displayListView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NameValuePair> list = new ArrayList<>();
                String url = "/SalaryServlet/selectSalaryByLimit";
                if (user.getFlag() == 0) {
                    list.add(new BasicNameValuePair("eno", searchEditText.getText().toString()));
                    list.add(new BasicNameValuePair("page", String.valueOf(page)));
                    list.add(new BasicNameValuePair("count", (String) countSpinner.getSelectedItem()));
                } else {
                    list.add(new BasicNameValuePair("eno", user.getEno()));
                    list.add(new BasicNameValuePair("page", String.valueOf(page)));
                    list.add(new BasicNameValuePair("count", (String) countSpinner.getSelectedItem()));
                    url = "/SalaryServlet/selectSalary";
                }
                try {
                    String json = ServerUtils.executeUrl(url, list);
                    List<Salary> data = JSON.parseArray(json, Salary.class);
                    String count = ServerUtils.executeUrl("/SalaryServlet/getSelectSalaryCount", list);
                    maxPage = Integer.parseInt(count) / Integer.parseInt((String) countSpinner.getSelectedItem()) + 1;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SalaryAdapter adapter = new SalaryAdapter(getContext(), data);
                            listView.setAdapter(adapter);
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}