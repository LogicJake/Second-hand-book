package com.nuaa.book.second_hand_book;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;

/**
 * Created by SCY on 2017/8/8/0008.
 */

public class NewService {
    public static String rooturl = "http://test.logicjake.xyz/book-api/";
    public static String pic_root = "http://test.logicjake.xyz/book-api/pic/";
    public static String getMD5(String message) {
        String md5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");  // 创建一个md5算法对象
            byte[] messageByte = message.getBytes("UTF-8");
            byte[] md5Byte = md.digest(messageByte);              // 获得MD5字节数组,16*8=128位
            md5 = bytesToHex(md5Byte);                            // 转换为16进制字符串
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }
    // 二进制转十六进制
    public static String bytesToHex(byte[] bytes) {
        StringBuffer hexStr = new StringBuffer();
        int num;
        for (int i = 0; i < bytes.length; i++) {
            num = bytes[i];
            if(num < 0) {
                num += 256;
            }
            if(num < 16){
                hexStr.append("0");
            }
            hexStr.append(Integer.toHexString(num));
        }
        return hexStr.toString().toUpperCase();
    }

    public static JSONObject login(String user_name, String user_passwd) {          //登陆请求
        JSONObject jsonObject = null;
        try {
            user_passwd = getMD5(user_passwd);
            System.out.println(user_passwd);
            String path = rooturl+"index.php?_action=postLogin";
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data = "&user_name=" + URLEncoder.encode(user_name, "UTF-8") + "&user_passwd=" + URLEncoder.encode(user_passwd, "UTF-8");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                jsonObject = new JSONObject(baos.toString()).getJSONObject("data").getJSONObject("result");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static int logout(String token){
        int result = 0;
        System.out.println(token);
        try {
            String path = rooturl+"index.php?_action=postLogout&token="+token;
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("GET");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                result = new JSONObject(baos.toString()).getInt("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject signup(String user_name, String user_password) {
        JSONObject jsonObject = null;
        try {
            user_password = getMD5(user_password);
            System.out.println(user_password);
            String path = rooturl+"index.php?_action=postSignup";
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            String data = "&user_name=" + URLEncoder.encode(user_name, "UTF-8") + "&user_passwd=" + URLEncoder.encode(user_password, "UTF-8");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                jsonObject = new JSONObject(baos.toString()).getJSONObject("data").getJSONObject("result");
            }
            else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject getinfo(String token,String type,String key) {
        JSONObject result = null;
        try {
            String path = rooturl+"index.php?_action=getInfo&token="+token;
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            System.out.println(token);
            String data = "&type="+URLEncoder.encode(type, "UTF-8");
            if (type.equals("update_sex")){
                int sex = (key.equals("男"))?1:0;
                data += "&sex="+sex;
            }
            if (type.equals("update_qq_num")){
                data += "&qq_num="+URLEncoder.encode(key, "UTF-8");
            }
            if (type.equals("update_phone_num")){
                data += "&phone_num="+URLEncoder.encode(key, "UTF-8");;
            }
            if (type.equals("update_user_sign")){
                data += "&user_sign="+URLEncoder.encode(key, "UTF-8");
            }
            System.out.println(data);
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入

            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                result = new JSONObject(baos.toString()).getJSONObject("data").getJSONObject("result");
                System.out.println(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONArray getbook(String token) {
        JSONArray res = null;
        try {
            String path = rooturl+"index.php?_action=getBook&token="+token;;
            URL url = new URL(path);
            // 获得连接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            OutputStream os = urlConnection.getOutputStream();
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos);
                res = new JSONObject(baos.toString()).getJSONObject("data").getJSONArray("book");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static JSONArray search(String token,String query){
        JSONArray res = null;
        try {
            String path = rooturl+"index.php?_action=getSearch&token="+token+"&query="+URLEncoder.encode(query, "UTF-8");
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("GET");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                is.close();
                baos.close();
                System.out.println(baos.toString());
                res = new JSONObject(baos.toString()).getJSONArray("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
