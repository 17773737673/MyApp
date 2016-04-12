package com.autism.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.autism.chat.ChatApplication;
import com.autism.chat.R;
import com.autism.chat.base.BaseListAdapter;
import com.autism.chat.bean.User;
import com.autism.chat.receiver.AddFriendEvent;
import com.autism.chat.utils.ViewUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConversationListener;
import cn.bmob.v3.exception.BmobException;


/**
 * Created by AutismPerson on 4/5 0005.
 */
public class FindFriendAdapter<E> extends BaseListAdapter {


    public FindFriendAdapter(Context context, List<E> list) {
        super(context, list);


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.item_list_find, null);
        ImageView tox = (ImageView) convertView.findViewById(R.id.iv_toux);
        TextView name = (TextView) convertView.findViewById(R.id.tv_name);
        Button add = (Button) convertView.findViewById(R.id.add);

        final User friend = (User) lists.get(position);

        final String toux = friend.getAvatar();
        final String name2 = friend.getUsername();
        name.setText(name2);
        //如果设置了头像就下载
        //如果设置了头像就下载
        ViewUtil.setAvatar(toux, R.drawable.default_icon_user, tox);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //对象传递，跳转界面
                BmobIMUserInfo info = new BmobIMUserInfo(friend.getObjectId(), friend.getUsername(), friend.getAvatar());
                //私聊
                BmobIM.getInstance().startPrivateConversation(info, new ConversationListener() {
                    @Override
                    public void done(BmobIMConversation c, BmobException e) {
                        if (e == null) {
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("a", c);
                            if (bundle != null) {

                                intent.putExtra(mContext.getPackageName(), bundle);
                                EventBus.getDefault().post(new AddFriendEvent(intent));
                            }
                        } else {
                            Toast.makeText(ChatApplication.getInstance(), e.getMessage() + "(" + e.getErrorCode() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
        return convertView;
    }
}
