package com.nuaa.book.second_hand_book;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER_HORIZONTAL;
import static com.nuaa.book.second_hand_book.LaunchScreen.imageLoader;
import static com.nuaa.book.second_hand_book.LaunchScreen.options;
import static com.nuaa.book.second_hand_book.NewService.avator_root;

public class Setting extends AppCompatActivity {
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            JSONObject result = null;
            String key = null;
            int flag = -1;
            switch (msg.what) {
                case 0:
                    super.handleMessage(msg);
                    int res = (int)msg.obj;
                    if (res == 1)
                    {
                        Toast.makeText(Setting.this, "注销成功", Toast.LENGTH_SHORT).show();
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(Setting.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(Setting.this, "注销失败", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Setting.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
                case 1:
                    super.handleMessage(msg);
                    result = (JSONObject) msg.obj;
                    try {
                        flag = result.getInt("status");
                        key = result.getString("key");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (flag == 0)
                        Toast.makeText(Setting.this, R.string.modify_fail, Toast.LENGTH_SHORT).show();
                    else if(flag == 1){
                        Toast.makeText(Setting.this, R.string.modify_success, Toast.LENGTH_SHORT).show();
                        phone.setText(key);
                        editor.putString("phone_num",key);
                        editor.commit();
                    }
                    break;
                case 2:
                    super.handleMessage(msg);
                    result = (JSONObject) msg.obj;
                    try {
                        flag = result.getInt("status");
                        key = result.getString("key");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (flag == 0)
                        Toast.makeText(Setting.this, R.string.modify_fail, Toast.LENGTH_SHORT).show();
                    else if(flag == 1) {
                        Toast.makeText(Setting.this, R.string.modify_success, Toast.LENGTH_SHORT).show();
                        qq.setText(key);
                        editor.putString("qq_num",key);
                        editor.commit();
                    }
                    break;
                case 3:
                    super.handleMessage(msg);
                    result = (JSONObject) msg.obj;
                    int rsex = -1;
                    try {
                        flag = result.getInt("status");
                        rsex = result.getInt("key");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (flag == 0)
                        Toast.makeText(Setting.this, R.string.modify_fail, Toast.LENGTH_SHORT).show();
                    else if(flag == 1) {
                        Toast.makeText(Setting.this, R.string.modify_success, Toast.LENGTH_SHORT).show();
                        editor.putString("sex",String.valueOf(rsex));
                        editor.commit();
                        if (rsex == 1)
                            sex.setText(R.string.man);
                        else
                            sex.setText(R.string.woman);
                    }
                    break;
                case 4:
                    super.handleMessage(msg);
                    result = (JSONObject) msg.obj;
                    try {
                        flag = result.getInt("status");
                        key = result.getString("key");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (flag == 0)
                        Toast.makeText(Setting.this, R.string.modify_fail, Toast.LENGTH_SHORT).show();
                    else if(flag == 1) {
                        Toast.makeText(Setting.this, R.string.modify_success, Toast.LENGTH_SHORT).show();
                        editor.putString("user_sign",key);
                        sigh.setText(key);
                    }
                    break;
                case 5:
                    super.handleMessage(msg);
                    result = (JSONObject) msg.obj;
                    try {
                        flag = result.getInt("status");
                        key = result.getString("key");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (flag == 0)
                        Toast.makeText(Setting.this, R.string.modify_fail, Toast.LENGTH_SHORT).show();
                    else if(flag == 1) {
                        Toast.makeText(Setting.this, R.string.modify_success, Toast.LENGTH_SHORT).show();
                        editor.putString("nick_name",key);
                        name.setText(key);
                    }
                    break;
            }
        }
    };
    private Button exit;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private TextView name,stu_id,phone,qq,sex,sigh;
    private TableRow trtele,trqq,trsex,trsigh,nickname;
    private CircleImageView avator;
    SelectPicPopupWindow menuWindow;
    String single[] = {"男","女"};
    String singleChoice;

    private int REQUEST_CODE_GALLERY = 200;
    private int REQUEST_CODE_CAMERA = 100;
    private FunctionConfig functionConfig;

    private SweetAlertDialog pDialog;
    private SweetAlertDialog pDialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        exit = (Button)findViewById(R.id.exit);
        name = (TextView)findViewById(R.id.name);
        stu_id = (TextView)findViewById(R.id.stu_id);
        phone = (TextView)findViewById(R.id.phone);
        qq = (TextView)findViewById(R.id.qq);
        sex = (TextView)findViewById(R.id.sex);
        sigh = (TextView)findViewById(R.id.sigh);

        nickname = (TableRow)findViewById(R.id.nickname);
        trtele = (TableRow)findViewById(R.id.trtele);
        trqq = (TableRow)findViewById(R.id.trqq);
        trsex = (TableRow)findViewById(R.id.trsex);
        trsigh = (TableRow)findViewById(R.id.trsigh);
        avator = (CircleImageView)findViewById(R.id.avator);
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        InitGalleryFinal();                 //初始化图片选择器
        initInfo();
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable(){
                    @Override
                    public void run()
                    {
                        final String token = preferences.getString("token",null);
                        int result = NewService.logout(token);
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });
        nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.app.AlertDialog.Builder builder;
                final EditText et = new EditText(Setting.this);
                et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                et.setSingleLine(true);
                et.setMovementMethod(ScrollingMovementMethod.getInstance());
                builder = new android.app.AlertDialog.Builder(Setting.this);
                builder.setTitle("请输入用户名（不超过5个字符）");
                et.setText(name.getText());
                builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            new Thread(new Runnable(){
                                @Override
                                public void run()
                                {
                                    String key = et.getText().toString();
                                    JSONObject result =  NewService.getinfo(preferences.getString("token",null),"update_nick_name",key);
                                    Message msg = new Message();
                                    msg.obj = result;
                                    msg.what = 5;
                                    handler.sendMessage(msg);
                                }
                            }).start();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
                builder.setView(et);
                builder.setNegativeButton(R.string.cancel, null);
                builder.create().show();
            }
        });
        trtele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.app.AlertDialog.Builder builder;
                final EditText et = new EditText(Setting.this);
                et.setInputType(InputType.TYPE_CLASS_PHONE);
                et.setText(phone.getText());
                builder = new android.app.AlertDialog.Builder(Setting.this);
                builder.setTitle(R.string.modfy_phone);
                builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            new Thread(new Runnable(){
                                @Override
                                public void run()
                                {
                                    String key = et.getText().toString();
                                    JSONObject result =  NewService.getinfo(preferences.getString("token",null),"update_phone_num",key);
                                    Message msg = new Message();
                                    msg.obj = result;
                                    msg.what = 1;
                                    handler.sendMessage(msg);
                                }
                            }).start();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
                builder.setView(et);
                builder.setNegativeButton(R.string.cancel, null);
                builder.create().show();
            }
        });

        trqq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.app.AlertDialog.Builder builder;
                final EditText et = new EditText(Setting.this);
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                et.setText(qq.getText());
                builder = new android.app.AlertDialog.Builder(Setting.this);
                builder.setTitle(R.string.modify_qq);
                builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            new Thread(new Runnable(){
                                @Override
                                public void run()
                                {
                                    String key = et.getText().toString();
                                    JSONObject result =  NewService.getinfo(preferences.getString("token",null),"update_qq_num",key);
                                    Message msg = new Message();
                                    msg.obj = result;
                                    msg.what = 2;
                                    handler.sendMessage(msg);
                                }
                            }).start();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
                builder.setView(et);
                builder.setNegativeButton(R.string.cancel, null);
                builder.create().show();
            }
        });

        trsex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final android.app.AlertDialog.Builder builder;
                builder = new android.app.AlertDialog.Builder(Setting.this);
                builder.setTitle(R.string.modify_sex);
                builder.setSingleChoiceItems(single,3, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        singleChoice = single[which];
                    }
                });
                builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            new Thread(new Runnable(){
                                @Override
                                public void run()
                                {
                                    String key = singleChoice;
                                    JSONObject result =  NewService.getinfo(preferences.getString("token",null),"update_sex",key);
                                    Message msg = new Message();
                                    msg.obj = result;
                                    msg.what = 3;
                                    handler.sendMessage(msg);
                                }
                            }).start();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.create().show();
            }
        });

        trsigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.app.AlertDialog.Builder builder;
                final EditText et = new EditText(Setting.this);
                et.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                et.setMinLines(1);
                et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                et.setSingleLine(false);
                et.setMovementMethod(ScrollingMovementMethod.getInstance());
                builder = new android.app.AlertDialog.Builder(Setting.this);
                builder.setTitle(R.string.modify_sign);
                et.setText(sigh.getText());
                builder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            new Thread(new Runnable(){
                                @Override
                                public void run()
                                {
                                    String key = et.getText().toString();
                                    JSONObject result =  NewService.getinfo(preferences.getString("token",null),"update_user_sign",key);
                                    Message msg = new Message();
                                    msg.obj = result;
                                    msg.what = 4;
                                    handler.sendMessage(msg);
                                }
                            }).start();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
                builder.setView(et);
                builder.setNegativeButton(R.string.cancel, null);
                builder.create().show();
            }
        });
        avator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuWindow = new SelectPicPopupWindow(Setting.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(Setting.this.findViewById(R.id.avatar), BOTTOM|CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            }
        });
    }
    public void initInfo(){
        name.setText(preferences.getString("nick_name",null));
        stu_id.setText(preferences.getString("userName","未认证"));
        phone.setText(preferences.getString("phone_num","未设置"));
        qq.setText(preferences.getString("qq_num","未设置"));
        trtele = (TableRow)findViewById(R.id.trtele);
        trqq = (TableRow)findViewById(R.id.trqq);
        trsex = (TableRow)findViewById(R.id.trsex);
        trsigh = (TableRow)findViewById(R.id.trsigh);
        if(preferences.getString("sex","-1").equals("-1"))
            sex.setText("未设置");
        else if(preferences.getString("sex","-1").equals("0"))
            sex.setText("女");
        else
            sex.setText("男");
        if (preferences.getString("user_sign","null").equals("null"))
            sigh.setText("");
        else
            sigh.setText(preferences.getString("user_sign",null));
        imageLoader.displayImage(avator_root+preferences.getString("avator_url",null),avator,options);
    }
    private View.OnClickListener itemsOnClick = new View.OnClickListener(){

        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_take_photo:
                    GalleryFinal.openCamera(REQUEST_CODE_CAMERA, functionConfig, mOnHanlderResultCallback);
                    break;
                case R.id.btn_pick_photo:
                    GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, functionConfig, mOnHanlderResultCallback);
                    break;
                default:
                    break;
            }
        }
    };

    public void InitGalleryFinal(){
        ThemeConfig theme = new ThemeConfig.Builder().build();

        functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(true)
                .setEnableEdit(true)
                .setEnableCrop(true)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnablePreview(true)
                .build();

        cn.finalteam.galleryfinal.ImageLoader imageloader = new UILImageLoader();

        CoreConfig coreConfig = new CoreConfig.Builder(Setting.this, imageloader, theme).setFunctionConfig(functionConfig).build();
        GalleryFinal.init(coreConfig);
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback=new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int i, List<PhotoInfo> list) {
            pDialog2 = new SweetAlertDialog(Setting.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog2.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog2.setTitleText("上传头像中");
            pDialog2.setCancelable(false);
            pDialog2.show();
            final String filepath = list.get(0).getPhotoPath();
            if (list!=null){
                if (i==100){
                    System.out.println("openCamera");
                }else if (i==200){
                    System.out.println("openGallerySingle");
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject res = NewService.avater(new File(filepath), preferences.getString("token", ""));
                        Message msg = new Message();
                        msg.what = 5;
                        msg.obj = res;
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        }
        @Override
        public void onHanlderFailure(int i, String s) {
            Toast.makeText(Setting.this,"设置头像失败",Toast.LENGTH_SHORT);
        }
    };
}
