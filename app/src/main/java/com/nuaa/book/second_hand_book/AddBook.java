package com.nuaa.book.second_hand_book;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;
import static com.nuaa.book.second_hand_book.NewService.pic_root;

public class AddBook extends Fragment {
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONObject result = (JSONObject) msg.obj;
            if (result == null){
                //  pDialog.cancel();
                Toast.makeText(getActivity(), R.string.server_error, Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getActivity(), "上架成功", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private static String url = pic_root +"1.png";

    private EditText ISBN;
    private EditText name;
    private EditText author;
    private EditText publisher;
    private EditText old_price;
    private EditText now_price;
    private EditText num;
    private EditText classify;
    private EditText quality;
    private EditText remark;

    private String text_ISBN;
    private String text_name;
    private String text_author;
    private String text_publisher;
    private String text_old_price;
    private String text_now_price;
    private String text_num;
    private String text_classify;
    private String text_quality;
    private String text_remark;
    private String token;

    private Button publish;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public AddBook() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_add_book,container,false);

        ISBN = (EditText)view.findViewById(R.id.ISBN);
        name = (EditText)view.findViewById(R.id.name);
        author = (EditText)view.findViewById(R.id.author);
        publisher = (EditText)view.findViewById(R.id.publisher);
        old_price = (EditText)view.findViewById(R.id.old_price);
        now_price = (EditText)view.findViewById(R.id.now_price);
        num = (EditText)view.findViewById(R.id.num);
        classify = (EditText)view.findViewById(R.id.classify);
        quality = (EditText)view.findViewById(R.id.quality);
        remark = (EditText)view.findViewById(R.id.remark);
        publish = (Button)view.findViewById(R.id.publish);
        
        preferences = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                text_ISBN = ISBN.getText().toString();
                text_name = name.getText().toString();
                text_author = author.getText().toString();
                text_publisher = publisher.getText().toString();
                text_old_price = old_price.getText().toString();
                text_now_price = now_price.getText().toString();
                text_num = num.getText().toString();
                text_classify = classify.getText().toString();
                text_quality = quality.getText().toString();
                text_remark = remark.getText().toString();
                token = preferences.getString("token",null);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject result = NewService.addbook(token,text_ISBN,text_name,text_author,text_publisher,text_old_price,text_now_price,text_num,text_classify,text_quality,text_remark);
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                });
                thread.start();
            }
        });
        return view;
    }
}
