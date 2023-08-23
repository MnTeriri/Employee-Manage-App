package com.example.classdesign.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.classdesign.R;

import java.util.ArrayList;

public class MySelfAdapter extends BaseAdapter {
    private ArrayList<String> itemName = new ArrayList<>();
    private Context context;

    public MySelfAdapter(Context context) {
        this.context = context;
        itemName.add("修改密码");
        itemName.add("退出登录");
    }

    @Override
    public int getCount() {
        return itemName.size();
    }

    @Override
    public Object getItem(int position) {
        return itemName.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.myself_item, parent, false);
        ImageView imageView = convertView.findViewById(R.id.item_icon);
        TextView textView = convertView.findViewById(R.id.item_name);

        textView.setText(itemName.get(position));
        return convertView;
    }
}
