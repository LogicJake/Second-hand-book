package com.nuaa.book.second_hand_book;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class Setting extends AppCompatActivity {
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    super.handleMessage(msg);
                    int result = (int)msg.obj;
                    if (result == 1)
                    {
                        Toast.makeText(Setting.this, "注销成功", Toast.LENGTH_SHORT).show();
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(Setting.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(Setting.this, "注销失败", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Setting.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
            }
        }
    };
    private Button exit;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private SwipeRefreshLayout mswipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        exit = (Button)findViewById(R.id.exit);
        mswipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable(){
                    @Override
                    public void run()
                    {
                        final String token = preferences.getString("token",null);
                        int result = NewService.logout(token);
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });
        mswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        mswipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }
}
