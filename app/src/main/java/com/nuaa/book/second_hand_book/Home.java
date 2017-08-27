package com.nuaa.book.second_hand_book;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.nuaa.book.second_hand_book.LaunchScreen.imageLoader;
import static com.nuaa.book.second_hand_book.LaunchScreen.options;
import static com.nuaa.book.second_hand_book.SearchResult.setListViewHeightBasedOnChildren;

public class Home extends Fragment {
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    int res = (int)msg.obj;
                    if(res == 1){
                        mSchedule = new SimpleAdapter(getActivity(),
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
                                    tv.setText((String )data);
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
                        pg.setVisibility(View.GONE);
                        //添加并且显示
                        mlistview.setAdapter(mSchedule);
                        setListViewHeightBasedOnChildren(mlistview);
                        mSchedule.notifyDataSetChanged();
                    }
                    else if (res == 2) {
                        mswipeRefreshLayout.setRefreshing(false);
                        pg.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), R.string.server_error, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    private TextView more;
    private LinearLayout allBook,socialsci,art,economics,life,language,science,exam,cs,Medical;
    private ListView mlistview;
    private EditText search;
    private List<HashMap<String, Object>> mListData = new ArrayList<HashMap<String, Object>>();
    private SwipeRefreshLayout mswipeRefreshLayout;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private SimpleAdapter mSchedule;
    private ProgressBar pg;
    public Home() {
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home,container,false);
        allBook = (LinearLayout) view.findViewById(R.id.all);
        socialsci = (LinearLayout) view.findViewById(R.id.socialsci);
        art = (LinearLayout)view.findViewById(R.id.art);
        economics = (LinearLayout)view.findViewById(R.id.economics);
        life = (LinearLayout)view.findViewById(R.id.life);
        language = (LinearLayout)view.findViewById(R.id.language);
        science = (LinearLayout)view.findViewById(R.id.science);
        exam = (LinearLayout)view.findViewById(R.id.exam);
        cs = (LinearLayout)view.findViewById(R.id.cs);
        Medical = (LinearLayout)view.findViewById(R.id.Medical);
        mlistview = (ListView) view.findViewById(R.id.MyListView);
        mswipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
        search = (EditText) view.findViewById(R.id.search);
        pg = (ProgressBar)view.findViewById(R.id.pg);
        more = (TextView)view.findViewById(R.id.more);
        preferences = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        System.out.println("Home......token"+preferences.getString("token",null));
        getData();              //从网络中获取数据
        mswipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Fragment fg = getFragmentManager().findFragmentByTag("home");
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.detach(fg);
                transaction.attach(fg);
                transaction.commit();
            }
        });

        more.setOnClickListener(new MyOnClickListener(more.getId()));
        search.setOnClickListener(new MyOnClickListener(search.getId()));
        allBook.setOnClickListener(new MyOnClickListener(allBook.getId()));
        socialsci.setOnClickListener(new MyOnClickListener(socialsci.getId()));
        art.setOnClickListener(new MyOnClickListener(art.getId()));
        economics.setOnClickListener(new MyOnClickListener(economics.getId()));
        life.setOnClickListener(new MyOnClickListener(life.getId()));
        language.setOnClickListener(new MyOnClickListener(language.getId()));
        science.setOnClickListener(new MyOnClickListener(science.getId()));
        exam.setOnClickListener(new MyOnClickListener(exam.getId()));
        cs.setOnClickListener(new MyOnClickListener(cs.getId()));
        Medical.setOnClickListener(new MyOnClickListener(Medical.getId()));
        return view;

    }

    public void getData(){
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                JSONObject res = NewService.getbook(preferences.getString("token",null),0,1);
                if (res!=null) {
                    try {
                        JSONArray result = res.getJSONArray("book");

                        mListData.clear();
                        for (int i = 0; i < result.length(); i++) {
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
                        }
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = 1;
                        handler.sendMessage(msg);
                        System.out.println(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = 2;        //代表服务器失败
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
    public static Calendar stampTocal(String s){
        s+="000";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
    class MyOnClickListener implements View.OnClickListener{

        private int buttonId;

        public MyOnClickListener(int buttonId) {
            this.buttonId = buttonId;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.search) {
                Intent intent = new Intent(getActivity(), SearchResult.class);
                startActivity(intent);
            } else if (v.getId() == R.id.art){
                Intent intent = new Intent(getActivity(), AllBook.class);
                intent.putExtra("type", 9);
                startActivity(intent);
            }else if (v.getId() == R.id.socialsci){
                Intent intent = new Intent(getActivity(), AllBook.class);
                intent.putExtra("type", 8);
                startActivity(intent);
            }else if (v.getId() == R.id.economics){
                Intent intent = new Intent(getActivity(), AllBook.class);
                intent.putExtra("type", 7);
                startActivity(intent);
            }else if (v.getId() == R.id.life){
                Intent intent = new Intent(getActivity(), AllBook.class);
                intent.putExtra("type", 6);
                startActivity(intent);
            }else if (v.getId() == R.id.language){
                Intent intent = new Intent(getActivity(), AllBook.class);
                intent.putExtra("type", 5);
                startActivity(intent);
            }else if (v.getId() == R.id.science){
                Intent intent = new Intent(getActivity(), AllBook.class);
                intent.putExtra("type", 4);
                startActivity(intent);
            }else if (v.getId() == R.id.exam){
                Intent intent = new Intent(getActivity(), AllBook.class);
                intent.putExtra("type", 3);
                startActivity(intent);
            }else if (v.getId() == R.id.cs){
                Intent intent = new Intent(getActivity(), AllBook.class);
                intent.putExtra("type", 2);
                startActivity(intent);
            }else if (v.getId() == R.id.Medical){
                Intent intent = new Intent(getActivity(), AllBook.class);
                intent.putExtra("type", 1);
                startActivity(intent);
            }else if (v.getId() == R.id.more || v.getId() == R.id.all) {
                Intent intent = new Intent(getActivity(), AllBook.class);
                intent.putExtra("type", 0);
                startActivity(intent);
            }

        }
    }
}