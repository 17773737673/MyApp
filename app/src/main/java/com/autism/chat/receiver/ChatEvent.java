package com.autism.chat.receiver;

import cn.bmob.newim.bean.BmobIMUserInfo;

/**
 * Created by AutismPerson on 4/7 0007.
 */
public class ChatEvent {

    public BmobIMUserInfo info;

    public ChatEvent(BmobIMUserInfo info){
        this.info=info;
    }
}
