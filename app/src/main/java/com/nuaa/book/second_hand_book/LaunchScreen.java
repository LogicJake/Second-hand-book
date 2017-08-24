package com.nuaa.book.second_hand_book;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import org.json.JSONException;
import org.json.JSONObject;

public class LaunchScreen extends AppCompatActivity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static ImageLoader imageLoader;
    public static DisplayImageOptions options;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        checkLogin();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(this)
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY -2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2* 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
                .discCacheSize(50 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100) //缓存的文件数量
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(this,10 * 1000, 10 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs()
                .build();//开始构建
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(configuration);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)                               //启用内存缓存
                .cacheOnDisk(true)                                 //启用外存缓存
                .build();
    }
    public void checkLogin(){
        String name = preferences.getString("userName",null);
        String pass = preferences.getString("userPassword",null);
        String token = preferences.getString("token",null);
        if(name != null&&pass != null&&token != null)          //登陆了直接进
        {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LaunchScreen.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }
        else
        {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LaunchScreen.this,Login.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }
    }
}
