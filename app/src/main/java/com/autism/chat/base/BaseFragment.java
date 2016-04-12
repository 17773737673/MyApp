package com.autism.chat.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.autism.chat.ChatApplication;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by AutismPerson on 4/5 0005.
 */
public class BaseFragment extends Fragment {


    public Activity mActivity;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = getActivity();
    }


    public void toast(final String msg){
        if(mActivity==null) return;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ChatApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * 跳转页面
     *
     * @param target
     * @param bundle
     * @param finish
     */
    public void start(Class<? extends Activity> target, Bundle bundle, boolean finish) {

        if (getActivity() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), target);
        if (bundle != null) {
            intent.putExtra(getActivity().getPackageName(), bundle);
        }
        startActivity(intent);
        if (finish)
            getActivity().finish();
    }
}
