package com.autism.chat.voice;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.autism.chat.ChatApplication;
import com.autism.chat.utils.DirUtil;

public class AtudioRecorderManager {

	// 录音
	MediaRecorder mRecorder;

	// 单例
	private static AtudioRecorderManager arm;

	private AtudioRecorderManager() {
	};

	public static AtudioRecorderManager getInstance() {
		if (arm == null) {
			synchronized (AtudioRecorderManager.class) {
				arm = new AtudioRecorderManager();
			}
		}
		return arm;
	}

	// 录音状态接口
	public interface RecorderState {
		void wellPrepare();
	}

	private RecorderState mRs;

	public void RsListener(RecorderState listener) {
		mRs = listener;
	}

	// 写入文件夹路径
	String file = DirUtil.getDir(ChatApplication.getInstance(),"voice");
	// 当前文件
	static String mCurFile;
	boolean isPrepare; // 记录录音是否开启，用于获取声音大小

	// 准备录音
	public void startRecorder() {

		File files = new File(file);
		if (!files.exists()) {
			files.mkdir();
		}
		String newFile = files + "/" + fileName();//拼接文件路径
		mCurFile = newFile;						//记录当前文件
		isPrepare = false;						//准备开关
		if (mRecorder == null) {
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 设置音频源来于麦克风
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);// 设置音频的格式
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 设置音频的编码
			mRecorder.setOutputFile(newFile);// 设置输出文件绝对路径
			mRecorder.setAudioChannels(1);
			mRecorder.setAudioEncodingBitRate(12200);
		}else{
			mRecorder.stop();
			mRecorder.reset();
		}
		try {
			mRecorder.prepare();		//进入准备状态
		} catch (IOException e) {
			Log.e("ERROR", "prepare error");
		}

		mRecorder.start();				//开启
		isPrepare = true;				//准备完毕
		// 接口发送消息
		if (mRs != null) {
			mRs.wellPrepare();			//调用接口
		}
	}

	// 获取声音分贝值,防止内存溢出
	public int voiceLevel(int level) {
		if (isPrepare) {
			if(mRecorder!=null){
				return level * mRecorder.getMaxAmplitude() / 32768 +1;
			}
		}
		return 1;
	}

	// 随机文件名
	private String fileName() {
		return UUID.randomUUID().toString() + ".amr";
	}

	// 停止并释放录音
	public void release() {
		if (mRecorder != null) {
			mRecorder.stop(); // 停止
			mRecorder.release();// 释放资源
			mRecorder = null;
		}
	}

	// 放弃时调用删除当前录音文件
	public void cancel() {
		release();
		if (mCurFile != null) {
			File file = new File(mCurFile);
			file.delete();
			mCurFile = null;
		}
	}

	public static String getCurrentFile() {
		return mCurFile;
	}
}
