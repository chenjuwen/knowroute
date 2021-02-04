package com.heasy.knowroute.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.heasy.knowroute.R;
import com.heasy.knowroute.bean.FriendBean;

import java.util.List;

public class FriendListAdapter extends BaseAdapter {
    private Context context;
    private List<FriendBean> dataList;

    public FriendListAdapter(Context context, List<FriendBean> dataList){
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
        View view = LayoutInflater.from(context).inflate(R.layout.friends_listitem, null);

        TextView textView1 = (TextView)view.findViewById(R.id.nickname);
        textView1.setText(dataList.get(position).getNickname());

        TextView textView2 = (TextView)view.findViewById(R.id.phone);
        textView2.setText(dataList.get(position).getPhone());

        return view;
    }
}
