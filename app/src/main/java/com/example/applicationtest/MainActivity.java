package com.example.applicationtest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Xml;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        ImageView imageView = new ImageView(this);
        Glide.with(this).load("").into(imageView);

    }

    /**
     * 【Pull解析器解析XML文件】
     **/
    public static List<Person> readXmlByPull(InputStream inputStream) {
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(inputStream, "UTF-8");
            int eventType = parser.getEventType();
            Person currenPerson = null;
            List<Person> persons = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:/**【文档开始事件】**/
                        persons = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:/**【元素（即标签）开始事件】**/
                        String name = parser.getName();
                        if (name.equals("person")) {
                            currenPerson = new Person();
                            currenPerson.setId((parser.getAttributeValue(null, "id")));
                        } else if (currenPerson != null) {
                            if (name.equals("name")) {/**【判断标签名（元素名）是否为name】**/
                                currenPerson.setName(parser.nextText());/**【如果后面是text元素，即返回它的值】**/
                            } else if (name.equals("age")) {
                                currenPerson.setAge(parser.nextText());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:/**【元素结束事件】**/
                        if (parser.getName().equals("person") && currenPerson != null) {
                            persons.add(currenPerson);
                            currenPerson = null;
                        }
                        break;
                }
                eventType = parser.next();
            }
            inputStream.close();
            return persons;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void GsonParser() {
        Gson gson = new Gson();
        String json = "";
        Person person = gson.fromJson(json, Person.class);
        String name = person.getName();

    }

    /**
     * Get方式提交数据到服务器
     */
    public void LoginByGet(View view) {
        EditText et_name = new EditText(this);
        EditText et_pwd = new EditText(this);

        String name = et_name.getText().toString().trim();
        String pwd = et_pwd.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "用户名密码不能为空", Toast.LENGTH_LONG).show();
        } else {
            // 模拟http请求，提交数据到服务器
            String path = "http://169.254.168.71:8080/web/LoginServlet?username="
                    + name + "&password=" + pwd;
            try {
                URL url = new URL(path);
                // 2.建立一个http连接
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                // 3.设置一些请求方式
                conn.setRequestMethod("GET");// 注意GET单词字幕一定要大写
                conn.setRequestProperty(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");

                int code = conn.getResponseCode(); // 服务器的响应码 200 OK //404 页面找不到
                // // 503服务器内部错误
                if (code == 200) {
                    InputStream is = conn.getInputStream();
                    // 把is的内容转换为字符串
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while ((len = is.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                    }
                    String result = new String(bos.toByteArray());
                    is.close();
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this, "请求失败，失败原因: " + code, Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "请求失败，请检查logcat日志控制台", Toast.LENGTH_LONG).show();
            }

        }


    }

    /**
     * POST方式提交
     */

    @SuppressLint("WrongConstant")
    public void LoginByPost(View view) {
        String name = new EditText(this).getText().toString();
        String pwd = new EditText(this).getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "用户名密码不能为空", Toast.LENGTH_LONG).show();
        } else {
            try {
                String path = "http://169.254.168.71:8080/web/LoginServlet";
                // 1.定义请求url
                URL url = new URL(path);
                // 2.建立一个http的连接
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                // 3.设置一些请求的参数
                conn.setRequestMethod("POST");
                conn.setRequestProperty(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                String data = "username=" + name + "&password=" + pwd;
                conn.setRequestProperty("Content-Length", data.length() + "");
                conn.setConnectTimeout(5000);//设置连接超时时间
                conn.setReadTimeout(5000); //设置读取的超时时间

                // 4.一定要记得设置 把数据以流的方式写给服务器
                conn.setDoOutput(true); // 设置要向服务器写数据
                conn.getOutputStream().write(data.getBytes());

                int code = conn.getResponseCode(); // 服务器的响应码 200 OK //404 页面找不到
                // // 503服务器内部错误
                if (code == 200) {
                    InputStream is = conn.getInputStream();
                    // 把is的内容转换为字符串
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while ((len = is.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                    }
                    String result = new String(bos.toByteArray());
                    is.close();
                    Toast.makeText(this, result, 0).show();

                } else {
                    Toast.makeText(this, "请求失败，失败原因: " + code, 0).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "请求失败，请检查logcat日志控制台", 0).show();
            }
        }

    }




}
