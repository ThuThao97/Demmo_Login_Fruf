package com.example.admin.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.model.ListOffice;

import java.util.ArrayList;

public class listviewAdapter extends BaseAdapter {
    private Context context;
    private int resourt;
    public ArrayList<ListOffice> arrayList;

    public listviewAdapter(Context context, int resourt, ArrayList<ListOffice> arrayList) {
        this.context = context;
        this.resourt = resourt;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        TextView txt_name, txt_phone, txt_address;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item, null);
            viewHolder = new ViewHolder();
            viewHolder.txt_address = convertView.findViewById(R.id.txt_address);
            viewHolder.txt_name = convertView.findViewById(R.id.txt_name);
            viewHolder.txt_phone = convertView.findViewById(R.id.txt_phone);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ListOffice item = arrayList.get(position);
        viewHolder.txt_phone.setText(item.getPhone().toString());
        viewHolder.txt_name.setText(item.getName().toString());
        viewHolder.txt_address.setText(item.getAddress().toString());
        return convertView;
    }

}
