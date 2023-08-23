package com.example.classdesign.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson2.JSON;
import com.example.classdesign.R;
import com.example.classdesign.ServerUtils;
import com.example.classdesign.activity.MainActivity;
import com.example.classdesign.fragment.EmployeeFragment;
import com.example.classdesign.model.Employee;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDialog extends Dialog implements View.OnClickListener {
    private TextView title;
    private EditText enoEditText;
    private EditText enameEditText;
    private RadioGroup sexGroup;
    private EditText ageEditText;
    private Spinner deptSpinner;
    private Spinner appointmentSpinner;
    private Spinner qualificationSpinner;
    private TextView entryTimeEditText;
    private LocalDate entryDate;
    private EditText phoneEditText;
    private ImageView imageView;
    private Bitmap bitmap;
    private final MainActivity activity;
    private final EmployeeFragment parent;
    private Employee employee = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init() {
        title = findViewById(R.id.title);
        enoEditText = findViewById(R.id.eno);
        enameEditText = findViewById(R.id.ename);
        sexGroup = findViewById(R.id.sexGroup);
        ageEditText = findViewById(R.id.age);
        deptSpinner = findViewById(R.id.dept);
        appointmentSpinner = findViewById(R.id.appointment);
        qualificationSpinner = findViewById(R.id.qualification);
        entryTimeEditText = findViewById(R.id.entryTime);
        phoneEditText = findViewById(R.id.phone);
        imageView = findViewById(R.id.image);
        Button imageButton = findViewById(R.id.imageButton);
        Button confirmButton = findViewById(R.id.confirmButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        entryTimeEditText.setOnClickListener(this);
        imageButton.setOnClickListener(this);
        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        entryDate = LocalDate.now();
        entryTimeEditText.setText(entryDate.toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public EmployeeDialog(EmployeeFragment parent) {
        super(parent.getContext());
        this.activity = (MainActivity) parent.getActivity();
        this.parent = parent;
        setContentView(R.layout.dialog_employee);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public EmployeeDialog(EmployeeFragment parent, Employee employee) {
        super(parent.getContext());
        this.parent = parent;
        this.activity = (MainActivity) parent.getActivity();
        this.employee = employee;
        setContentView(R.layout.dialog_employee);
        init();
        title.setText("修改员工信息");
        enoEditText.setText(employee.getEno());
        enoEditText.setFocusable(false);
        enameEditText.setText(employee.getEname());
        if (employee.getSex().equals("男")) {
            RadioButton button = findViewById(R.id.male);
            button.setChecked(true);
        } else {
            RadioButton button = findViewById(R.id.female);
            button.setChecked(true);
        }
        ageEditText.setText(employee.getAge().toString());
        SpinnerAdapter adapter = deptSpinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(employee.getDept())) {
                deptSpinner.setSelection(i);
                break;
            }
        }
        adapter = appointmentSpinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(employee.getAppointment())) {
                appointmentSpinner.setSelection(i);
                break;
            }
        }
        adapter = qualificationSpinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(employee.getQualification())) {
                qualificationSpinner.setSelection(i);
                break;
            }
        }
        entryDate = employee.getEntryTime().toLocalDate();
        entryTimeEditText.setText(entryDate.toString());
        phoneEditText.setText(employee.getPhone());
        bitmap = ServerUtils.decodeImageString(employee.getImageString());
        imageView.setImageBitmap(bitmap);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageButton) {
            imageButton();
        } else if (v.getId() == R.id.confirmButton) {
            confirmButton();
        } else if (v.getId() == R.id.cancelButton) {
            cancel();
            parent.displayListView();
        } else if (v.getId() == R.id.entryTime) {
            entryTimeInput();
        }
    }

    private void imageButton() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, 1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (activity.getBitmap() == null) {
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bitmap = activity.getBitmap();
                                imageView.setImageBitmap(bitmap);
                                activity.setBitmap(null);
                            }
                        });
                        break;
                    }
                }
            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void entryTimeInput() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_HOLO_LIGHT);
        datePickerDialog.updateDate(entryDate.getYear(), entryDate.getMonthValue() - 1, entryDate.getDayOfMonth());
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                entryDate = LocalDate.of(year, month + 1, dayOfMonth);
                entryTimeEditText.setText(entryDate.toString());
            }
        });
        datePickerDialog.show();
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
        String ename = enameEditText.getText().toString();
        if (ename.trim().equals("")) {
            Toast.makeText(getContext(), "姓名不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton sexButton = findViewById(sexGroup.getCheckedRadioButtonId());
        String sex = sexButton.getText().toString();
        String str = ageEditText.getText().toString();
        if (str.trim().equals("")) {
            Toast.makeText(getContext(), "年龄不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        Integer age = Integer.parseInt(str);
        String dept = (String) deptSpinner.getSelectedItem();
        String appointment = (String) appointmentSpinner.getSelectedItem();
        String qualification = (String) qualificationSpinner.getSelectedItem();
        LocalDateTime entryTime = entryDate.atStartOfDay();
        String phone = phoneEditText.getText().toString();
        if (phone.trim().equals("")) {
            Toast.makeText(getContext(), "电话号码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.trim().length() != 11) {
            Toast.makeText(getContext(), "电话号码不足11位！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (bitmap == null) {
            Toast.makeText(getContext(), "请选择照片！", Toast.LENGTH_SHORT).show();
            return;
        }
        Employee emp = new Employee(eno, ename, sex, age, dept, appointment, entryTime, qualification, ServerUtils.encodeImageBitmap(bitmap), phone);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String json = JSON.toJSONString(emp);
                List<NameValuePair> list = new ArrayList<>();
                list.add(new BasicNameValuePair("employee", json));
                String url = "";
                if (employee == null) {
                    url = "/EmployeeServlet/addEmployee";
                } else {
                    url = "/EmployeeServlet/updateEmployee";
                }
                try {
                    String result = ServerUtils.executeUrl(url, list);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result.equals("true")) {
                                if (employee == null) {
                                    Toast.makeText(getContext(), "添加成功！", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "修改成功！", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (employee == null) {
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
}
