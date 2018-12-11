package com.example.ryu.menu_subject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {

    ArrayList<ArrayList<String>> mArrayList = new ArrayList<>();

    ViewHolder viewHolder;

    public void setList(ArrayList<ArrayList<String>> arrayList){
        mArrayList.clear();
        mArrayList = arrayList;
        notifyDataSetChanged();
    }

    public ArrayList<ArrayList<String>> getList(){
        return mArrayList;
    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_custom, null);
            viewHolder = new ViewHolder();
            viewHolder.textMenu = convertView.findViewById(R.id.textMenu);
            viewHolder.textPrice = convertView.findViewById(R.id.textPrice);
            convertView.setTag(viewHolder);
        }else{
            convertView.getTag();
        }
        viewHolder.textMenu.setText(mArrayList.get(position).get(0));

        viewHolder.textPrice.setText(mArrayList.get(position).get(1));

        return convertView;
    }

    class ViewHolder{
        TextView textMenu;
        TextView textPrice;
    }
}
