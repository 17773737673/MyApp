package com.autism.chat.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.autism.chat.R;
import com.autism.chat.base.BaseListAdapter;
import com.autism.chat.base.ViewHolder;
import com.autism.chat.bean.User;
import com.autism.chat.utils.ImageLoadOptions;
import com.autism.chat.utils.TimeUtil;
import com.autism.chat.utils.ViewUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;


/**
 * Created by AutismPerson on 4/5 0005.
 */
public class ChatAdapter<E> extends BaseListAdapter {


    public ChatAdapter(Context context, List<E> list) {
        super(context, list);
    }

    /**
     * 刷新
     *
     * @param list
     */
    public void upData(List<BmobIMConversation> list) {
        lists.clear();
        if (null != list) {
            lists.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void upData(BmobIMConversation list) {
        lists.clear();
        lists.addAll(Arrays.asList(list));
        notifyDataSetChanged();
    }

    /**
     * 移除会话
     *
     * @param position
     */
    public void remove(int position) {
        lists.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.item_list_chat, null);
        ImageView tox = ViewHolder.get(convertView, R.id.iv_toux);
        TextView name = ViewHolder.get(convertView, R.id.tv_name);
        TextView msg = ViewHolder.get(convertView, R.id.msg);
        TextView time = ViewHolder.get(convertView, R.id.time);
        TextView tv_unread = ViewHolder.get(convertView, R.id.unread);

        BmobIMConversation conversation = (BmobIMConversation) lists.get(position);
        List<BmobIMMessage> messages = conversation.getMessages();


        if (messages != null && messages.size() > 0) {
            BmobIMMessage lastMsg = messages.get(0);//获取最后一条数据
            String content = lastMsg.getContent();//最后一条数据的内容
            if (lastMsg.getMsgType().equals(BmobIMMessageType.TEXT.getType())) {
                msg.setText(content);
            } else if (lastMsg.getMsgType().equals(BmobIMMessageType.VOICE.getType())) {
                msg.setText("[语音]");                //如果发来的是语音的话就
            }
            time.setText(TimeUtil.getChatTime(Long.parseLong(String.valueOf(lastMsg.getCreateTime()))));
        }

//        messages.get(position).getBmobIMUserInfo().getAvatar()
        //会话图标
        ViewUtil.setAvatar(conversation.getConversationIcon(), R.drawable.default_icon_user, tox);

        //会话标题
        name.setText(conversation.getConversationTitle());

        //未读消息数
        long unread = BmobIM.getInstance().getUnReadCount(conversation.getConversationId());
        if (unread > 0) {
            tv_unread.setVisibility(View.VISIBLE);
            tv_unread.setText(String.valueOf(unread));
        } else {
            tv_unread.setVisibility(View.GONE);
        }
        return convertView;
    }
}
