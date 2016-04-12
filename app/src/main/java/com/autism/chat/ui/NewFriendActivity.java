package com.autism.chat.ui;

import android.os.Bundle;
import android.widget.ListView;

import com.autism.chat.R;
import com.autism.chat.adapter.NewFriendAdapter;
import com.autism.chat.base.BaseActivity;

import org.greenrobot.eventbus.Subscribe;

import cn.bmob.newim.event.MessageEvent;


public class NewFriendActivity extends BaseActivity {


    String from="";
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
//        listview = (ListView)findViewById(R.id.list_newfriend);
    }

    @Subscribe
    public void onEventMainThread(MessageEvent event) {
    }
}
