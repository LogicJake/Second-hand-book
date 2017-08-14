package com.nuaa.book.second_hand_book;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.nuaa.book.second_hand_book.LaunchScreen.imageLoader;
import static com.nuaa.book.second_hand_book.LaunchScreen.options;
import static com.nuaa.book.second_hand_book.NewService.pic_root;

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
                                new String[] {"name","url", "bookname","oldprice","author","nowprice","quality","sex"},
                                new int[] {R.id.seller_name,R.id.book_pic,R.id.book_name,R.id.old_price,R.id.author,R.id.now_price,R.id.quality,R.id.sex});
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
                                return false;
                            }
                        });
                        pg.setVisibility(View.GONE);
                        //添加并且显示
                        mlistview.setAdapter(mSchedule);
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
    private static String url = pic_root +"1.png";
    private LinearLayout allBook;
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
        mlistview = (ListView) view.findViewById(R.id.MyListView);
        mswipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
        search = (EditText) view.findViewById(R.id.search);
        pg = (ProgressBar)view.findViewById(R.id.pg);
        preferences = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
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
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchResult.class);
                startActivity(intent);
            }
        });
        allBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"所有分类",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    public void getData(){
        new Thread(new Runnable(){
            @Override
            public void run()
            {
                JSONArray result = NewService.getbook(preferences.getString("token",null));
                if (result!=null) {
                    mListData.clear();
                    for (int i = 0; i < result.length(); i++) {
                        try {
                            JSONObject temp = (JSONObject) result.get(i);
                            HashMap map = new HashMap<String,Object>();
                            map.put("url",pic_root+temp.getString("pic_url"));
                            map.put("bookname", temp.getString("name"));
                            map.put("oldprice", "￥"+temp.getString("old_price"));
                            map.put("nowprice","￥"+temp.getString("now_price"));
                            map.put("author",temp.getString("author")+" | "+temp.getString("publisher"));
                            map.put("quality",temp.getString("quality"));
                            map.put("sex",temp.getString("seller_sex"));
                            map.put("name",temp.getString("seller_name"));
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