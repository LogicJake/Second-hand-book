package com.nuaa.book.second_hand_book;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;


public class UserInfo extends Fragment {
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0:
                    super.handleMessage(msg);
                    JSONObject res = (JSONObject) msg.obj;
                    if (res == null) {
                        Toast.makeText(getActivity(), R.string.server_error, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        try {
                            editor.putString("stu_id",res.getString("stu_id"));
                            editor.putString("phone_num",res.getString("phone_num"));
                            editor.putString("qq_num",res.getString("qq_num"));
                            editor.putString("user_sign",res.getString("user_sign"));
                            editor.putString("sex",res.getString("sex"));
                            editor.putString("avator_url",res.getString("avator_url"));
                            editor.putString("sell_num",res.getString("sell_num"));
                            editor.putString("like_num",res.getString("like_num"));
                            editor.commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        user_name.setText(preferences.getString("userName",null));
                        sell_num.setText("("+preferences.getString("sell_num","0")+")");
                        like_num.setText("("+preferences.getString("like_num","0")+")");
                    }
                    break;
            }
        }
    };
    private LinearLayout setting;
    private TextView user_name,sell_num,like_num;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private SwipeRefreshLayout mswipeRefreshLayout;
    public UserInfo() {
        // Required empty public constructor
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user_info,container,false);
        setting = (LinearLayout)view.findViewById(R.id.setting);
        user_name = (TextView)view.findViewById(R.id.name);
        sell_num = (TextView) view.findViewById(R.id.sell_num);
        like_num = (TextView) view.findViewById(R.id.like_num);
        mswipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
        preferences = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        getInfo();
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), Setting.class);
                startActivity(intent);
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
        return view;
    }
    public void getInfo(){
        try {         //初始化个人信息
            new Thread(new Runnable(){
                @Override
                public void run()
                {
                    JSONObject result = NewService.getinfo(preferences.getString("token",null),"get_info",null);
                    Message msg = new Message();
                    msg.obj = result;
                    msg.what = 0;
                    handler.sendMessage(msg);
                }
            }).start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
