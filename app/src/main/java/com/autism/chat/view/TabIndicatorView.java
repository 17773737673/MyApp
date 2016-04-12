package com.autism.chat.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.autism.chat.R;


public class TabIndicatorView extends RelativeLayout {

	private ImageView ivTabIcon;
	private TextView tvTabHint;
	private TextView tvTabUnRead;

	private int normalIconId;
	private int focusIconId;

	public TabIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TabIndicatorView(Context context) {
		this(context, null);
		View view = View.inflate(context, R.layout.tab_indicator, this);
		ivTabIcon = (ImageView) findViewById(R.id.tab_indicator_icon);
		tvTabHint = (TextView) findViewById(R.id.tab_indicator_hint);
		tvTabUnRead = (TextView) findViewById(R.id.tab_indicator_unread);
	}

	public void setTabText(String text) {
		tvTabHint.setText(text);
	}


	public void setTabIcon(int normalIconId, int focusIconId) {
		this.normalIconId = normalIconId;
		this.focusIconId = focusIconId;
		ivTabIcon.setImageResource(normalIconId);
	}

	public void setTabUnRead(int unReadCount) {

		if (unReadCount <= 0) {
			tvTabUnRead.setVisibility(View.GONE);
		} else {
			if (unReadCount <= 99) {
				tvTabUnRead.setText(unReadCount + "");
			} else {
				tvTabUnRead.setText("99+");
			}
			tvTabUnRead.setVisibility(View.VISIBLE);
		}
	}
	
	public void setSelected(boolean selected){
		if(selected){
			ivTabIcon.setImageResource(focusIconId);
		}else{
			ivTabIcon.setImageResource(normalIconId);
		}
	}
}
