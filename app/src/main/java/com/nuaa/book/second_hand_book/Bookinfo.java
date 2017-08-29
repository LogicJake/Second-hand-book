package com.nuaa.book.second_hand_book;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import static com.nuaa.book.second_hand_book.Home.stampTocal;
import static com.nuaa.book.second_hand_book.LaunchScreen.imageLoader;
import static com.nuaa.book.second_hand_book.LaunchScreen.options;
import static com.nuaa.book.second_hand_book.NewService.avator_root;

/**
 * Created by Eres_tu on 2017/8/27.
 */

public class Bookinfo extends AppCompatActivity {

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    JSONObject res = (JSONObject) msg.obj;
                    System.out.println("boook_info"+res);
                    try {
                        res = res.getJSONObject("data");
                        imageLoader.displayImage(avator_root+res.getString("avator_url"),avator,options);
                        nick_name.setText(res.getString("nick_name"));
                        sell_num.setText(res.getString("sell_num"));
                        if(res.getString("sex").equals("0"))
                        {
                            sex.setImageResource(R.drawable.male);
                        }
                        else
                        {
                            sex.setImageResource(R.drawable.female);
                        }
                        book_name.setText(res.getString("name"));
                        imageLoader.displayImage(res.getString("pic_url"),pic_url,options);
                        old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                        old_price.setText("￥ "+res.getString("old_price"));
                        now_price.setText("￥ "+res.getString("now_price"));
                        String aupu = res.getString("author")+" | "+res.getString("publisher");
                        author_publisher.setText(aupu);
                        switch (res.getString("quality")){
                            case "1":
                                quality.setText("6成新");
                                break;
                            case "2":
                                quality.setText("7成新");
                                break;
                            case "3":
                                quality.setText("8成新");
                                break;
                            case "4":
                                quality.setText("9成新");
                                break;
                            case "5":
                                quality.setText("全新");
                                break;
                        }

                        String raw_time = res.getString("add_time");
                        Calendar time = stampTocal(raw_time);
                        Calendar localtime = Calendar.getInstance();

                        if (time.get(Calendar.YEAR) == localtime.get(Calendar.YEAR) && time.get(Calendar.MONTH) == localtime.get(Calendar.MONTH)) {
                            if (time.get(Calendar.DAY_OF_MONTH) == localtime.get(Calendar.DAY_OF_MONTH)) {
                                add_time.setText("今天");
                            } else {
                                if ((time.get(Calendar.DAY_OF_MONTH) + 1) == (localtime.get(Calendar.DAY_OF_MONTH))) {
                                    add_time.setText("昨天");
                                } else {
                                    add_time.setText(Integer.toString(time.get(Calendar.MONTH) + 1) + "-" + time.get(Calendar.DAY_OF_MONTH) + " " + time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE));
                                }
                            }
                        } else
                            add_time.setText(Integer.toString(time.get(Calendar.YEAR)) + "-" + Integer.toString(time.get(Calendar.MONTH) + 1) + "-" + time.get(Calendar.DAY_OF_MONTH) + " " + time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE));
                        ISBN.setText(res.getString("ISBN"));
                        num.setText(res.getString("num")+"本");
                        if (res.getString("remark") == null || res.getString("remark").length() == 0)
                            remark.setText("备注：无");
                        else
                            remark.setText(res.getString("备注："+"remark"));
                        if (res.getString("user_sign") == null || res.getString("user_sign").length() == 0)
                            sign.setText("这个人很懒，啥也没写");
                        else
                            sign.setText(res.getString("user_sign"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    int result = (int)msg.obj;
                    like.setClickable(true);
                    if (result == 0)
                        Toast.makeText(Bookinfo.this,"收藏失败",Toast.LENGTH_SHORT).show();
                    else if (result == 1)
                        Toast.makeText(Bookinfo.this,"收藏成功",Toast.LENGTH_SHORT).show();
                    else if (result == 2)
                        Toast.makeText(Bookinfo.this,"您已经收藏过了",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private String book_id;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String token;

    private ImageView avator;
    private TextView nick_name;
    private TextView sell_num;
    private ImageView sex;
    private TextView book_name;
    private ImageView pic_url;
    private TextView old_price;
    private TextView now_price;
    private TextView author_publisher;
    private TextView quality;
    private TextView add_time;
    private TextView ISBN;
    private TextView num;
    private TextView remark;
    private ImageView backup;
    private TextView sign;
    private Button like;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        Intent intent =getIntent();
        book_id = intent.getStringExtra("bookinfo_id");
        token = preferences.getString("token",null);

        avator = (ImageView)findViewById(R.id.avator);
        nick_name = (TextView)findViewById(R.id.seller_name);
        sell_num = (TextView)findViewById(R.id.sell_num);
        sex = (ImageView)findViewById(R.id.sex);
        book_name = (TextView)findViewById(R.id.book_name);
        pic_url = (ImageView)findViewById(R.id.book_url);
        old_price = (TextView)findViewById(R.id.old_price);
        now_price = (TextView)findViewById(R.id.now_price);
        author_publisher = (TextView)findViewById(R.id.author_publisher);
        quality = (TextView)findViewById(R.id.quality);
        add_time = (TextView)findViewById(R.id.time);
        ISBN = (TextView)findViewById(R.id.ISBN);
        num = (TextView)findViewById(R.id.num);
        remark = (TextView)findViewById(R.id.remark);
        backup = (ImageView)findViewById(R.id.backup);
        sign = (TextView)findViewById(R.id.sign);
        like = (Button) findViewById(R.id.like);
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like.setClickable(false);
                new Thread(new Runnable(){
                    @Override
                    public void run()
                    {
                        int result = NewService.collection(preferences.getString("token",null),book_id);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject res = NewService.getbookinfo(preferences.getString("token",null),book_id);
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = res;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

}
