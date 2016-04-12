package com.autism.chat.ui;

import android.content.Intent;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;

import com.autism.chat.R;
import com.autism.chat.base.BaseActivity;
import com.autism.chat.bean.User;
import com.autism.chat.bean.UserModel;
import com.autism.chat.fragment.ChatFragment;
import com.autism.chat.fragment.ContactFragment;
import com.autism.chat.fragment.FaXianFragemnt;
import com.autism.chat.fragment.MeFragment;
import com.autism.chat.utils.IMMLeaks;
import com.autism.chat.view.TabIndicatorView;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.listener.ObseverListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.exception.BmobException;


public class Main2Activity extends BaseActivity implements TabHost.OnTabChangeListener, ObseverListener {
    private static final String TAB_CHAT = "tabulated";
    private static final String TAB_CONTACT = "contact";
    private static final String ME = "me";
    private static final String TAB_QUAN = "penguin";

    private Toolbar toolbar;
    private ListView list;
    public FragmentTabHost tabhost;
    private Intent mIntent;
    public TabIndicatorView tabIndicator1;
    private TabIndicatorView tabIndicator2;
    private TabIndicatorView tabIndicator3;
    private TabIndicatorView tabIndicator4;
    private TabHost.TabSpec tabbed1;
    private TabHost.TabSpec tabbed2;
    private TabHost.TabSpec tabbed3;
    private TabHost.TabSpec tabbed4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();

        //连接服务器
        User user = UserModel.getInstance().getCurrentUser();
        BmobIM.connect(user.getObjectId(), new ConnectListener() {
            @Override
            public void done(String uid, BmobException e) {
                if (e == null) {
                    Logger.i("connect success");
                } else {
                    Logger.e(e.getErrorCode() + "/" + e.getMessage());
                }
            }
        });
        //监听连接状态，也可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus status) {
            }
        });

        IMMLeaks.fixFocusedViewLeak(getApplication());

    }


    private void initView() {
        tabhost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabhost.setup(this, getSupportFragmentManager(), R.id.fl_tab_content);
        tabbed1 = tabhost.newTabSpec(TAB_CHAT);
        tabbed2 = tabhost.newTabSpec(TAB_CONTACT);
        tabbed3 = tabhost.newTabSpec(TAB_QUAN);
        tabbed4 = tabhost.newTabSpec(ME);

        tabIndicator1 = new TabIndicatorView(this);
        tabIndicator2 = new TabIndicatorView(this);
        tabIndicator3 = new TabIndicatorView(this);
        tabIndicator4 = new TabIndicatorView(this);

        tabIndicator1.setTabText("信息");
        tabIndicator1.setTabIcon(R.drawable.tab_icon_chat_normal, R.drawable.tab_icon_chat_focus);

        tabIndicator2.setTabText("联系人");
        tabIndicator2.setTabIcon(R.drawable.tab_icon_contact_normal, R.drawable.tab_icon_contact_focus);

        tabIndicator3.setTabText("发现");
        tabIndicator3.setTabIcon(R.drawable.tab_icon_discover_normal, R.drawable.tab_icon_discover_focus);

        tabIndicator4.setTabText("我");
        tabIndicator4.setTabIcon(R.drawable.tab_icon_me_normal, R.drawable.tab_icon_me_focus);

        tabhost.addTab(tabbed1.setIndicator(tabIndicator1), ChatFragment.class, null);
        tabhost.addTab(tabbed2.setIndicator(tabIndicator2), ContactFragment.class, null);
        tabhost.addTab(tabbed3.setIndicator(tabIndicator3), FaXianFragemnt.class, null);
        tabhost.addTab(tabbed4.setIndicator(tabIndicator4), MeFragment.class, null);

        tabhost.getTabWidget().setDividerDrawable(android.R.color.transparent);
        tabhost.setOnTabChangedListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        SearchView actionView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        actionView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                toast("Search...");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //toast("sss");
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            start(FindFriendActivity.class, null, false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabChanged(String tabId) {

        switch (tabId) {
            case TAB_CHAT:

                tabIndicator1.setTabUnRead(-1);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //添加观察者-用于是否显示通知消息
        BmobNotificationManager.getInstance(this).addObserver(this);
        //进入应用后，通知栏应取消
        BmobNotificationManager.getInstance(this).cancelNotification();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //移除观察者
        BmobNotificationManager.getInstance(this).removeObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清理导致内存泄露的资源
        BmobIM.getInstance().clear();
        //完全退出应用时需调用clearObserver来清除观察者
        BmobNotificationManager.getInstance(this).clearObserver();
    }

    @Subscribe
    public void onEventMainThread(MessageEvent event) {
    }
}
