package com.example.classdesign.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.example.classdesign.R;
import com.example.classdesign.ServerUtils;
import com.example.classdesign.model.Employee;

import java.util.List;

public class EmployeeAdapter extends BaseAdapter {
    private Context context;
    List<Employee> employees;

    public EmployeeAdapter(Context context, List<Employee> employees) {
        this.context = context;
        this.employees = employees;
    }

    @Override
    public int getCount() {
        return employees.size();
    }

    @Override
    public Object getItem(int position) {
        return employees.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.employee_item, parent, false);
        ImageView imageView = convertView.findViewById(R.id.image);
        TextView ename = convertView.findViewById(R.id.ename);
        TextView sex = convertView.findViewById(R.id.sex);
        TextView age = convertView.findViewById(R.id.age);
        TextView eno = convertView.findViewById(R.id.eno);
        TextView dept = convertView.findViewById(R.id.dept);
        TextView appointment = convertView.findViewById(R.id.appointment);
        TextView entryTime = convertView.findViewById(R.id.entryTime);
        TextView qualification = convertView.findViewById(R.id.qualification);
        TextView phone = convertView.findViewById(R.id.phone);
        Employee employee = employees.get(position);
        String imageString = employee.getImageString();
        Bitmap bitmap = ServerUtils.decodeImageString(imageString);
        RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        roundedDrawable.setCornerRadius(80);
        imageView.setImageDrawable(roundedDrawable);
        ename.setText(employee.getEname());
        sex.setText(employee.getSex());
        age.setText(String.valueOf(employee.getAge()));
        eno.setText(employee.getEno());
        dept.setText(employee.getDept());
        appointment.setText(employee.getAppointment());
        entryTime.setText(employee.getEntryTime().toLocalDate().toString());
        qualification.setText(employee.getQualification());
        phone.setText(employee.getPhone());
        return convertView;
    }
}
