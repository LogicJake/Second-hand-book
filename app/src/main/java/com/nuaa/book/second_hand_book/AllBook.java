package com.nuaa.book.second_hand_book;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ScrollingTabContainerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.nuaa.book.second_hand_book.Home.stampTocal;
import static com.nuaa.book.second_hand_book.LaunchScreen.imageLoader;
import static com.nuaa.book.second_hand_book.LaunchScreen.options;
import static com.nuaa.book.second_hand_book.NewService.pic_root;
import static com.nuaa.book.second_hand_book.SearchResult.setListViewHeightBasedOnChildren;

public class AllBook extends AppCompatActivity {
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    int res = (int)msg.obj;
                    if(res == 1){
                        if (mListData.size() == 0)
                            noInfo.setVisibility(View.VISIBLE);
                        else
                            noInfo.setVisibility(View.GONE);
                        mSchedule = new SimpleAdapter(AllBook.this,
                                mListData,//数据来源
                                R.layout.item_list,//ListItem的XML实现
                                new String[]{"name", "url", "bookname", "oldprice", "author", "nowprice", "quality", "sex", "add_time"},
                                new int[]{R.id.seller_name, R.id.book_pic, R.id.book_name, R.id.old_price, R.id.author, R.id.now_price, R.id.quality, R.id.sex, R.id.time});
                        mSchedule.setViewBinder(new SimpleAdapter.ViewBinder() {
                            @Override
                            public boolean setViewValue(View view, Object data, String textRepresentation) {
                                if (view.getId() == R.id.book_pic) {
                                    ImageView iv = (ImageView) view;
                                    imageLoader.displayImage((String) data, iv, options);
                                    return true;
                                } else if (view.getId() == R.id.old_price) {
                                    TextView tv = (TextView) view;
                                    tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                    return true;
                                } else if (view.getId() == R.id.quality) {
                                    TextView tv = (TextView) view;
                                    String quality = (String) data;
                                    if (quality.equals("5"))
                                        tv.setText("全新");
                                    else if (quality.equals("4"))
                                        tv.setText("9成新");
                                    else if (quality.equals("3"))
                                        tv.setText("8成新");
                                    else if (quality.equals("2"))
                                        tv.setText("7成新");
                                    else if (quality.equals("1"))
                                        tv.setText("6成新");
                                    else if (quality.equals("0"))
                                        tv.setText("较破旧");
                                    return true;
                                } else if (view.getId() == R.id.sex) {
                                    ImageView sex = (ImageView) view;
                                    if (((String) data).equals("0"))
                                        sex.setImageResource(R.drawable.female);
                                    else
                                        sex.setImageResource(R.drawable.male);
                                    return true;
                                } else if (view.getId() == R.id.time) {
                                    String raw_time = (String) data;
                                    Calendar time = stampTocal(raw_time);
                                    Calendar localtime = Calendar.getInstance();
                                    TextView tv_time = (TextView) view;
                                    if (time.get(Calendar.YEAR) == localtime.get(Calendar.YEAR) && time.get(Calendar.MONTH) == localtime.get(Calendar.MONTH)) {
                                        if (time.get(Calendar.DAY_OF_MONTH) == localtime.get(Calendar.DAY_OF_MONTH)) {
                                            tv_time.setText("今天");
                                        } else {
                                            if ((time.get(Calendar.DAY_OF_MONTH) + 1) == (localtime.get(Calendar.DAY_OF_MONTH))) {
                                                tv_time.setText("昨天");
                                            } else {
                                                tv_time.setText(Integer.toString(time.get(Calendar.MONTH) + 1) + "-" + time.get(Calendar.DAY_OF_MONTH) + " " + time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE));
                                            }
                                        }
                                    } else
                                        tv_time.setText(Integer.toString(time.get(Calendar.YEAR)) + "-" + Integer.toString(time.get(Calendar.MONTH) + 1) + "-" + time.get(Calendar.DAY_OF_MONTH) + " " + time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE));
                                    return true;
                                }
                                return false;
                            }
                        });
                        mlistview.setAdapter(mSchedule);
                        setListViewHeightBasedOnChildren(mlistview);
                        mSchedule.notifyDataSetChanged();
                    }
                    else if (res == 2) {
                        Toast.makeText(AllBook.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    private SharedPreferences preferences;
    private ImageView backup;
    private List<HashMap<String, Object>> mListData = new ArrayList<HashMap<String, Object>>();
    private SimpleAdapter mSchedule;
    private ListView mlistview;
    private Spinner spinner;
    private TextView noInfo;
    private ScrollView scrollView;
    private ProgressBar pg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_book);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);       //先声明再使用
        backup = (ImageView) findViewById(R.id.backup);
        spinner = (Spinner)findViewById(R.id.classify);
        noInfo = (TextView)findViewById(R.id.noInfo);
        mlistview = (ListView)findViewById(R.id.booklist) ;
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        pg = (ProgressBar)findViewById(R.id.pg);
        mlistview.setDividerHeight(30);
        mListData.clear();      //先清空
        Intent intent =getIntent();
        int type = intent.getIntExtra("type",0);
        spinner.setSelection(type);
        getData(type);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
                mListData.clear();
                getData(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if (scrollView.getChildAt(0).getMeasuredHeight() <= scrollView.getHeight()+scrollView.getScrollY())
                    {
                        System.out.println("我正在刷新");
                        pg.setVisibility(View.VISIBLE );
                    }
                }
                return false;
            }
        });
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void getData(final int type){
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                JSONArray result = NewService.getbook(preferences.getString("token",null),type);
                if (result!=null) {
                    for (int i = 0; i < result.length(); i++) {
                        try {
                            JSONObject temp = (JSONObject) result.get(i);
                            HashMap map = new HashMap<String,Object>();
                            map.put("url",temp.getString("pic_url"));
                            map.put("bookname", temp.getString("name"));
                            map.put("oldprice", "￥"+temp.getString("old_price"));
                            map.put("nowprice","￥"+temp.getString("now_price"));
                            map.put("author",temp.getString("author")+" | "+temp.getString("publisher"));
                            map.put("quality",temp.getString("quality"));
                            map.put("sex",temp.getString("seller_sex"));
                            map.put("name",temp.getString("seller_name"));
                            map.put("add_time",temp.get("add_time"));
                            mListData.add(map);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = 1;
                    handler.sendMessage(msg);
                    System.out.println(result);
                }
                else {
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = 2;        //代表服务器失败
                    handler.sendMessage(msg);
                    System.out.println(result);
                }
            }
        }).start();
    }
}
