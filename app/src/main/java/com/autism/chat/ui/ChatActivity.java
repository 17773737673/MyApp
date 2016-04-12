package com.autism.chat.ui;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.autism.chat.R;
import com.autism.chat.adapter.MessageChatAdapter;
import com.autism.chat.base.BaseActivity;
import com.autism.chat.bean.User;
import com.autism.chat.bean.listener.EditTextChangeListener;
import com.autism.chat.voice.ButtonManager;
import com.autism.chat.voice.MediaManager;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.w3c.dom.Text;

import java.util.List;

import cn.bmob.newim.bean.BmobIMAudioMessage;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.newim.listener.ObseverListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;


public class ChatActivity extends BaseActivity implements View.OnClickListener, ButtonManager.AudioFinishRecorderListener, ObseverListener {

    private EditText eet;
    private ListView list;
    private MessageChatAdapter adapter;
    public BmobIMConversation c;
    private SwipeRefreshLayout srf;
    private LinearLayout linearLayout;
    private Button voice;
    private Button msgsend;
    private ButtonManager voicesend;
    private Button change;
    private Button emo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        EventBus.getDefault().register(this);
        initView();
        autoRefresh();
    }

    private void initView() {
        eet = (EditText) findViewById(R.id.edit_user_comment);
        list = (ListView) findViewById(R.id.mListView);
        voice = (Button) findViewById(R.id.voice);
        msgsend = (Button) findViewById(R.id.msg_send);
        voicesend = (ButtonManager) findViewById(R.id.send_voice);
        change = (Button) findViewById(R.id.change);
        emo = (Button) findViewById(R.id.btn_user_comment_emo);
        emo.setOnClickListener(this);
        change.setOnClickListener(this);
        voice.setOnClickListener(this);
        voicesend.setAudioFinishRecorderListener(this);
        msgsend.setOnClickListener(this);
        linearLayout = (LinearLayout) findViewById(R.id.ll_chat);
        srf = (SwipeRefreshLayout) findViewById(R.id.sw_refresh);

        //在聊天页面的onCreate方法中，通过如下方法创建新的会话实例，能拿到当前传过来对象的数据
        c = BmobIMConversation.obtain(BmobIMClient.getInstance(), (BmobIMConversation) getBundle().getSerializable("c"));
        adapter = new MessageChatAdapter(this, c);
        list.setAdapter(adapter);

        //文本变化监听
        eet.addTextChangedListener(new EditTextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    voice.setVisibility(View.GONE);
                    msgsend.setVisibility(View.VISIBLE);
                } else {
                    msgsend.setVisibility(View.GONE);
                    voice.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 自动刷新与手动刷新
     */
    private void autoRefresh() {
        srf.setEnabled(true);
        linearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                linearLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                srf.setRefreshing(false);
                queryMessages(null);
                list.setSelection(adapter.getCount());      //第一次打开定位到最后一条
            }
        });

        srf.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BmobIMMessage firstMessage = adapter.getFirstMessage();
                queryMessages(firstMessage);
            }
        });
    }

    /**
     * @param msg 加载消息
     */
    public void queryMessages(BmobIMMessage msg) {
        c.queryMessages(msg, 10, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> lists, BmobException e) {
                if (e == null) {
                    if (null != list && lists.size() > 0) {
                        adapter.addMessages(lists);
                        srf.setRefreshing(false);
                    } else {
                        srf.setRefreshing(false);
                    }
                } else {
                    srf.setRefreshing(false);
                }
            }
        });
    }

    /**
     * 消息发送监听器
     */
    public MessageSendListener listener = new MessageSendListener() {

        @Override
        public void onProgress(int value) {
            super.onProgress(value);
            //文件类型的消息才有进度值
            Logger.i("onProgress：" + value);
        }

        @Override
        public void onStart(BmobIMMessage msg) {
            super.onStart(msg);
            adapter.addAllMessage(msg);
            eet.setText("");
        }

        @Override
        public void done(BmobIMMessage msg, BmobException e) {
            adapter.notifyDataSetChanged();
            eet.setText("");
            if (e != null) {
                toast(e.getMessage());
            }
        }
    };

    /**
     * 接收到聊天消息
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        addMessage2Chat(event);
    }


    /**
     * 添加消息到聊天界面中
     *
     * @param event
     */
    private void addMessage2Chat(MessageEvent event) {
        BmobIMMessage msg = event.getMessage();
        Logger.i("接收到消息：" + msg.getContent());
        if (c != null && event != null && c.getConversationId().equals(event.getConversation().getConversationId()) //如果是当前会话的消息
                && !msg.isTransient()) {//并且不为暂态消息
            if (adapter.findPosition(msg) < 0) {
                adapter.addAllMessage(msg);
                //更新该会话下面的已读状态
                c.updateReceiveStatus(msg);
                list.setSelection(adapter.getCount());
            }
        } else {
            Logger.i("不是与当前聊天对象的消息");
        }
    }

    /**
     * 自定义Button语音接口
     * @param seconds
     * @param filePath
     */
    @Override
    public void onFinish(float seconds, String filePath) {
        BmobIMAudioMessage audio = new BmobIMAudioMessage(filePath);
        c.sendMessage(audio,listener);
        list.setSelection(adapter.getCount());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.msg_send:
                String msg = eet.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    return;
                }
                //消息文本类型
                BmobIMTextMessage textmsg = new BmobIMTextMessage();
                //添加数据
                textmsg.setContent(msg);
                c.sendMessage(textmsg, listener);
                list.setSelection(adapter.getCount());
                break;
            case R.id.send_voice:

                break;
            case R.id.voice:

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(eet.getWindowToken(),0);
                eet.setVisibility(View.GONE);
                voicesend.setVisibility(View.VISIBLE);
                change.setVisibility(View.VISIBLE);
                voice.setVisibility(View.GONE);
                msgsend.setVisibility(View.GONE);
                break;
            case R.id.change:

                eet.setVisibility(View.VISIBLE);
                voice.setVisibility(View.VISIBLE);
                voicesend.setVisibility(View.GONE);
                msgsend.setVisibility(View.GONE);
                change.setVisibility(View.GONE);
                break;
        }

    }



    /**
    注：为了使SDK能够区分当前应用是否退出，开发者需进行以下几个步骤：

            1、在会话和聊天的Activity类实现'ObseverListener'监听器；

            2、在onResume方法中调用BmobNotificationManager.getInstance(context).addObserver(this)方法添加观察者；
            在onPause方法中调用BmobNotificationManager.getInstance(context).removeObserver(this)方法移除观察者

    3、在主Activity的onDestroy方法中调用BmobNotificationManager.getInstance(context).clearObserver()清空观察者。
    */
    @Override
    protected void onResume() {
        MediaManager.resume();
        //锁屏期间的收到的未读消息需要添加到聊天界面中
        addUnReadMessage();
        //添加通知监听
        BmobNotificationManager.getInstance(this).addObserver(this);
        // 有可能锁屏期间，在聊天界面出现通知栏，这时候需要清除通知
        BmobNotificationManager.getInstance(this).cancelNotification();
        super.onResume();
    }

    /**
     * 添加未读的通知栏消息到聊天界面
     */
    private void addUnReadMessage(){
        List<MessageEvent> cache = BmobNotificationManager.getInstance(this).getNotificationCacheList();
        if(cache.size()>0){
            int size =cache.size();
            for(int i=0;i<size;i++){
                MessageEvent event = cache.get(i);
                addMessage2Chat(event);
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
        BmobNotificationManager.getInstance(this).removeObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //更新此会话的所有消息为已读状态
        c.updateLocalCache();
        MediaManager.release();
    }
}
