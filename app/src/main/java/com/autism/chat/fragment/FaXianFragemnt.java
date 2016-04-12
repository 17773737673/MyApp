package com.autism.chat.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.autism.chat.R;
import com.autism.chat.base.BaseFragment;

import org.greenrobot.eventbus.Subscribe;

import cn.bmob.newim.event.MessageEvent;

/**
 * Created by AutismPerson on 4/5 0005.
 */
public class FaXianFragemnt extends BaseFragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_faxian,null);


        return view;

    }
    @Subscribe
    public void OnEventMainThread(MessageEvent event){
        toast("刷新");
    }
}
