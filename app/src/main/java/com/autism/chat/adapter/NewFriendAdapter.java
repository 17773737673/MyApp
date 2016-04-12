package com.autism.chat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.autism.chat.base.BaseListAdapter;
import com.autism.chat.bean.User;

import java.util.List;

/**
 * Created by AutismPerson on 4/6 0006.
 */
public class NewFriendAdapter  extends BaseListAdapter{


    public NewFriendAdapter(Context context, List<User> invitations) {
        super(context,invitations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        convertView = inflater.inflate(R.layout.item_add_friend, null);
//
//        //会话对象
//        final BmobInvitation msg = (BmobInvitation) lists.get(position);
//
//        TextView name = (TextView) convertView.findViewById(R.id.name);
//        ImageView iv = (ImageView)convertView.findViewById(R.id.avatar);
//        final Button btn_add  = (Button)convertView.findViewById(R.id.btn_add);
//
//        String avatar = msg.getAvatar();
//        if (avatar != null && !avatar.equals("")) {
//            ImageLoader.getInstance().displayImage(avatar, iv, ImageLoadOptions.getOptions());
//        } else {
//            iv.setImageResource(R.drawable.default_icon_user);
//        }
//
//
//        int status = msg.getStatus();
//
//        //没同意
//        if(status== BmobConfig.STATUS_ADD_NO_VALIDATION){
//            btn_add.setText("同意");
//            btn_add.setTextColor(mContext.getResources().getColor(R.color.base_color_text_white));
//            btn_add.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View arg0) {
//                    // TODO Auto-generated method stub
//                    BmobLog.i("点击同意按钮:" + msg.getFromid());
//                    agressAdd(btn_add, msg);
//                }
//            });
//         //同意
//        }else if(status==BmobConfig.STATUS_ADD_AGREE){
//            btn_add.setText("已同意");
//            btn_add.setBackgroundDrawable(null);
//            btn_add.setTextColor(mContext.getResources().getColor(R.color.base_color_text_black));
//            btn_add.setEnabled(false);
//        }
//        name.setText(msg.getFromname());
        return convertView;
    }

}
