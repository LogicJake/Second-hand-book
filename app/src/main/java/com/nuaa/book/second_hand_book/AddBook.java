package com.nuaa.book.second_hand_book;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.nuaa.book.second_hand_book.LaunchScreen.imageLoader;
import static com.nuaa.book.second_hand_book.LaunchScreen.options;
import static com.nuaa.book.second_hand_book.NewService.pic_root;

public class AddBook extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int res = -1;
            JSONObject result = (JSONObject) msg.obj;
            if (result == null) {
                pDialog.cancel();
                Toast.makeText(AddBook.this, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
            else{
                try {
                    res = result.getInt("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.cancel();
                if (res == 2)
                    Toast.makeText(AddBook.this, "请不要重复提交", Toast.LENGTH_SHORT).show();
                else if (res == 0)
                    Toast.makeText(AddBook.this, "上架失败", Toast.LENGTH_SHORT).show();
                else if (res == 1)
                    Toast.makeText(AddBook.this, "上架成功", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private Handler ISBNhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONObject result = (JSONObject) msg.obj;
            if (result==null){
                Toast.makeText(AddBook.this, R.string.no_book, Toast.LENGTH_SHORT).show();
                System.out.println("has code:yes");
            }
            else{
                if(result.has("code"))
                {
                    System.out.println("has code:yes "+result.has("code"));
                }
                System.out.println("has code:yes "+result.has("code"));
                try {
                    System.out.println(result);
                    name.setText(result.getString("title"));
                    JSONArray authors = result.getJSONArray("author");
                    String au = null;
                    publisher.setText(result.getString("publisher"));
                    old_price.setText(result.getString("price"));

                    System.out.println("作者："+authors);
                    for(int i = 0 ;i<authors.length();i++)
                    {
                        if(i>0)
                        {
                            au = au + ',' + authors.getString(i).toString();
                        }else {
                            au = au + authors.getString(i);
                        }
                        System.out.println(authors.getString(i));
                    }
                    author.setText(au);
                    image_url = result.getJSONObject("images").getString("large");
                    imageLoader.displayImage(image_url,cover,options);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(AddBook.this, "识别成功", Toast.LENGTH_SHORT).show();
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
    private ImageView backup;
    private Spinner classify;
    private Spinner quality;
    private EditText remark;
    private ImageView QR;
    private ImageView cover;

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
    private String image_url;

    private Button publish;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private SweetAlertDialog pDialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_book);

        ISBN = (EditText)findViewById(R.id.ISBN);
        name = (EditText)findViewById(R.id.name);
        author = (EditText)findViewById(R.id.author);
        publisher = (EditText)findViewById(R.id.publisher);
        old_price = (EditText)findViewById(R.id.old_price);
        now_price = (EditText)findViewById(R.id.now_price);
        num = (EditText)findViewById(R.id.num);
        classify = (Spinner)findViewById(R.id.classify);
        quality = (Spinner)findViewById(R.id.quality);
        remark = (EditText)findViewById(R.id.remark);
        publish = (Button)findViewById(R.id.publish);
        backup = (ImageView) findViewById(R.id.backup);
        QR = (ImageView)findViewById(R.id.QR);
        cover = (ImageView)findViewById(R.id.cover);
        
        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        ZXingLibrary.initDisplayOpinion(this);
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                text_classify = (String)classify.getSelectedItem();
                text_quality = (String)quality.getSelectedItem();
                text_remark = remark.getText().toString();
                token = preferences.getString("token",null);
                pDialog = new SweetAlertDialog(AddBook.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("正在上传");
                pDialog.show();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject result = NewService.addbook(token,text_ISBN,text_name,text_author,text_publisher,text_old_price,text_now_price,text_num,text_classify,text_quality,text_remark,image_url);
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = result;
                        System.out.println("上架。。。。"+result);
                        handler.sendMessage(msg);
                    }
                });
                thread.start();
            }
        });
        QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("cameral");
                cameraTask();
            }
        });
    }

    /**
     * 扫描跳转Activity RequestCode
     */
    public static final int REQUEST_CODE = 111;
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    final String ISBN_num = bundle.getString(CodeUtils.RESULT_STRING);
                    System.out.println("ISBN_NUM: "+ISBN_num);
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject result = NewService.ISBNinfo(ISBN_num);
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = result;
                            System.out.println("result: "+result);
                            ISBNhandler.sendMessage(msg);
                        }
                    });
                    thread.start();
                    ISBN.setText(ISBN_num);
                    Toast.makeText(this, "解析结果:" + ISBN_num, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(AddBook.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
    /**
     * 请求CAMERA权限码
     */
    public static final int REQUEST_CAMERA_PERM = 101;

    @AfterPermissionGranted(REQUEST_CAMERA_PERM)
    public void cameraTask() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            // Have permission, do the thing!
            System.out.println("cameral");
            Intent intent = new Intent(getApplication(), CaptureActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "需要请求camera权限",
                    REQUEST_CAMERA_PERM, Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, "当前App需要申请camera权限,需要打开设置页面么?")
                    .setTitle("权限申请")
                    .setPositiveButton("确认")
                    .setNegativeButton("取消", null /* click listener */)
                    .setRequestCode(REQUEST_CAMERA_PERM)
                    .build()
                    .show();
        }
    }
}
