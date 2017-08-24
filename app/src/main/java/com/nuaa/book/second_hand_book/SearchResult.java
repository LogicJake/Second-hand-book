package com.nuaa.book.second_hand_book;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
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

public class SearchResult extends AppCompatActivity {
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    int res = (int)msg.obj;
                    if(res == 1){
                        mSchedule = new SimpleAdapter(SearchResult.this,
                                mListData,//数据来源
                                R.layout.item_list,//ListItem的XML实现
                                new String[] {"name","url", "bookname","oldprice","author","nowprice","quality","sex","add_time"},
                                new int[] {R.id.seller_name,R.id.book_pic,R.id.book_name,R.id.old_price,R.id.author,R.id.now_price,R.id.quality,R.id.sex,R.id.time});
                        mSchedule.setViewBinder(new SimpleAdapter.ViewBinder() {
                            @Override
                            public boolean setViewValue(View view, Object data,String textRepresentation) {
                                if (view.getId() == R.id.book_pic) {
                                    ImageView iv = (ImageView) view;
                                    imageLoader.displayImage((String)data, iv, options);
                                    return true;
                                }
                                else if (view.getId() == R.id.old_price)
                                {
                                    TextView tv = (TextView) view;
                                    tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                    return true;
                                }
                                else if(view.getId() == R.id.quality){
                                    TextView tv = (TextView) view;
                                    String quality = (String)data;
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
                                }
                                else if (view.getId() == R.id.sex){
                                    ImageView sex = (ImageView)view;
                                    if (((String)data).equals("0"))
                                        sex.setImageResource(R.drawable.female);
                                    else
                                        sex.setImageResource(R.drawable.male);
                                    return true;
                                }
                                else if (view.getId() == R.id.time){
                                    String raw_time = (String)data;
                                    Calendar time = stampTocal(raw_time);
                                    Calendar localtime = Calendar.getInstance();
                                    TextView tv_time = (TextView)view;
                                    if(time.get(Calendar.YEAR) == localtime.get(Calendar.YEAR)&&time.get(Calendar.MONTH) == localtime.get(Calendar.MONTH)){
                                        if (time.get(Calendar.DAY_OF_MONTH) == localtime.get(Calendar.DAY_OF_MONTH)) {
                                            tv_time.setText("今天");
                                        }
                                        else {
                                            if ( (time.get(Calendar.DAY_OF_MONTH) + 1)== ( localtime.get(Calendar.DAY_OF_MONTH) )) {
                                                tv_time.setText("昨天");
                                            }
                                            else {
                                                tv_time.setText(Integer.toString(time.get(Calendar.MONTH) + 1) + "-" + time.get(Calendar.DAY_OF_MONTH)+" "+time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE));
                                            }
                                        }
                                    }
                                    else
                                        tv_time.setText(Integer.toString(time.get(Calendar.YEAR))+"-"+Integer.toString(time.get(Calendar.MONTH) + 1) + "-" + time.get(Calendar.DAY_OF_MONTH)+" "+time.get(Calendar.HOUR_OF_DAY) + ":" + time.get(Calendar.MINUTE));
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
                        Toast.makeText(SearchResult.this, "无结果", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    private SearchView searchView;
    private ImageView backup;
    private SharedPreferences preferences;
    private List<HashMap<String, Object>> mListData = new ArrayList<HashMap<String, Object>>();
    private SimpleAdapter mSchedule;
    private ListView mlistview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        searchView = (SearchView)findViewById(R.id.searchView);
        mlistview = (ListView)findViewById(R.id.searchResult) ;
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        backup = (ImageView)findViewById(R.id.backup);
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchView.setSubmitButtonEnabled(true);
        mlistview.setDividerHeight(30);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                mListData.clear();
                getData(query);
                return false;
            }
            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void getData(final String query) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray result = NewService.search(preferences.getString("token",null),query);
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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        //获取listview的适配器
        ListAdapter listAdapter = listView.getAdapter(); //item的高度
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); //计算子项View 的宽高 //统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight()+listView.getDividerHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;
        listView.setLayoutParams(params);
    }
}
