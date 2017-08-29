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

import java.io.File;
import java.util.List;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.pedant.SweetAlert.SweetAlertDialog;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER_HORIZONTAL;
import static com.nuaa.book.second_hand_book.LaunchScreen.imageLoader;
import static com.nuaa.book.second_hand_book.LaunchScreen.options;
import static com.nuaa.book.second_hand_book.NewService.cover_root;

public class Updatebook extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int res = -1;
            JSONObject result = (JSONObject) msg.obj;
            if (result == null) {
                pDialog.cancel();
                Toast.makeText(Updatebook.this, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
            else{
                try {
                    res = result.getInt("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.cancel();
                if (res == 2)
                    Toast.makeText(Updatebook.this, "请不要重复提交", Toast.LENGTH_SHORT).show();
                else if (res == 0)
                    Toast.makeText(Updatebook.this, "上架失败", Toast.LENGTH_SHORT).show();
                else if (res == 1)
                {
                    Toast.makeText(Updatebook.this, "上架成功", Toast.LENGTH_SHORT).show();
                    reset();
                }
            }
        }
    };

    private Handler Coverhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int res = -1;
            JSONObject result = (JSONObject) msg.obj;
            if (result == null) {
                Toast.makeText(Updatebook.this, R.string.server_error, Toast.LENGTH_SHORT).show();
            }
            else{
                try {
                    System.out.print("result++++: "+result);
                    image_url = cover_root +  result.getString("cover_url");
                    System.out.println("cover url "+ image_url);
                    imageLoader.displayImage(image_url,cover,options);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            pDialog2.cancel();
        }

    };
    private Handler ISBNhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONObject result = (JSONObject) msg.obj;
            if (result==null){
                Toast.makeText(Updatebook.this, R.string.no_book, Toast.LENGTH_SHORT).show();
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
                            au = authors.getString(i);
                        }
                        System.out.println(authors.getString(i));
                    }
                    author.setText(au);
                    image_url = result.getJSONObject("images").getString("large");
                    imageLoader.displayImage(image_url,cover,options);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(Updatebook.this, "识别成功", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private Handler Bookhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            JSONObject res = (JSONObject) msg.obj;
            if (res == null)
                Toast.makeText(Updatebook.this,R.string.server_error,Toast.LENGTH_SHORT);
            else {
                try {
                    ISBN.setText(res.getString("ISBN"));
                    imageLoader.displayImage(res.getString("pic_url"),cover,options);
                    name.setText(res.getString("name"));
                    author.setText(res.getString("author"));
                    publisher.setText(res.getString("publisher"));
                    old_price.setText(res.getString("old_price"));
                    now_price.setText(res.getString("now_price"));
                    num.setText(res.getString("sell_num"));
                    classify.setSelection(res.getInt("classify")-1);
                    quality.setSelection(res.getInt("quality")-1);
                    remark.setText(res.getString("remark"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

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
    private String book_id;

    private Button publish;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private SweetAlertDialog pDialog;

    private int REQUEST_CODE_GALLERY = 200;
    private int REQUEST_CODE_CAMERA = 100;
    private FunctionConfig functionConfig;

    SelectPicPopupWindow menuWindow;

    private SweetAlertDialog pDialog2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_book);

        InitGalleryFinal();                 //初始化图片选择器

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
        image_url = null;



        preferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = preferences.edit();
        ZXingLibrary.initDisplayOpinion(this);
        Intent intent = getIntent();
        book_id = intent.getStringExtra("bookinfo_id");
        int type = intent.getIntExtra("type",0);
        if (type == 1)
            Init(intent.getStringExtra("bookinfo_id"));
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
                if(image_url == null)
                {
                    Toast.makeText(Updatebook.this, "未上传封面", Toast.LENGTH_SHORT).show();
                }else if(text_ISBN == null)
                {
                    Toast.makeText(Updatebook.this,"未填写ISBN", Toast.LENGTH_SHORT).show();
                }
                else if(!(text_ISBN.length()==10||text_ISBN.length()==13))
                {
                    Toast.makeText(Updatebook.this,"ISBN位数错误", Toast.LENGTH_SHORT).show();
                }
                else if(text_name == null)
                {
                    Toast.makeText(Updatebook.this,"未填写书名", Toast.LENGTH_SHORT).show();
                }
                else if(text_author == null)
                {
                    Toast.makeText(Updatebook.this,"未填写作者", Toast.LENGTH_SHORT).show();
                }
                else if(text_publisher == null)
                {
                    Toast.makeText(Updatebook.this,"未填写出版社", Toast.LENGTH_SHORT).show();
                }
                else if(text_old_price == null)
                {
                    Toast.makeText(Updatebook.this,"未填写出原始价格", Toast.LENGTH_SHORT).show();
                }
                else if(!isDecimal(text_old_price))
                {
                    System.out.println("isDecimal: "+isDecimal(text_old_price));
                    Toast.makeText(Updatebook.this,"原始价格非纯数字", Toast.LENGTH_SHORT).show();
                }
                else if(text_now_price == null)
                {
                    Toast.makeText(Updatebook.this,"未填写出售价", Toast.LENGTH_SHORT).show();
                }
                else if(Float.parseFloat(text_old_price)<Float.parseFloat(text_now_price))
                {
                    Toast.makeText(Updatebook.this,"售价不可高于原价", Toast.LENGTH_SHORT).show();
                }
                else if(text_num == null)
                {
                    Toast.makeText(Updatebook.this,"未填写出售数量", Toast.LENGTH_SHORT).show();
                }
                else if(text_classify == null)
                {
                    Toast.makeText(Updatebook.this,"未填写种类", Toast.LENGTH_SHORT).show();
                }
                else if(text_quality == null)
                {
                    Toast.makeText(Updatebook.this,"未选择质量等级", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    pDialog = new SweetAlertDialog(Updatebook.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("正在上传");
                    pDialog.show();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject result = NewService.updatebook(token,text_ISBN,text_name,text_author,text_publisher,text_old_price,text_now_price,text_num,text_classify,text_quality,text_remark,image_url,book_id);
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = result;
                            System.out.println("上架。。。。"+result);
                            handler.sendMessage(msg);
                        }
                    });
                    thread.start();
                }
            }
        });
        QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("cameral");
                cameraTask();
            }
        });
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuWindow = new SelectPicPopupWindow(Updatebook.this, itemsOnClick);
                //显示窗口
                menuWindow.showAtLocation(Updatebook.this.findViewById(R.id.pic), BOTTOM|CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
            }
        });
    }
    public void reset()
    {
        ISBN.setText(null);
        name.setText(null);
        author.setText(null);
        publisher.setText(null);
        old_price.setText(null);
        now_price.setText(null);
        num.setText(null);
        remark.setText(null);
        cover.setImageResource(R.drawable.addcover);
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

        CoreConfig coreConfig = new CoreConfig.Builder(Updatebook.this, imageloader, theme).setFunctionConfig(functionConfig).build();
        GalleryFinal.init(coreConfig);
    }
    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback=new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int i, List<PhotoInfo> list) {
            pDialog2 = new SweetAlertDialog(Updatebook.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog2.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog2.setTitleText("上传信息中");
            pDialog2.setCancelable(false);
            pDialog2.show();
            final String filepath = list.get(0).getPhotoPath();
            if (list != null) {
                if (i == 100) {
                    System.out.println("openCamera");
                } else if (i == 200) {
                    System.out.println("openGallerySingle");
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject res = NewService.book_cover(new File(filepath), preferences.getString("token", ""));
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = res;
                        System.out.println("result: "+res);
                        Coverhandler.sendMessage(msg);
                    }
                }).start();
            }
        }

        @Override
        public void onHanlderFailure(int i, String s) {
            Toast.makeText(Updatebook.this, "设置头像失败", Toast.LENGTH_SHORT).show();
        }
    };
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
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(Updatebook.this, "解析二维码失败", Toast.LENGTH_LONG).show();
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
    public boolean isDecimal(String s) {
        System.out.println(java.util.regex.Pattern.matches("\\d+\\.*\\d+", s));
        return java.util.regex.Pattern.matches("\\d+\\.*\\d+", s);
    }

    public void Init(final String book_id){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject res = NewService.getbookinfo(preferences.getString("token",null),book_id);
                    Message msg = new Message();
                    msg.obj = res.getJSONObject("data");
                    Bookhandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
