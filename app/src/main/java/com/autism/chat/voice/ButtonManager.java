package com.autism.chat.voice;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.autism.chat.R;
import com.autism.chat.voice.AtudioRecorderManager;
import com.autism.chat.voice.DialogManager;


public class ButtonManager extends Button implements AtudioRecorderManager.RecorderState {

	// 状态码
	private static final int WELLPREPARE = 0x11111;// 录音准备完毕
	private static final int DIALOGXY = 50;// y高
	private static final int NORMAL = 0x1311;// 关闭带画框
	private static final int WANTTOGO = 0x1312;
	private static final int RECORDING = 0x1313;
	protected static final int MSG_VOICE_LEVEL = 0x1314;
	protected static final int VOICE_NUMBER = 7;
	private static final int MSG_DIALOG_DISMISS = 0x1315;
	private static final int VOICE = 0x1316;

	// 标签
	private boolean isRecoder = false; // 准备状态
	private int curState; // 当前状态
	private boolean mReady = false; // 长按监听状态

	// 时间
	private float mTime = 0;
	// 管理器
	private AtudioRecorderManager arm;
	private DialogManager dm;

	public ButtonManager(Context context) {
		this(context, null);
	}


	//当按钮实例化时，获取录音管理器实例，获取，dialog实例，录音进入准备状态
	//长按时，启动录音
	public ButtonManager(Context context, AttributeSet attrs) {
		super(context, attrs);

		// 单例获取
		arm = AtudioRecorderManager.getInstance();

		// 录音状态接口获取
		arm.RsListener(this);

		// 浮窗
		dm =new DialogManager(context);

		// 长点击
		setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				mReady = true;
				arm.startRecorder();

				return false;
			}
		});
	}

	//录音完成后获取当前的录音时长，和存储路劲，回掉
	public interface AudioFinishRecorderListener{
		void onFinish(float seconds,String filePath);//时长和文件路径
	}
	private AudioFinishRecorderListener mlistener;

	public  void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
		mlistener = listener;
	}
	// //线程
	Runnable mVoiceLevel = new Runnable() {

		@Override
		public void run() {
			while (isRecoder) {
				try {
					Thread.sleep(100);// 每0.1秒
					mTime += 0.1f;
					handler.sendEmptyMessage(MSG_VOICE_LEVEL);// 发送消息通知刷新ui
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	// 消息处理器
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case WELLPREPARE:
					dm.showDialogRecording();
					isRecoder = true;
					//	buttonState(RECORDING);
					curState=RECORDING;
					setText(R.string.str_recording);
					new Thread(mVoiceLevel).start();// 线程刷新声音大小提示
					break;
				case MSG_VOICE_LEVEL:
					dm.upDataVoiceLevel(arm.voiceLevel(VOICE_NUMBER));
					break;
				case MSG_DIALOG_DISMISS:
					dm.dimissDialog();
					break;
			}

		}
	};

	// 接口实现发送消息
	@Override
	public void wellPrepare() {
		handler.sendEmptyMessage(WELLPREPARE);
	}

	// 触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		int action = event.getAction();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
				// TODO

				break;
			case MotionEvent.ACTION_MOVE:
				if (isRecoder) { // 只有当录音进入准备，开始录音是，才处理当前移动事件
					if (wantToCancel(x, y)) {
						buttonState(WANTTOGO);
						// handler.sendEmptyMessage(VOICE);

					} else {
						buttonState(RECORDING);
					}
				}
				break;

			case MotionEvent.ACTION_UP:
//			if (!isRecoder) {
//				// dm.dimissDialog();
//				dm.tooShort();
//				handler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 1500);
//			}
				if (!mReady) { // 当长按监听没准备好时
					reset(); // 初始化数据
					return super.onTouchEvent(event);// 返回继续触摸
				}

				if(!isRecoder||mTime < 1f){
					dm.tooShort();
					arm.cancel();
					handler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 1000);
					reset();
				}
				if (curState == RECORDING) {

					// TODO 正常结束此处要发送消息结束，并把语音显示在listView
					dm.dimissDialog();
					//正常结束后，回掉显示数据
					if(mlistener!=null){
						mlistener.onFinish(mTime, AtudioRecorderManager.getCurrentFile());
					}
					arm.release();

				} else if (curState == WANTTOGO) {// 当前状态为离开状态结束时
					dm.dimissDialog();
					arm.cancel(); // 关闭录音并删除当前录的文件
				}
				reset();
				break;
		}

		return super.onTouchEvent(event);
	}

	private boolean wantToCancel(int x, int y) {
		if (x < 0 || x > getWidth()) {
			return true;
		}
		/*
		 * 判断用户的y坐标是否超出按钮范围 当y坐标大于屏幕y-按钮高时，
		 */
		if (y < -DIALOGXY) {
			return true;
		}
		return false;
	}

	// 重置
	private void reset() {
		isRecoder = false;
		buttonState(NORMAL);
		mTime = 0;
		mReady = false;

	}

	/*
	 * 按钮状态码判断
	 */
	private void buttonState(int state) {
		// 给当前状态赋值
		if (curState != state) {
			curState = state;
			switch (state) {
				case RECORDING:
					setText(R.string.str_recording);
					dm.recording();
					break;
				case WANTTOGO:
					setText(R.string.str_want_cancel);
					dm.wantToCancel();
					break;
				case NORMAL:
					setText(R.string.str_normal);
					break;

			}
		}
	}
}
