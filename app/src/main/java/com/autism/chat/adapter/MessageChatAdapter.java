package com.autism.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.autism.chat.ChatApplication;
import com.autism.chat.R;
import com.autism.chat.base.ViewHolder;
import com.autism.chat.bean.User;
import com.autism.chat.utils.DirUtil;
import com.autism.chat.utils.TimeUtil;
import com.autism.chat.utils.ViewUtil;
import com.autism.chat.voice.MediaManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import cn.bmob.newim.bean.BmobIMAudioMessage;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.core.BmobDownloadManager;
import cn.bmob.v3.BmobUser;


/**
 * Created by AutismPerson on 4/6 0006.
 */

public class MessageChatAdapter extends BaseAdapter {
    private static final int TYPE_RECEIVER_VOICE = 2;
    private static final int TYPE_SEND_VOICE = 3;
    private final int TYPE_RECEIVER_TXT = 0;
    private final int TYPE_SEND_TXT = 1;


    private BmobIMConversation c;
    //消息集合
    private List<BmobIMMessage> msgs = new ArrayList<>();
    private String objectId = "";
    private LayoutInflater inflater;
    Context context;
    private final int mMaxItemWidth;
    private final int mMinItemWidth;
    private View view;
    private View view2;

    /**
     * @param context
     * @param c       当前对象会话
     */
    public MessageChatAdapter(Context context, BmobIMConversation c) {
        this.context = context;
        //获取当前用户唯一id值
        objectId = BmobUser.getCurrentUser(context).getObjectId();
        this.c = c;
        inflater = LayoutInflater.from(context);
        /**
         * 获取系统窗口服务
         */
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mMaxItemWidth = (int) (outMetrics.widthPixels * 0.7f);
        mMinItemWidth = (int) (outMetrics.widthPixels * 0.15f);

    }

    //查找信息所在item
    public int findPosition(BmobIMMessage message) {
        int index = this.getCount();
        int position = -1;
        while (index-- > 0) {
            if (message.equals(this.getItem(index))) {
                position = index;
                break;
            }
        }
        return position;
    }

    public void addMessages(List<BmobIMMessage> messages) {
        msgs.addAll(0, messages);
        notifyDataSetChanged();
    }

    public void addAllMessage(BmobIMMessage msg) {
        msgs.addAll(Arrays.asList(msg));
        notifyDataSetChanged();
    }


    public BmobIMMessage getFirstMessage() {
        if (null != msgs && msgs.size() > 0) {
            return msgs.get(0);
        } else {
            return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        BmobIMMessage bmobIMMessage = msgs.get(position);
        //当用信息类型是文本类型时
        if (bmobIMMessage.getMsgType().equals(BmobIMMessageType.TEXT.getType())) {
            //当当前id不是自己时那就是别人
            return bmobIMMessage.getFromId().equals(objectId) ? TYPE_SEND_TXT : TYPE_RECEIVER_TXT;
        } else if (bmobIMMessage.getMsgType().equals(BmobIMMessageType.VOICE.getType())) {
            return bmobIMMessage.getFromId().equals(objectId) ? TYPE_SEND_VOICE : TYPE_RECEIVER_VOICE;
        }
        return -1;
    }

    @Override
    public int getCount() {
        return this.msgs == null ? 0 : this.msgs.size();
    }

    @Override
    public Object getItem(int position) {
        return this.msgs == null ? null : (position >= this.msgs.size() ? null : this.msgs.get(position));

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_chat_received_message, null);

        //右边消息框
        ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
        TextView tv_message = ViewHolder.get(convertView, R.id.tv_message);

        //左边消息框
        ImageView iv_avatar1 = ViewHolder.get(convertView, R.id.iv_avatar1);
        TextView tv_message1 = ViewHolder.get(convertView, R.id.tv_message1);
        TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);

        //右边语音框
        ImageView iv_voice = ViewHolder.get(convertView, R.id.iv_icon);
        FrameLayout fl = ViewHolder.get(convertView, R.id.fy_recorder_length);//.9语音框

        final TextView voice_time = ViewHolder.get(convertView, R.id.tv_time_audio);

        //左边语音框
        ImageView iv_voice2 = ViewHolder.get(convertView, R.id.iv_icon2);
        FrameLayout fl2 = ViewHolder.get(convertView, R.id.fy_recorder_length2);//.9语音框
        final TextView voice_time2 = ViewHolder.get(convertView, R.id.tv_time_audio2);

        //消息框Layout
        LinearLayout msglayout = ViewHolder.get(convertView, R.id.msg_layout);
        RelativeLayout leftmsglayout = ViewHolder.get(convertView, R.id.left_msg_layout);
        RelativeLayout rightmsglayout = ViewHolder.get(convertView, R.id.right_msg_layout);

        //语音框Layout
        LinearLayout voicelayout = ViewHolder.get(convertView, R.id.voice_layout);
        RelativeLayout leftvoicelayout = ViewHolder.get(convertView, R.id.left_voice_layout);
        RelativeLayout rightvoicelayout = ViewHolder.get(convertView, R.id.right_voice_layout);

        final BmobIMMessage item = msgs.get(position);

        int itemViewType = getItemViewType(position);
        if (itemViewType == TYPE_SEND_TXT) {
            leftmsglayout.setVisibility(View.GONE);
            rightmsglayout.setVisibility(View.VISIBLE);
            voicelayout.setVisibility(View.GONE);

            tv_message.setText(item.getContent());
            ViewUtil.setAvatar(BmobUser.getCurrentUser(context, User.class).getAvatar(), R.drawable.default_icon_user, iv_avatar);
        } else if (itemViewType == TYPE_RECEIVER_TXT) {
            leftmsglayout.setVisibility(View.VISIBLE);
            rightmsglayout.setVisibility(View.GONE);
            voicelayout.setVisibility(View.GONE);

            tv_message1.setText(item.getContent());

            ViewUtil.setAvatar(c.getConversationIcon(), R.drawable.default_icon_user, iv_avatar1);
            tv_time.setText(TimeUtil.getChatTime(Long.parseLong(String.valueOf(item.getCreateTime()))));
        } else if (itemViewType == TYPE_SEND_VOICE) {
            final BmobIMAudioMessage message = BmobIMAudioMessage.buildFromDB(true, item);


            final ViewGroup.LayoutParams layoutParams = fl.getLayoutParams();
            layoutParams.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * message.getDuration()));// /这。。。。。
            msglayout.setVisibility(View.GONE);
            leftvoicelayout.setVisibility(View.GONE);
            rightvoicelayout.setVisibility(View.VISIBLE);
            ViewUtil.setAvatar(BmobUser.getCurrentUser(context, User.class).getAvatar(), R.drawable.default_icon_user, iv_voice);
            voice_time.setText(message.getDuration() + "\''");
            //语音框参数
            final View finalConvertView = convertView;

            fl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 判断，当用户点击向另一个语音时，重置图片
                    if (view != null) {
                        view.setBackgroundResource(R.drawable.adj);
                        view = null;
                    }
                    //播放动画
                    view = ViewHolder.get(finalConvertView, R.id.v_view);
                    view.setBackgroundResource(R.drawable.play_anim);
                    AnimationDrawable anim = (AnimationDrawable) view.getBackground();
                    anim.start();
                    //播放当前文件地址
                    String localPath = message.getContent().split("&")[0];
                    Log.e("Chat", "发送方地址为" + localPath);
                    play(localPath,view);
                }
            });
        } else if (itemViewType == TYPE_RECEIVER_VOICE) {
            final BmobIMAudioMessage message = BmobIMAudioMessage.buildFromDB(true, item);
            msglayout.setVisibility(View.GONE);
            rightvoicelayout.setVisibility(View.GONE);
            leftvoicelayout.setVisibility(View.VISIBLE);
            ViewUtil.setAvatar(c.getConversationIcon(), R.drawable.default_icon_user, iv_voice2);
            voice_time2.setText(message.getDuration() + "\''");
            final ViewGroup.LayoutParams layoutParams = fl2.getLayoutParams();
            layoutParams.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * message.getDuration()));

            final View finalConvertView = convertView;
            final ProgressBar bar = ViewHolder.get(finalConvertView, R.id.progressBar);
            fl2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String localPath = BmobDownloadManager.getDownLoadFilePath(message);
                    Log.e("Chat", "当前下载localPath-" + localPath);

                    if (view2 != null) {
                        view2.setBackgroundResource(R.drawable.adj);
                        view2 = null;
                    }
                    //播放动画
                    view2 = ViewHolder.get(finalConvertView, R.id.v_view2);
                    view2.setBackgroundResource(R.drawable.play_anim);
                    AnimationDrawable anim = (AnimationDrawable) view2.getBackground();
                    anim.start();

                    //由于Bmob下载的io流0x800000警告，导致音频文件不能正常播放，且偶尔下载失败，所以这里自己手动下载
                    load(message.getContent(), localPath,view2,bar);
                    //当不为0时才播放
                    if (new File(localPath).length() > 0) {
                        play(localPath,view2);
                    }
//
                }
            });

        }
        return convertView;
    }

    public void load(String path, String target, final View view, final ProgressBar bar) {

        if (new File(target).length() <= 0) {
            bar.setVisibility(View.VISIBLE);
            HttpUtils utils = new HttpUtils();
            utils.download(path, target, true, true, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    bar.setVisibility(View.GONE);
                    //希望能在下载完第一时间就播放，，虽然有点冗余，但是还是得这么做
                    play(responseInfo.result.getAbsolutePath(),view);
                    Log.e("Chat", "下载成功");
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // 写入文件夹路径
    public void play(String path, final View view) {
        try {
            MediaManager.playSound(path, context, new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer arg0) {
                    Log.e("Chat", "到这里来类吗");
                    //播放完毕后关闭语音动画
                   view.setBackgroundResource(R.drawable.adj);
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
