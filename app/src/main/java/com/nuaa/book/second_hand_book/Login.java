package com.nuaa.book.second_hand_book;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Login extends AppCompatActivity {
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String name = editName.getText().toString().trim();
                    String password = editPassword.getText().toString().trim();
                    int id = -1;
                    Boolean res = true;
                    String token = null;
                    super.handleMessage(msg);
                    JSONObject result = (JSONObject) msg.obj;
                    if (result == null) {
                        pDialog.cancel();
                        Toast.makeText(Login.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                        if (name.equals("admin") && password.equals("admin")) {       //断网下测试使用
                            editor.putString("userName", name);
                            editor.putString("userPassword", password);
                            editor.putInt("id", id);
                            editor.commit();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        try {
                            id = result.getInt("id");
                            token = result.getString("token");
                            res = result.getBoolean("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (res) {
                            pDialog.cancel();
                            editor.putString("userName", name);
                            editor.putString("userPassword", password);
                            editor.putInt("id", id);
                            editor.putString("token",token);
                            editor.commit();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            editor.clear();
                            editor.commit();
                            pDialog.cancel();
                            Toast.makeText(Login.this, R.string.password_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    };
    private Button bt_login;
    private EditText editName, editPassword;
    private SharedPreferences preferences;
//    private TextView signup;
    private ImageView eye;
    private Boolean eyeOpen = false;
    private SweetAlertDialog pDialog;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        bt_login = (Button)findViewById(R.id.login);
        editName = (EditText)findViewById(R.id.editText1);
        editPassword = (EditText)findViewById(R.id.editText2);
        eye = (ImageView)findViewById(R.id.eye);
//        signup = (TextView)findViewById(R.id.sign_up);
//        signup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Login.this, SignUp.class);
//                startActivity(intent);
//                finish();
//            }
//        });
        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( eyeOpen ){
                    //密码 TYPE_CLASS_TEXT 和 TYPE_TEXT_VARIATION_PASSWORD 必须一起使用
                    editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eye.setImageResource( R.drawable.close_eye );
                    eyeOpen = false ;
                }else {
                    //明文
                    editPassword.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                    eye.setImageResource( R.drawable.open_eye );
                    eyeOpen = true ;
                }
            }
        });
        bt_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText(getString(R.string.loging));
                pDialog.setCancelable(false);
                pDialog.show();
                try {
                    final String name = editName.getText().toString();
                    final String password = editPassword.getText().toString();
                    if(name.length() == 0)
                        Toast.makeText(Login.this, R.string.name_no_empty, Toast.LENGTH_SHORT).show();
                    else {
                        if (password.length() == 0)
                            Toast.makeText(Login.this, R.string.password_no_empty, Toast.LENGTH_SHORT).show();
                        else {
                            new Thread(new Runnable(){
                                @Override
                                public void run()
                                {
                                    JSONObject result = NewService.login(name, password);
                                    Message msg = new Message();
                                    msg.what = 0;
                                    msg.obj = result;
                                    handler.sendMessage(msg);

                                }
                            }).start();
                        }
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
}
