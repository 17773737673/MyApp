package com.autism.chat.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.autism.chat.R;
import com.autism.chat.base.BaseActivity;
import com.autism.chat.bean.User;
import com.autism.chat.bean.UserModel;

import org.greenrobot.eventbus.Subscribe;

import cn.bmob.newim.event.MessageEvent;


public class SplashActivity extends BaseActivity{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        Log.e("Chat","闪屏页启动类");
        getSupportActionBar().hide();

        FrameLayout frame = (FrameLayout) findViewById(R.id.ll_splash);


        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f,1.0f);
        alphaAnimation.setDuration(1200);

        frame.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Handler handler =new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        User currentUser = UserModel.getInstance().getCurrentUser();
                        if (currentUser == null) {
                            start(LoginActivity.class, null, true);
                        } else {
                            start(Main2Activity.class, null, true);
                        }
                    }
                }, 200);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    @Subscribe
    public void onEventMainThread(MessageEvent event) {
    }
}
