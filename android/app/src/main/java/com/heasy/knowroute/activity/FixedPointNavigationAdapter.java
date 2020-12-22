package com.heasy.knowroute.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.heasy.knowroute.R;
import com.heasy.knowroute.bean.FixedPointCategoryBean;

import java.util.List;

public class FixedPointNavigationAdapter extends BaseAdapter {
    private Context context;
    private List<FixedPointCategoryBean> dataList;

    public FixedPointNavigationAdapter(Context context, List<FixedPointCategoryBean> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList==null ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.fixed_point_navigation_listitem, null);
            textView = (TextView)convertView.findViewById(R.id.category_name);
            convertView.setTag(R.id.category_name, textView);
        }else{
            textView = (TextView)convertView.getTag(R.id.category_name);
        }

        textView.setText(dataList.get(position).getName());

        return textView;
    }
}
