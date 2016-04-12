package com.autism.chat.bean;

import cn.bmob.newim.bean.BmobIMExtraMessage;

/**同意添加好友请求
 * @author smile
 * @project AgreeAddFriendMessage
 * @date 2016-03-04-10:41
 * 此为示例，教大家怎样自定义消息类型
 */
public class AgreeAddFriendMessage extends BmobIMExtraMessage {

    @Override
    public String getMsgType() {
        return "agree";
    }

    @Override
    public boolean isTransient() {
        //设置为true,表明为暂态消息，那么这条消息并不会保存到本地db中，SDK只负责发送出去
        //设置为false,则会保存到指定会话的数据库中
        return true;
    }

    public AgreeAddFriendMessage(){}
}
