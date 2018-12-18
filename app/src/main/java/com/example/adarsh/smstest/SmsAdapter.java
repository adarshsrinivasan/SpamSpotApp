package com.example.adarsh.smstest;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADARSH on 15-03-2017.
 */
public class SmsAdapter extends ArrayAdapter {
    List list = new ArrayList();
    LayoutInflater li;
    public SmsAdapter(Context context, int resource) {
        super(context, resource);
    }

    static class ViewHolder{
        TextView msg;
        TextView contact;
        TextView type;
        CheckBox box;
    }
    public void clean(){
        list.clear();
    }
    @Override
    public void add(Object object) {
        super.add(object);
        list.add(object);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();
        if(row == null){
            li = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = li.inflate(R.layout.row,parent,false);
            holder.msg=(TextView)row.findViewById(R.id.lblMsg);
            holder.contact=(TextView)row.findViewById(R.id.lblNumber);
            holder.type=(TextView)row.findViewById(R.id.lbltype);
            holder.box = (CheckBox)row.findViewById(R.id.checkBox);
            row.setTag(holder);
        }
        else {
            holder = (ViewHolder)row.getTag();
        }
        Data_SmsInbox dp = (Data_SmsInbox)this.getItem(position);
        holder.msg.setText(dp.getMsg());
        holder.contact.setText(dp.getContact());
        holder.type.setText(dp.getType());
        if(dp.getType().trim().equals("negative")){
            holder.type.setTextColor(Color.RED);
            holder.box.setChecked(true);
        }
        else{
            holder.type.setTextColor(Color.parseColor("#66ff00"));
            holder.box.setChecked(false);
        }
        return row;
    }
    public List getList(){return  list;}
}
