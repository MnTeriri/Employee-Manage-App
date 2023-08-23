package com.example.classdesign.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson2.JSON;
import com.example.classdesign.R;
import com.example.classdesign.ServerUtils;
import com.example.classdesign.activity.MainActivity;
import com.example.classdesign.fragment.SalaryFragment;
import com.example.classdesign.model.Salary;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class SalaryDialog extends Dialog implements View.OnClickListener {
    private TextView title;
    private EditText enoEditText;
    private EditText basicSalaryEditText;
    private EditText rewardSalaryEditText;
    private EditText overtimeSalaryEditText;
    private EditText subsidySalaryEditText;
    private EditText reduceSalaryEditText;
    private TextView settlementTimeEditText;
    private LocalDate settlementDate;

    private final MainActivity activity;
    private final SalaryFragment parent;
    private Salary salary = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SalaryDialog(SalaryFragment parent) {
        super(parent.getContext());
        this.activity = (MainActivity) parent.getActivity();
        this.parent = parent;
        setContentView(R.layout.dialog_salary);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SalaryDialog(SalaryFragment parent, Salary salary) {
        super(parent.getContext());
        this.activity = (MainActivity) parent.getActivity();
        this.parent = parent;
        this.salary = salary;
        setContentView(R.layout.dialog_salary);
        init();
        title.setText("修改员工工资信息");
        enoEditText.setText(salary.getEno());
        enoEditText.setFocusable(false);
        basicSalaryEditText.setText(salary.getBasicSalary().toString());
        rewardSalaryEditText.setText(salary.getRewardSalary().toString());
        overtimeSalaryEditText.setText(salary.getOvertimeSalary().toString());
        subsidySalaryEditText.setText(salary.getSubsidySalary().toString());
        reduceSalaryEditText.setText(salary.getReduceSalary().toString());
        settlementDate = salary.getSettlementTime().toLocalDate();
        settlementTimeEditText.setText(settlementDate.toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init() {
        title = findViewById(R.id.title);
        enoEditText = findViewById(R.id.eno);
        basicSalaryEditText = findViewById(R.id.basicSalary);
        rewardSalaryEditText = findViewById(R.id.rewardSalary);
        overtimeSalaryEditText = findViewById(R.id.overtimeSalary);
        subsidySalaryEditText = findViewById(R.id.subsidySalary);
        reduceSalaryEditText = findViewById(R.id.reduceSalary);
        settlementTimeEditText = findViewById(R.id.settlementTime);

        Button confirmButton = findViewById(R.id.confirmButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        settlementTimeEditText.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        settlementDate = LocalDate.now();
        settlementTimeEditText.setText(settlementDate.toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.confirmButton) {
            confirmButton();
        } else if (v.getId() == R.id.cancelButton) {
            cancel();
            parent.displayListView();
        } else if (v.getId() == R.id.settlementTime) {
            settlementTimeInput();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void confirmButton() {
        String eno = enoEditText.getText().toString();
        if (eno.trim().equals("")) {
            Toast.makeText(getContext(), "工号不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (eno.trim().length() != 9) {
            Toast.makeText(getContext(), "工号不足9位！", Toast.LENGTH_SHORT).show();
            return;
        }
        BigDecimal basicSalary = new BigDecimal(basicSalaryEditText.getText().toString());
        BigDecimal rewardSalary = new BigDecimal(rewardSalaryEditText.getText().toString());
        BigDecimal overtimeSalary = new BigDecimal(overtimeSalaryEditText.getText().toString());
        BigDecimal subsidySalary = new BigDecimal(subsidySalaryEditText.getText().toString());
        BigDecimal reduceSalary = new BigDecimal(reduceSalaryEditText.getText().toString());
        LocalDateTime settlementTime = settlementDate.atStartOfDay();
        Salary sal = new Salary(eno, basicSalary, rewardSalary, overtimeSalary, subsidySalary, reduceSalary, settlementTime);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "";
                if (salary == null) {
                    url = "/SalaryServlet/addSalary";
                } else {
                    sal.setId(salary.getId());
                    url = "/SalaryServlet/updateSalary";
                }
                String json = JSON.toJSONString(sal);
                List<NameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("salary", json));
                try {
                    String result = ServerUtils.executeUrl(url, list);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result.equals("true")) {
                                if (salary == null) {
                                    Toast.makeText(getContext(), "添加成功！", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "修改成功！", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (salary == null) {
                                    Toast.makeText(getContext(), "添加失败！", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "修改失败！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                parent.displayListView();
                cancel();
            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void settlementTimeInput() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT);
        datePickerDialog.updateDate(settlementDate.getYear(), settlementDate.getMonthValue() - 1, settlementDate.getDayOfMonth());
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                settlementDate = LocalDate.of(year, month + 1, dayOfMonth);
                settlementTimeEditText.setText(settlementDate.toString());
            }
        });
        datePickerDialog.show();
    }
}
