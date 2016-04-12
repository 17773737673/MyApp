package com.autism.chat.voice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.util.Log;

public class MediaManager {

    static MediaPlayer mPlayer;
    private static boolean isPause;

    public static void playSound(String filePath, Context context,
                                 OnCompletionListener onCompletionListener) throws FileNotFoundException {

        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            // 出现错误时的处理
            mPlayer.setOnErrorListener(new OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                    mPlayer.reset();
                    return false;
                }
            });
        } else {
            mPlayer.reset();
        }

        try {

            FileInputStream fis = new FileInputStream(new File(filePath));
            Log.e("Chat", "fis.getFD()" + fis.getFD().toString());
            mPlayer.setDataSource(fis.getFD());
            mPlayer.prepareAsync();
            mPlayer.setVolume(1f, 1f);// 播放音量
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.e("Chat", "准备完毕,播放");
                    mp.start();
                }
            });

            mPlayer.setOnCompletionListener(onCompletionListener);

        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * 暂停
     */
    public static void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            isPause = true;
        }
    }

    /*
     * 暂停获取焦点时
     */
    public static void resume() {
        if (mPlayer != null && isPause) {
            mPlayer.start();
            isPause = false;
        }
    }

    /*
     * 停止
     */
    public static void release() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
