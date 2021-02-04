package com.heasy.knowroute.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.heasy.knowroute.R;
import com.heasy.knowroute.bean.FriendBean;
import com.heasy.knowroute.core.DefaultDaemonThread;
import com.heasy.knowroute.core.service.ServiceEngineFactory;
import com.heasy.knowroute.event.GetFriendListEvent;
import com.heasy.knowroute.service.backend.FriendAPI;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FriendListActivity extends BaseActivity implements View.OnClickListener {
    private static final Logger logger = LoggerFactory.getLogger(FriendListActivity.class);
    private TextView btnBack;
    private ListView list_view;
    private List<FriendBean> dataList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        hideActionBar();

        btnBack = (TextView)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        list_view = (ListView) findViewById(R.id.list_view);

        ServiceEngineFactory.getServiceEngine().getEventService().register(this);

        loadData();
    }

    private void loadData(){
        new DefaultDaemonThread(){
            @Override
            public void run() {
                //加载数据
                dataList = FriendAPI.getFriendList(true);
                if(dataList != null && dataList.size() > 0){
                    ServiceEngineFactory.getServiceEngine().getEventService()
                            .postEvent(new GetFriendListEvent(this, ""));
                }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnBack){
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleGetFriendListEvent(final GetFriendListEvent event) {
        if (event != null) {
            FriendListAdapter adapter = new FriendListAdapter(FriendListActivity.this, dataList);
            list_view.setAdapter(adapter);
            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    FriendBean friendBean = dataList.get(position);

                    //参数值回传
                    Intent intent = new Intent();
                    intent.putExtra("userName", friendBean.getNickname());
                    intent.putExtra("userNumber", friendBean.getPhone());
                    setResult(Activity.RESULT_OK, intent);

                    //关闭Activity
                    finish();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServiceEngineFactory.getServiceEngine().getEventService().unregister(this);
    }

}
