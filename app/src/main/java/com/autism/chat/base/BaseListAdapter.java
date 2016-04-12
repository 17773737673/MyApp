package com.autism.chat.base;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.List;

/**
 * Created by AutismPerson on 4/6 0006.
 */
public abstract class BaseListAdapter<T> extends BaseAdapter {

    public List<T> lists;
    public Context mContext;
    public LayoutInflater inflater;

    public BaseListAdapter(Context context, List<T> list) {
        super();
        this.lists = list;
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void addAll(List<T> list) {
        this.lists.addAll(list);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        lists.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void toast(final String msg) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
