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
                quality.setText(res.getString("quality"));
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
                num.setText(res.getString("num"));
                remark.setText(res.getString("remark"));
            } catch (JSONException e) {
                e.printStackTrace();
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
    private String my;
    private Button modify;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        Intent intent =getIntent();
        book_id = intent.getStringExtra("bookinfo_id");
        my = intent.getStringExtra("my");
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
        modify = (Button)findViewById(R.id.modify);

        System.out.println("bookinfo token +"+token);

        if(my.equals("1"))
        {
            modify.setVisibility(View.VISIBLE);
            modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Bookinfo.this, Updatebook.class);
                    intent.putExtra("bookinfo_id",book_id);
                    startActivity(intent);
                }
            });
        }


        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject res = NewService.getbookinfo(preferences.getString("token",null),book_id);
                    Message msg = new Message();
                    msg.obj = res;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        Toast.makeText(Bookinfo.this,book_id,Toast.LENGTH_SHORT).show();
    }

}
