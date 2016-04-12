package com.autism.chat.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.autism.chat.ChatApplication;
import com.autism.chat.R;
import com.autism.chat.adapter.ContactAdapter;
import com.autism.chat.base.BaseFragment;
import com.autism.chat.bean.User;
import com.autism.chat.bean.UserModel;
import com.autism.chat.receiver.UpDataEvent;
import com.autism.chat.ui.ChatActivity;
import com.autism.chat.ui.NewFriendActivity;


import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by AutismPerson on 4/5 0005.
 */
public class ContactFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView listview;

    ContactAdapter adapter;
    private BmobIMUserInfo info;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, null);
        Button button = (Button) view.findViewById(R.id.add);
        button.setOnClickListener(this);
        listview = (ListView) view.findViewById(R.id.lv);
        listview.setOnItemClickListener(this);
        query();
        return view;
    }

    private void query() {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereNotEqualTo("username", UserModel.getInstance().getCurrentUser().getUsername());
        query.setLimit(10000);
        query.order("createdAt");
        //先从缓存获取，不管怎样都会从网络获取一次，保持实时更新好友状态
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.findObjects(ChatApplication.getInstance(), new FindListener<User>() {
            @Override
            public void onSuccess(List<User> list) {
                adapter = new ContactAdapter(ChatApplication.getInstance(), list);
                listview.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        startActivity(new Intent(mActivity, NewFriendActivity.class));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

        //用户对象
        User friend = (User) adapter.getItem(position);

        //用户信息
        info = new BmobIMUserInfo(friend.getObjectId(), friend.getUsername(), friend.getAvatar());
        //私聊
        BmobIM.getInstance().startPrivateConversation(info, new ConversationListener() {
            @Override
            public void done(BmobIMConversation c, BmobException e) {
                if (e == null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("c", c);
                    start(ChatActivity.class, bundle, false);
                } else {
                    Toast.makeText(ChatApplication.getInstance(), e.getMessage() + "(" + e.getErrorCode() + ")", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Subscribe
    public void onEventMainThread(MessageEvent event){}
}
