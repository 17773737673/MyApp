package com.autism.chat.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.autism.chat.R;
import com.autism.chat.base.BaseListAdapter;
import com.autism.chat.bean.User;
import com.autism.chat.receiver.AddFriendEvent;
import com.autism.chat.utils.ImageLoadOptions;
import com.autism.chat.utils.ViewUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;


/**
 * Created by AutismPerson on 4/5 0005.
 */
public class ContactAdapter<E> extends BaseListAdapter {


    public ContactAdapter(Context context, List<E> list) {
        super(context, list);
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.item_list_contact, null);
        ImageView tox = (ImageView) convertView.findViewById(R.id.iv_toux);
        TextView name = (TextView) convertView.findViewById(R.id.tv_name);

        final User friend = (User) lists.get(position);

        final String toux = friend.getAvatar();
        final String name2 = friend.getUsername();
        name.setText(name2);
        //如果设置了头像就下载
        ViewUtil.setAvatar(toux, R.drawable.default_icon_user, tox);
        if (!TextUtils.isEmpty(toux)) {
            ImageLoader.getInstance().displayImage(toux, tox, ImageLoadOptions.getOptions());
        } else {
            tox.setBackgroundResource(R.drawable.default_icon_user);
        }
        return convertView;
    }
}
