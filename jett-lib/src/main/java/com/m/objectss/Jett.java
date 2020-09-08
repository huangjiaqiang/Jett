package com.m.objectss;



import android.text.TextUtils;

import com.m.objectss.Serializer.ISerializer;
import com.m.objectss.io.Input;
import com.m.objectss.io.Output;
import com.m.objectss.model.KeyFieldEntry;
import com.m.objectss.util.Util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import static com.m.objectss.ClassResoverManager.CLASS_ID_KEYOBJECT_START;
import static com.m.objectss.ClassResoverManager.CLASS_ID_MASK;
import static com.m.objectss.JettLog.TRACE;


/**
 * Project Name: Paper
 * File Name:    John.java
 * ClassName:    John
 *
 * Description: 系列化主入口
 *
 * @author jiaqianghuang
 * @date 2020年08月29日 2:02 PM
 *
 *
 */
public class Jett
{

    static public final byte NULL = 0;

    //KeyField读取结束的类型
    static public class FieldEndType{};
    //用于标记keyObject的结束位置
    static public final int KEYFIELD_END_FIELD_ID = 9999;


    /**
     * 默认的解析器对应的classid最大值
     */
    static final int CLASS_ID_DEFAULT_MAX = CLASS_ID_KEYOBJECT_START -1;


    private int depth, maxDepth = Integer.MAX_VALUE;
    private boolean autoReset = true;


    /**
     * 当前写入，或读取对象的上下文（用于保存当前读写对象classid，classKey信息）
     */
    static HashMap<Integer, String> mClassContext = new HashMap<>();

    /**
     * 当前写入，或读取对象的上下文（用于保存当前读写对象fieldId，fieldKey信息）
     */
    static HashMap<String, HashMap<Integer, String>> mFieldContext = new HashMap<>();


    static {

    }

    /**
     * 用于保存class key, class id
     */
    static final HashMap<String, Integer> classKeyIdMap = new HashMap<>();




    /** Writes an object using the registered serializer. */
    public void writeObject (Output output, Object object) {
        if (output == null) throw new IllegalArgumentException("output cannot be null.");
        beginObject();
        try {
            if (TRACE || depth == 1) log("Write", object);

            if (object == null)
            {
                //若对象为完则无需写入
                return;
            }
            getSerializer(object.getClass()).write(this, output, object);
        } finally {
            if (--depth == 0 && autoReset) reset();
        }
    }

    /** Reads an object using the registered serializer. */
    public <T> T readObject (Input input, Class<T> type) {
        if (input == null) throw new IllegalArgumentException("input cannot be null.");
        if (type == null) throw new IllegalArgumentException("type cannot be null.");
        beginObject();
        try {
            T object;
            object = (T)getSerializer(type).read(this, input, type);
            return object;
        } finally {
            if (--depth == 0 && autoReset) reset();
        }
    }

    /**
     * 开始写入对象
     * @param output
     * @param object
     */
    public void write (Output output, Object object)
    {
        if (output == null) throw new IllegalArgumentException("output cannot be null.");
        if (object == null) throw new IllegalArgumentException("object cannot be null.");
        beginObject();
        try {
            if (TRACE || depth == 1) log("Write", object);
            writeClassInfo(output, object);
            writeObject(output, object);
        } finally {
            if (--depth == 0 && autoReset) reset();
        }
    }

    /**
     * 读取对象
     * @param input
     */
    public <T> T read (Input input)
    {
        if (input == null) throw new IllegalArgumentException("input cannot be null.");
        beginObject();
        try {
            T object;
            Class type = readClassInfo(input);
            object = (T) readObject(input, type);
            return object;
        } finally {
            if (--depth == 0 && autoReset) reset();
        }
    }

    /**
     * 读取对象
     * @param input
     */
    public <T> T read (Input input, Class type)
    {
        if (input == null) throw new IllegalArgumentException("input cannot be null.");
        beginObject();
        try {
            T object;
            Class readType = readClassInfo(input);
            if (type == null)
            {
                type = readType;
            }
            object = (T) readObject(input, type);
            return object;
        } finally {
            if (--depth == 0 && autoReset) reset();
        }
    }

    /**
     * 写入类信息
     * @param output
     * @param object
     */
    public void writeClassInfo(Output output, Object object)
    {
        Class objectType = null;
        if (object == null)
        {
            objectType = ClassResoverManager.NullType.class;
        }else
        {
            objectType = object.getClass();
        }
        writeClassInfo(output, objectType);
    }

    /**
     * 写入类信息
     * @param output
     * @param classType
     */
    public void writeClassInfo(Output output, Class classType)
    {
        Class objectType = classType;

        int classId = getClassId(objectType);
        boolean isNeedWirteClassKey = !mClassContext.containsKey(classId) && classId > CLASS_ID_DEFAULT_MAX;;

        int fieldInfo = 0;
        fieldInfo = fieldInfo | (classId  & CLASS_ID_MASK); //记录类型id
        String classKey = "";
        if (isNeedWirteClassKey)
        {
            classKey = getClassKey(objectType);
            if (TextUtils.isEmpty(classKey))
            {
                throw new JettException("未找到对应的class key:" + classType);
            }
            mClassContext.put(classId, classKey);
        }
        output.writeInt(fieldInfo, true);
        if (!TextUtils.isEmpty(classKey))
        {
            output.writeString(classKey);
            JettLog.d(this, "write class info, classId:%d, classKey:%s, position:%d", classId, classKey, output.position());
        }
    }

    /**
     * 读取类信息
     * @param input
     * @return
     */
    public Class readClassInfo(Input input)
    {
        int fieldInfo = input.readInt(true);

        int classId = (fieldInfo & CLASS_ID_MASK); //读取类型信息
        String classKey = null;
        if (classId > CLASS_ID_DEFAULT_MAX)
        {
            classKey = mClassContext.get(classId);
            if (TextUtils.isEmpty(classKey))
            {
                classKey = input.readString();
                mClassContext.put(classId, classKey);
            }
        }

        Class classType = null;
        if (!TextUtils.isEmpty(classKey))
        {
            classType = getClassType(classKey);
            JettLog.d(this, "read class info, classId:%d, classKey:%s, position:%d", classId, classKey, input.position());
        }else
        {
            classType = getClassType(classId);
        }
        return classType;
    }


    /**
     * 写入keyField信息(fieldId, fieldKey)
     * @param output
     * @param type
     * @param fieldKey
     */
    public void writeKeyFieldInfo(Output  output, Class type, String fieldKey)
    {
        if (type == FieldEndType.class)
        {
            output.writeInt(KEYFIELD_END_FIELD_ID, true);
            return;
        }

        if (TextUtils.isEmpty(fieldKey))
        {
            return;
        }
        String classKey = getClassKey(type);
        if (TextUtils.isEmpty(classKey))
        {
            throw new JettException("未找到对应的classKey:"+type);
        }
        HashMap<Integer, String> classMap = mFieldContext.get(classKey);
        if (classMap == null)
        {
            classMap = new HashMap<>();
            mFieldContext.put(classKey, classMap);
        }

        int fieldId = 0;
        for (Iterator<Map.Entry<Integer, String>> iterable = classMap.entrySet().iterator();iterable.hasNext();)
        {
            Map.Entry<Integer, String> entry = iterable.next();
            if (fieldKey.equals(entry.getValue()))
            {
                fieldId = entry.getKey();
                break;
            }
        }

        if (fieldId == 0)
        {
            fieldId = classMap.size()+1;
            classMap.put(fieldId, fieldKey);
            //需要写入fieldId　＋　fieldKey
            output.writeInt(fieldId, true);
            output.writeString(fieldKey);
            JettLog.d(this, "write key field, classId:%d, classKey:%s, position:%d", fieldId, fieldKey, output.position());
        }
        else
        {
            //仅要写入fieldId
            output.writeInt(fieldId, true);
        }
    }

    /**
     * 读取keyField信息(fieldId, fieldKey)
     * @param input
     * @param type
     */
    public KeyFieldEntry readKeyFieldInfo(Input input, Class type)
    {
        int fieldId = input.readInt(true);
        if (fieldId == KEYFIELD_END_FIELD_ID)
        {
            return new KeyFieldEntry(fieldId, "");
        }

        String classKey = getClassKey(type);
        if (TextUtils.isEmpty(classKey))
        {
            throw new JettException("未找到对应的classKey:"+type);
        }
        HashMap<Integer, String> classMap = mFieldContext.get(classKey);
        if (classMap == null)
        {
            classMap = new HashMap<>();
            mFieldContext.put(classKey, classMap);
        }

        String fieldKey = classMap.get(fieldId);

        if (TextUtils.isEmpty(fieldKey))
        {
            fieldKey = input.readString();
            classMap.put(fieldId, fieldKey);
            JettLog.d(this, "read key field, classId:%d, classKey:%s, position:%d", fieldId, fieldKey, input.position());
        }

        return new KeyFieldEntry(fieldId, fieldKey);
    }

    private void beginObject ()
    {
        if (depth == maxDepth) throw new JettException("Max depth exceeded: " + depth);
        depth++;
    }

    /**
     * 记录classkey　和 id
     * @param classId
     */
    public void cacheClassKeyAndId(int classId, String classKey)
    {
        mClassContext.put(classId, classKey);
    }

    public void reset () {
        depth = 0;
        mClassContext.clear();
        mFieldContext.clear();
        if (TRACE) log("jett", "Object graph complete.");
    }

    /**
     * 反射创建对象
     * @param type
     * @param <T>
     * @return
     */
    public <T> T newInstance(final Class<T> type)
    {
        Constructor<T> objectConstructor = ClassResoverManager.getConstructor(this, type);
        if (objectConstructor != null)
        {
            try
            {
                return objectConstructor.newInstance();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch (InstantiationException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

    /** Returns true if the specified type is final. Final types can be serialized more efficiently because they are
     * non-polymorphic.
     * <p>
     * This can be overridden to force non-final classes to be treated as final. Eg, if an application uses ArrayList extensively
     * but never uses an ArrayList subclass, treating ArrayList as final could allow FieldSerializer to save 1-2 bytes per
     * ArrayList field. */
    public boolean isFinal (Class type)
    {
        if (type == null) throw new IllegalArgumentException("type cannot be null.");
        if (type.isArray()) return Modifier.isFinal(Util.getElementClass(type).getModifiers());
        return Modifier.isFinal(type.getModifiers());
    }

    public ISerializer getSerializer(Class<?> aClass)
    {
        return ClassResoverManager.getSerirlizer(this, aClass);
    }

    public int getClassId(Class type)
    {
        return ClassResoverManager.getClassId(type);
    }

    public String getClassKey(Class type)
    {
        return ClassResoverManager.getClassKey(type);
    }

    public Class getClassType(String classKey)
    {
        return ClassResoverManager.getClassType(classKey);
    }

    public Class getClassType(int classId)
    {
        return ClassResoverManager.getClassType(classId);
    }
    
    static void log(String tag, Object obj)
    {
        JettLog.d(tag, obj == null ? "null" : obj.toString());
    }
}
