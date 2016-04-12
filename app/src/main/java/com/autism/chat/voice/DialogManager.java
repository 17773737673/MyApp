package com.autism.chat.voice;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.autism.chat.R;


public class DialogManager {
	private Dialog dialog;

	private ImageView mIcon;
	private ImageView mVoice;

	private TextView mTable;

	private Context mContext;

	public DialogManager(Context context) {
		mContext = context;
	}

	/**
	 * 显示dialog是调用
	 */
	public void showDialogRecording(){
		dialog = new Dialog(mContext, R.style.AudioDialog);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_recorder, null);
		dialog.setContentView(view);
		dialog.setContentView(view);

		//dialog.setContentView(R.layout.dialog_recorder);

		mIcon = (ImageView) dialog.findViewById(R.id.iv_dialog_recording);
		mVoice = (ImageView) dialog.findViewById(R.id.iv_dialog_voice);
		mTable = (TextView) dialog.findViewById(R.id.tv_lable);

		dialog.show();

	}

	/**
	 * 正在录音时回掉
	 */
	public void recording(){
		if(dialog!=null&&dialog.isShowing()){
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.VISIBLE);
			mTable.setVisibility(View.VISIBLE);

			mIcon.setImageResource(R.drawable.recorder);
			mTable.setText("手指上滑，取消发送");

		}
	}

	/**
	 * 放弃录音时回掉
	 */
	public void wantToCancel(){
		if(dialog!=null&&dialog.isShowing()){
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.GONE);
			mTable.setVisibility(View.VISIBLE);

			mIcon.setImageResource(R.drawable.cancel);
			mTable.setText("松开手指，取消发送");

		}
	}

	/**
	 * 时长果断时回掉
	 */
	public void tooShort(){
		if(dialog!=null&&dialog.isShowing()){
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.GONE);
			mTable.setVisibility(View.VISIBLE);

			mIcon.setImageResource(R.drawable.voice_to_short);
			mTable.setText("录音时长过短！");

		}
	}

	/**
	 * 关闭dialog
	 */
	public void dimissDialog(){
		if(dialog!=null&&dialog.isShowing()){
			dialog.dismiss();
			dialog=null;

		}
	}
	/**
	 * 通过level去更新voice上的图片
	 * @param level
	 */
	public void upDataVoiceLevel(int level){
		if(dialog!=null&&dialog.isShowing()){
//			mIcon.setVisibility(View.VISIBLE);
//			mVoice.setVisibility(View.VISIBLE);
//			mTable.setVisibility(View.VISIBLE);
			//通过方法名找到资源
			int resId = mContext.getResources().getIdentifier("v"+level, "drawable", mContext.getPackageName());
			mVoice.setImageResource(resId);
		}
	}
}