package com.example.jett;

import android.content.Context;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.m.objectss.ClassResoverManager;
import com.m.objectss.Jett;
import com.m.objectss.JettLog;
import com.m.objectss.annotation.KeyClass;
import com.m.objectss.annotation.KeyField;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import io.paperdb.Book;
import io.paperdb.Paper;


/**
 * Project Name: Paper
 * File Name:    JettTest.java
 * ClassName:    JettTest
 *
 * Description: TODO 描述必须有.
 *
 * @author jiaqianghuang
 * @date 2020年09月01日 3:13 PM
 *
 *
 */
class KryoTest
{
    static Kryo kryo = null;
    static String filePath  = null;
    static void init(Context context)
    {
        if (kryo == null)
        {
            Paper.init(context);
            kryo = new Kryo();
            kryo.register(TestModel.class);
            File dir = new File(context.getFilesDir().getPath() + "/kryo");
            dir.mkdir();
            filePath = dir.getAbsoluteFile() + "/file.txt";
        }
    }

    public static class BigFieldModel{
        int age = 43;
//        ArrayList<String> citys = new ArrayList<>();
        long bb = 3432;

        void fill()
        {
            for (int i=0;i<500;i++)
            {
//                citys.add("hello world!"+i);
            }
        }
    }


    public static void writeTest(Context context)
    {
        init(context);
        TestModel object = new TestModel();
        object.someModel = new TestModel();
        object.someModel.nickname = "kimwey";
        ArrayList list = new ArrayList();
//        list.add(object);
        for (int i = 0; i<134; i++)
        {
            TestModel t = new TestModel();
            list.add(t);
        }

        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put("city", "beijing");
            JSONArray students = new JSONArray();
            students.put("张三");
            students.put("李三");
            students.put("五三");
            students.put("9527");
            jsonObject.put("students", students);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        long start = System.currentTimeMillis();


        BigFieldModel bigFieldModel = new BigFieldModel();
        bigFieldModel.fill();
        Paper.book().write("paper_test", bigFieldModel);
        JettLog.d(KryoTest.class, "write Test data , time:%d", System.currentTimeMillis() - start);
    }


    public static void readTest(Context context)
    {
        init(context);


        ClassResoverManager.registerKeyObjectSerializer(TestModel.class);
        long start = System.currentTimeMillis();
        Object object = Paper.book().read("paper_test");
//        Object object2 = kryo.readObject(input, ArrayList.class);
//        JettLog.d(KryoTest.class, "read test, listSize:%d", ((ArrayList)object).size());
        JettLog.d(KryoTest.class, "read test,  time:%s",  System.currentTimeMillis() - start);
    }


    @KeyClass(key = "some_model")
    static final public class TestModel
    {
        //        @KryoField(fieldKey = "value")
        //        private int value = 569;
        //
        @KeyField(key = "nickName")
        private String nickname = new String("john");
        //
        //        @KryoField(fieldKey = "json")
        //        private JSONObject jsonObject = new JSONObject();
        //
        @KeyField(key = "someModel")
        public TestModel someModel = null;
        public int age = 456;

        @KeyField(key = "map")
        public HashMap<String, Integer> hashMap = new HashMap<>();

        @KeyField(key = "citys")
        public String[] citys = new String[]{"beijing", "xiamen"};
//
//        @KeyField(key = "peoplename")
//        public ArrayList<TestModel> mPeopleNames ;

        public TestModel()
        {

            hashMap.put("beijing", 010);
            hashMap.put(null, 34);
            hashMap.put("null", null);
            //            this.value = value;
        }

        @Override
        public String toString()
        {
            return "TestModel{" + "nickname='" + nickname + '\'' + ", someModel=" + someModel + ", age=" + age + ", hashMap=" + hashMap + ", citys=" + Arrays
                    .toString(citys) + '}';
        }
    }

}
