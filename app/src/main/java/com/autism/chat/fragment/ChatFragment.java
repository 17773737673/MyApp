package com.autism.chat.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.autism.chat.ChatApplication;
import com.autism.chat.R;
import com.autism.chat.adapter.ChatAdapter;
import com.autism.chat.base.BaseFragment;
import com.autism.chat.receiver.ChatEvent;
import com.autism.chat.ui.ChatActivity;

import org.greenrobot.eventbus.Subscribe;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;


/**
 * Created by AutismPerson on 4/5 0005.
 */
public class ChatFragment extends BaseFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ChatAdapter adapter;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, null);
        listView = (ListView) view.findViewById(R.id.lv);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        adapter = new ChatAdapter(ChatApplication.getInstance(), BmobIM.getInstance().loadAllConversation());
        listView.setAdapter(adapter);
        return view;
    }

    private void initData() {
        adapter.upData(BmobIM.getInstance().loadAllConversation());
        adapter.notifyDataSetChanged();
    }

    /**
     * 注册离线消息接收事件
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event) {
        //重新刷新列表
        initData();
    }

    /**
     * 注册消息接收事件
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        initData();
    }


    /**
     * 点击创建私聊会话
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView unread = (TextView) view.findViewById(R.id.unread);
        if (View.VISIBLE == unread.getVisibility()) {
            unread.setVisibility(View.INVISIBLE);
        }
        Bundle bundle = new Bundle();
        BmobIMConversation c = (BmobIMConversation) adapter.getItem(position);
        bundle.putSerializable("c", c);
        start(ChatActivity.class, bundle, false);
    }

    @Subscribe
    public void onEventMainThread(ChatEvent events) {

        BmobIMUserInfo fromUserInfo = events.info;
        BmobIM.getInstance().updateUserInfo(fromUserInfo);
        BmobIM.getInstance().loadAllConversation();
        adapter.notifyDataSetChanged();
    }

    /**
     * 长按删除
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     * @return
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        BmobIM.getInstance().deleteConversation((BmobIMConversation) adapter.getItem(position));
        adapter.remove(position);
        return true;
    }

    @Override
    public void onResume() {
        initData();
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
