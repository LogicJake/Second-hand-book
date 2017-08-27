package com.nuaa.book.second_hand_book;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int res = (int)msg.obj;
            pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            if (res == 0){
                pDialog.setTitleText(getString(R.string.server_error));
                pDialog.setCancelable(false);
                pDialog.show();
            }
            else if (res == -1){
                pDialog.setTitleText("登陆过期，请重新登陆");
                pDialog.setCancelable(false);
                pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                });
                pDialog.show();
            }
        }
    };
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Home home;
    private UserInfo userInfo;
    private SweetAlertDialog pDialog;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    initHome();
                    return true;
                case R.id.navigation_add:
                    Intent intent = new Intent(MainActivity.this, AddBook.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_info:
                    initUserInfo();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        checkToekn();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentManager fragmentManager = getFragmentManager();
        if (savedInstanceState != null) { // “内存重启”时调用
            initHome();
        }else{      //正常启动时调用
            initHome();
        }
    }

    private void initHome() {
        //开启事务，fragment的控制是由事务来实现的
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(home == null){
            home = new Home();
            transaction.add(R.id.content,home,"home");
        }
        hideFragment(transaction);
        transaction.show(home);
        transaction.commit();
    }

    private void initUserInfo(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(userInfo == null){
            userInfo = new UserInfo();
            transaction.add(R.id.content,userInfo,"userinfo");
        }
        hideFragment(transaction);
        transaction.show(userInfo);
        transaction.commit();
    }

    public void hideFragment(FragmentTransaction transaction){
        if(home != null){
            transaction.hide(home);
        }
        if(userInfo != null){
            transaction.hide(userInfo);
        }
    }

    public void checkToekn(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int res = NewService.checktoken(preferences.getString("token",null));
                    Message msg = new Message();
                    msg.obj = res;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
