package com.example.classdesign.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.classdesign.R;
import com.example.classdesign.model.Salary;

import java.util.List;

public class SalaryAdapter extends BaseAdapter {
    private Context context;
    List<Salary> salaries;

    public SalaryAdapter(Context context, List<Salary> salaries) {
        this.context = context;
        this.salaries = salaries;
    }

    @Override
    public int getCount() {
        return salaries.size();
    }

    @Override
    public Object getItem(int position) {
        return salaries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.salary_item, parent, false);
        TextView ename = convertView.findViewById(R.id.ename);
        TextView eno = convertView.findViewById(R.id.eno);
        TextView basicSalary = convertView.findViewById(R.id.basicSalary);
        TextView rewardSalary = convertView.findViewById(R.id.rewardSalary);
        TextView overtimeSalary = convertView.findViewById(R.id.overtimeSalary);
        TextView subsidySalary = convertView.findViewById(R.id.subsidySalary);
        TextView reduceSalary = convertView.findViewById(R.id.reduceSalary);
        TextView totalSalary = convertView.findViewById(R.id.totalSalary);
        TextView settlementTime = convertView.findViewById(R.id.settlementTime);

        Salary salary = salaries.get(position);
        ename.setText(salary.getEname());
        eno.setText(salary.getEno());
        basicSalary.setText(salary.getBasicSalary().toString());
        rewardSalary.setText(salary.getRewardSalary().toString());
        overtimeSalary.setText(salary.getOvertimeSalary().toString());
        subsidySalary.setText(salary.getSubsidySalary().toString());
        reduceSalary.setText(salary.getReduceSalary().toString());
        totalSalary.setText(salary.getTotalSalary().toString());
        settlementTime.setText(salary.getSettlementTime().toLocalDate().toString());

        return convertView;
    }
}
