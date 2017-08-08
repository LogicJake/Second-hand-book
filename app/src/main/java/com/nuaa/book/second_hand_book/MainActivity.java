package com.nuaa.book.second_hand_book;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private TextView mTextMessage;
    private Home home;
    private AddBook addBook;
    private UserInfo userInfo;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    initHome();
                    return true;
                case R.id.navigation_add:
                    initAddBook();
                    return true;
                case R.id.navigation_info:
                    Boolean isLogin = preferences.getBoolean("isLogin",false);
                    if(isLogin == true)
                        initUserInfo();
                    else{
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        startActivity(intent);
                    }
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
        initHome();
        checkLogin();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    public void checkLogin(){
        String name = preferences.getString("userName",null);
        String pass = preferences.getString("userPassword",null);
        if(name != null&&pass != null)          //登陆了
        {
            editor.putBoolean("isLogin",true);
            editor.commit();
            //自动登陆获取信息
        }
        else
        {
            editor.putBoolean("isLogin",false);
            editor.commit();
        }
    }

    private void initHome() {
        //开启事务，fragment的控制是由事务来实现的
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(home == null){
            home = new Home();
            transaction.add(R.id.content, home);
        }
        //隐藏所有fragment
        hideFragment(transaction);
        //显示需要显示的fragment
        transaction.show(home);
        transaction.commit();
    }

    private void initAddBook(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(addBook == null){
            addBook = new AddBook();
            transaction.add(R.id.content, addBook);
        }
        hideFragment(transaction);
        transaction.show(addBook);
        transaction.commit();
    }

    private void initUserInfo(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(userInfo == null){
            userInfo = new UserInfo();
            transaction.add(R.id.content, userInfo);
        }
        hideFragment(transaction);
        transaction.show(userInfo);
        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction){
        if(home != null){
            transaction.hide(home);
        }
        if(addBook != null){
            transaction.hide(addBook);
        }
        if(userInfo != null){
            transaction.hide(userInfo);
        }
    }
}
