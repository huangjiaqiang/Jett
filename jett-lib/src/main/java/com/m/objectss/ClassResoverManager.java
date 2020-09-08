package com.m.objectss;

import android.text.TextUtils;

import com.m.objectss.Serializer.CollectionSerializer;
import com.m.objectss.Serializer.DefaultArraySerializers;
import com.m.objectss.Serializer.DefaultSerializers;
import com.m.objectss.Serializer.FieldSerializer;
import com.m.objectss.Serializer.ISerializer;
import com.m.objectss.Serializer.KeyFieldSerializer;
import com.m.objectss.Serializer.MapSerializer;
import com.m.objectss.Serializer.ObjectFactory;
import com.m.objectss.annotation.KeyClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Project Name: Paper
 * File Name:    ClassResoverManager.java
 * ClassName:    ClassResoverManager
 *
 * Description: 解析器管理类
 *
 * @author jiaqianghuang
 * @date 2020年08月29日 2:56 PM
 *
 *
 */
public class ClassResoverManager
{
    //未发现classid
    final public static int CLASS_ID_NOT_FOUND = -1;

    final public static int CLASS_ID_MASK = 0x0000ffff; //用于获取读取class id

    static ArrayList<RerializerModel> rerializerModels = new ArrayList<>();

    /**
     * classType -- RerializerModel 类型的map,用于通过classType快速查找RerializerModel
     */
    static HashMap<Class, RerializerModel> typeRerializerMap = new HashMap<>();

    /**
     * KeyObject标记对象解析器id开始位置
     */
    static final public int CLASS_ID_KEYOBJECT_START = 1000;
    static public int keyObjectClassIdIndex = CLASS_ID_KEYOBJECT_START;

    //空对象
    static public class NullType{};


    static {

        addDefaultRerializerModel(new RerializerModel(boolean.class, 1, DefaultSerializers.BooleanSerializer.class));
        addDefaultRerializerModel(new RerializerModel(Boolean.class, 2, DefaultSerializers.BooleanSerializer.class));
        addDefaultRerializerModel(new RerializerModel(int.class, 3, DefaultSerializers.IntSerializer.class));
        addDefaultRerializerModel(new RerializerModel(Integer.class, 4, DefaultSerializers.IntSerializer.class));
        addDefaultRerializerModel(new RerializerModel(long.class, 5, DefaultSerializers.LongSerializer.class));
        addDefaultRerializerModel(new RerializerModel(Long.class, 6, DefaultSerializers.LongSerializer.class));
        addDefaultRerializerModel(new RerializerModel(float.class, 7, DefaultSerializers.FloatSerializer.class));
        addDefaultRerializerModel(new RerializerModel(Float.class, 8, DefaultSerializers.FloatSerializer.class));
        addDefaultRerializerModel(new RerializerModel(double.class, 9, DefaultSerializers.DoubleSerializer.class));
        addDefaultRerializerModel(new RerializerModel(Double.class, 10, DefaultSerializers.DoubleSerializer.class));
        addDefaultRerializerModel(new RerializerModel(String.class, 11, DefaultSerializers.StringSerializer.class));
        addDefaultRerializerModel(new RerializerModel(void.class, 12, DefaultSerializers.VoidSerializer.class));
        addDefaultRerializerModel(new RerializerModel(Void.class, 13, DefaultSerializers.VoidSerializer.class));
        addDefaultRerializerModel(new RerializerModel(byte.class, 14, DefaultSerializers.ByteSerializer.class));
        addDefaultRerializerModel(new RerializerModel(Byte.class, 15, DefaultSerializers.ByteSerializer.class));
        addDefaultRerializerModel(new RerializerModel(char.class, 16, DefaultSerializers.CharSerializer.class));
        addDefaultRerializerModel(new RerializerModel(Character.class, 17, DefaultSerializers.CharSerializer.class));
        addDefaultRerializerModel(new RerializerModel(short.class, 18, DefaultSerializers.ShortSerializer.class));
        addDefaultRerializerModel(new RerializerModel(Short.class, 19, DefaultSerializers.ShortSerializer.class));

        addDefaultRerializerModel(new RerializerModel(byte[].class, 20, DefaultArraySerializers.ByteArraySerializer.class));
        addDefaultRerializerModel(new RerializerModel(Byte[].class, 21, DefaultArraySerializers.ByteArraySerializer.class));
        addDefaultRerializerModel(new RerializerModel(int[].class, 22, DefaultArraySerializers.IntArraySerializer.class));
        addDefaultRerializerModel(new RerializerModel(Integer[].class, 23, DefaultArraySerializers.IntArraySerializer.class));
        addDefaultRerializerModel(new RerializerModel(float[].class, 24, DefaultArraySerializers.FloatArraySerializer.class));
        addDefaultRerializerModel(new RerializerModel(Float[].class, 25, DefaultArraySerializers.FloatArraySerializer.class));
        addDefaultRerializerModel(new RerializerModel(long[].class, 26, DefaultArraySerializers.LongArraySerializer.class));
        addDefaultRerializerModel(new RerializerModel(Long[].class, 27, DefaultArraySerializers.LongArraySerializer.class));
        addDefaultRerializerModel(new RerializerModel(short[].class, 28, DefaultArraySerializers.ShortArraySerializer.class));
        addDefaultRerializerModel(new RerializerModel(Short[].class, 29, DefaultArraySerializers.ShortArraySerializer.class));
        addDefaultRerializerModel(new RerializerModel(char[].class, 30, DefaultArraySerializers.CharArraySerializer.class));
        addDefaultRerializerModel(new RerializerModel(Character[].class, 31, DefaultArraySerializers.CharArraySerializer.class));
        addDefaultRerializerModel(new RerializerModel(double[].class, 32, DefaultArraySerializers.DoubleArraySerializer.class));
        addDefaultRerializerModel(new RerializerModel(Double[].class, 33, DefaultArraySerializers.DoubleArraySerializer.class));
        addDefaultRerializerModel(new RerializerModel(boolean[].class, 34, DefaultArraySerializers.BooleanArraySerializer.class));
        addDefaultRerializerModel(new RerializerModel(Boolean[].class, 35, DefaultArraySerializers.BooleanArraySerializer.class));
        addDefaultRerializerModel(new RerializerModel(String[].class, 36, DefaultArraySerializers.StringArraySerializer.class));

        addDefaultRerializerModel(new RerializerModel(NullType.class, 37, DefaultSerializers.NullSerializer.class));

        addDefaultRerializerModel(new RerializerModel(Collection.class, 38, CollectionSerializer.class));
        addDefaultRerializerModel(new RerializerModel(ArrayList.class, 39, CollectionSerializer.class));

        addDefaultRerializerModel(new RerializerModel(Map.class, 40, MapSerializer.class));
        addDefaultRerializerModel(new RerializerModel(HashMap.class, 41, MapSerializer.class));
        addDefaultRerializerModel(new RerializerModel(JSONObject.class, 42, FieldSerializer.class));
        addDefaultRerializerModel(new RerializerModel(JSONArray.class, 43, FieldSerializer.class));

    }

    
    static synchronized void addDefaultRerializerModel(RerializerModel model)
    {
        RerializerModel oldModel = typeRerializerMap.put(model.type, model);
        if (oldModel != null)
        {
            throw new JettException("同一个class只能添加一次:"+oldModel);
        }
        rerializerModels.add(model);
    }

    /**
     * get KryObject annotation ObjectKey value from class
     * @param type
     * @return
     */
    static String getTypeKey(Class type)
    {
        KeyClass annotation = (KeyClass)type.getAnnotation(KeyClass.class);
        String classKey = "";
        if (annotation != null)
        {
            classKey = annotation.key();
        }
        return classKey;
    }

    /**
     * 注册标记型解析器
     * @param type
     * @return
     */
    public static void registerKeyObjectSerializer(Class type)
    {
        if (type == null)
        {
            return;
        }
        if (typeRerializerMap.containsKey(type))
        {
            JettLog.e(ClassResoverManager.class, "重复注册KeyObject类型的解析"+type);
            return;
        }
        addKeyTypeSerializer(type);
    }

    /**
     * 添加keyObject的类解析
     * @param type
     */
    static RerializerModel addKeyTypeSerializer(Class type)
    {
        String classKey = getTypeKey(type);
        if (TextUtils.isEmpty(classKey))
        {
            throw new RuntimeException("不支持添加未声明KryoObject的类:"+type.toString());
        }
        RerializerModel model = new RerializerModel(type, keyObjectClassIdIndex++, KeyFieldSerializer.class, classKey);
        addDefaultRerializerModel(model);
        return model;
    }

    /**
     * 获取classId
     * @param type
     * @return
     */
    public static int getClassId(Class type)
    {
        RerializerModel model = getRerialiseModel(type);

        if (model != null)
        {
            return model.classId;
        }
        return CLASS_ID_NOT_FOUND;
    }

    /**
     * 获取classKey
     * @param type
     * @return
     */
    public static String getClassKey(Class type)
    {
        RerializerModel model = getRerialiseModel(type);
        if (model != null)
        {

            return model.classKey;
        }
        return null;
    }
    /**
     * 获取class
     * @param classKey
     * @return
     */
    public static Class getClassType(String classKey)
    {
        RerializerModel model = getRerialiseModel(classKey);
        if (model != null)
        {

            return model.type;
        }
        return null;
    }
    /**
     * 获取class
     * @param classId
     * @return
     */
    public static Class getClassType(int classId)
    {
        RerializerModel model = getRerialiseModel(classId);
        if (model != null)
        {

            return model.type;
        }
        return null;
    }

    /**
     * 获取解析对象
     * @param jett
     * @param type
     * @param <T>
     * @return
     */
    public static  <T> ISerializer getSerirlizer(Jett jett, Class<T> type)
    {

        RerializerModel rerializerModel = getRerialiseModel(type);;

        ISerializer<T> serializer = ObjectFactory.makeSerializer(jett, rerializerModel.ResoverClass, type);
        rerializerModel.serializer = serializer;
        return serializer;
    }

    /**
     * 获取解析对象
     * @param type
     * @return
     */
    static  RerializerModel getRerialiseModel(Class type)
    {
        if (type == null)
        {
            return null;
        }
        //优先取类型一致的
        RerializerModel rerializerModel  = typeRerializerMap.get(type);
        if (rerializerModel != null)
        {
            return rerializerModel;
        }

        //其次取其父类解析对象
        for (RerializerModel model : rerializerModels)
        {
            if (model.type.isAssignableFrom(type))
            {
                rerializerModel = model;
                return rerializerModel;
            }
        }

        //创建新的解析对象
        return addKeyTypeSerializer(type);
    }

    /**
     * 获取解析对象
     * @param classId
     * @return
     */
    static  RerializerModel getRerialiseModel(int classId)
    {
        RerializerModel rerializerModel = null;
        for (RerializerModel model : rerializerModels)
        {
            if (model.classId == classId)
            {
                rerializerModel = model;
                return rerializerModel;
            }
        }
        return null;
    }


    /**
     * 获取解析对象
     * @param classKey
     * @return
     */
    static  RerializerModel getRerialiseModel(String classKey)
    {
        if (TextUtils.isEmpty(classKey))
        {
            return null;
        }
        RerializerModel rerializerModel = null;
        for (RerializerModel model : rerializerModels)
        {
            if (classKey.equals(model.classKey))
            {
                rerializerModel = model;
                return rerializerModel;
            }
        }
        return null;
    }


    /**
     * 获取构造器
     * @param jett
     * @param type
     * @param <T>
     * @return
     */
    public static <T> Constructor<T> getConstructor(Jett jett, Class<T> type)
    {
        RerializerModel rerializerModel = getRerialiseModel(type);
        if (rerializerModel == null)
        {
            return null;
        }
        if (rerializerModel.constructor == null)
        {
            Constructor<T> objectConstructor = null;
            try
            {
                objectConstructor = type.getConstructor((Class[]) null);
                objectConstructor.setAccessible(true);
            }
            catch (Exception ex)
            {
                try
                {
                    objectConstructor = type.getDeclaredConstructor((Class[]) null);
                    objectConstructor.setAccessible(true);
                }
                catch (NoSuchMethodException e)
                {
                    e.printStackTrace();
                }
            }

            if (objectConstructor != null)
            {
                rerializerModel.constructor = objectConstructor;
            }

        }
        return rerializerModel.constructor;
    }

    /**
     * Project Name: Paper
     * File Name:    KeyObjectModel.java
     * ClassName:    KeyObjectModel
     *
     * Description: 解析器对象
     *
     * @author jiaqianghuang
     * @date 2020年08月29日 3:00 PM
     *
     *
     */
    static class RerializerModel<T>
    {
        int classId; //类id
        String classKey; //类key ，仅keyfield类型的class才有
        Class<T> type;//类
        Class<? extends ISerializer> ResoverClass;//对应的解析类

        //列表化器
        ISerializer<T> serializer;

        Constructor<T> constructor;//构造器

        public RerializerModel(Class<T> type, int classId, Class<? extends ISerializer> resoverClass)
        {
            this.classId = classId;
            this.type = type;
            ResoverClass = resoverClass;
        }

        public RerializerModel(Class<T> type, int classId,  Class<? extends ISerializer> resoverClass, String classKey)
        {
            this.classId = classId;
            this.classKey = classKey;
            this.type = type;
            ResoverClass = resoverClass;
        }

        @Override
        public String toString()
        {
            return "model{" + "id=" + classId + ", classKey='" + classKey + '\'' + ", type=" + type + ", reClass=" + ResoverClass + ", serializer=" + serializer + ", constructor=" + constructor + '}';
        }
    }
}
