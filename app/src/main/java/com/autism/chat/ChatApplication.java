package com.autism.chat;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.Bmob;

/**
 * Created by AutismPerson on 4/4 0004.
 */
public class ChatApplication extends Application{

    public static ChatApplication getInstance() {
        return instance;
    }

    public static void setInstance(ChatApplication instance) {
        ChatApplication.instance = instance;
    }

    private static ChatApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
        BmobIM.init(this);
        initImageLoader(this);
        Bmob.initialize(this, "3bbdfb2c71a839f2f36ecbf91fb5b497");
        BmobIM.registerDefaultMessageHandler(new MessageHandler(this));
    }



    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPoolSize(3);
        config.memoryCache(new WeakMemoryCache());
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
//      config.writeDebugLogs(); // Remove for release app
//      Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }







}
