package com.nuaa.book.second_hand_book;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class UserInfo extends Fragment {
    private SwipeRefreshLayout mswipeRefreshLayout;
    private LinearLayout setting;
    public UserInfo() {
        // Required empty public constructor
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_user_info,container,false);
        mswipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
        setting = (LinearLayout)view.findViewById(R.id.setting);
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
}
