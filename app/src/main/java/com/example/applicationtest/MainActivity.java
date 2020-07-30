package com.example.applicationtest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
