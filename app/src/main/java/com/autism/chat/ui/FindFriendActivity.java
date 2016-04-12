package com.autism.chat.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.autism.chat.R;
import com.autism.chat.adapter.FindFriendAdapter;
import com.autism.chat.bean.AddFriendMessage;
import com.autism.chat.bean.User;
import com.autism.chat.bean.UserModel;
import com.autism.chat.receiver.AddFriendEvent;
import com.autism.chat.receiver.ChatEvent;
import com.orhanobut.logger.Logger;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 查找好友，分析：
 */
public class FindFriendActivity extends FragmentActivity {

    private EditText filename;
    private SwipeRefreshLayout sp;
    private ListView rc;
    private FindFriendAdapter<User> adapter;
    private BmobIMConversation c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        EventBus.getDefault().register(this);
        initView();

    }


    public Bundle getBundle() {
        if (getIntent() != null && getIntent().hasExtra(getPackageName()))
            return getIntent().getBundleExtra(getPackageName());
        else
            return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        filename = (EditText) findViewById(R.id.et_find);
        sp = (SwipeRefreshLayout) findViewById(R.id.sw_refresh);
        rc = (ListView) findViewById(R.id.rc_view);
        sp.setClickable(false);
    }

    public void find(View v) {
        String name = filename.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(FindFriendActivity.this, "智障,不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        sp.setRefreshing(true);
        UserModel.getInstance().queryUsers(name, 20, new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                sp.setRefreshing(false);
                adapter = new FindFriendAdapter<User>(FindFriendActivity.this, list);
                rc.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(FindFriendActivity.this, "查找失败", Toast.LENGTH_SHORT).show();
                sp.setRefreshing(false);
            }
        });
    }

    //订阅者,哈哈哈哈哈哈 ，大神附体了，一下思路特别清晰，
    //发布一个intent，在这接受，
    //跳转到聊天界面
    @Subscribe
    public void onEventMainThread(AddFriendEvent event) {
        BmobIMConversation b = (BmobIMConversation) event.intent.getBundleExtra(getPackageName()).getSerializable("a");
        BmobIMConversation c = BmobIMConversation.obtain(BmobIMClient.getInstance(), b);
        //发送请求消息
        AddFriendMessage add = new AddFriendMessage();
        add.setContent(UserModel.getInstance().getCurrentUser().getUsername() + " 加你为好友");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("friend", "加个好友呗");
        add.setExtraMap(map);
        c.sendMessage(add, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                if (e == null) {
                    Toast.makeText(FindFriendActivity.this, "好友请求发送成功,等待添加,,,", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FindFriendActivity.this, "发送失败,,," + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
