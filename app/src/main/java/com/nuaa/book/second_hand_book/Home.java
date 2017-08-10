package com.nuaa.book.second_hand_book;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Home extends Fragment {
    private static String url = NewService.rooturl +"pic/1.png";
    private SearchView mSearchView;
    private LinearLayout allBook;
    private ListView mlistview;
    private DisplayImageOptions options;
    private List<HashMap<String, Object>> mListData = new ArrayList<HashMap<String, Object>>();
    private ImageLoader imageLoader;
    private SwipeRefreshLayout mswipeRefreshLayout;
    public Home() {
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home,container,false);
        mSearchView = (SearchView) view.findViewById(R.id.searchView);
        allBook = (LinearLayout) view.findViewById(R.id.all);
        mlistview = (ListView) view.findViewById(R.id.MyListView);
        imageLoader = ImageLoader.getInstance();
        mswipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)                               //启用内存缓存
                .cacheOnDisk(true)                                 //启用外存缓存
                .build();
        getData();              //从网络中获取数据
        SimpleAdapter mSchedule = new SimpleAdapter(view.getContext(),
                mListData,//数据来源
                R.layout.item_list,//ListItem的XML实现
                new String[] {"url", "bookname","oldprice"},
                new int[] {R.id.book_pic,R.id.book_name,R.id.old_price});
                mSchedule.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,String textRepresentation) {
                if (view instanceof ImageView) {
                    ImageView iv = (ImageView) view;
                    imageLoader.displayImage((String)data, iv, options);
                    return true;
                }
                else if (view.getId() == R.id.old_price)
                {
                    TextView tv = (TextView) view;
                    tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                }
                return false;
            }
        });
        //添加并且显示
        mlistview.setAdapter(mSchedule);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getActivity(),query,Toast.LENGTH_SHORT).show();
                mSearchView.clearFocus();
                return false;
            }
            // 当搜索内容改变时触发该方法
            // @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        allBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"所有分类",Toast.LENGTH_SHORT).show();
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

    public void getData(){
        for (int i = 0; i < 5; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("url", url);
            map.put("bookname","面向对象编程");
            map.put("oldprice","￥ 30.00");
            mListData.add(map);
        }
    }
}

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment Main.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static Main newInstance(String param1, String param2) {
//        Main fragment = new Main();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_main, container, false);
//    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
