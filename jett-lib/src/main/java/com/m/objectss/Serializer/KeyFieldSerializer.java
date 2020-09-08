
package com.m.objectss.Serializer;



import com.m.objectss.Jett;
import com.m.objectss.JettException;
import com.m.objectss.JettLog;
import com.m.objectss.annotation.KeyField;
import com.m.objectss.io.Input;
import com.m.objectss.io.Output;
import com.m.objectss.model.KeyFieldEntry;
import com.m.objectss.util.Util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.m.objectss.ClassResoverManager.CLASS_ID_NOT_FOUND;


/**
 * 有标记keyObject对象的系列化实现
 * @param <T>
 */
public class KeyFieldSerializer<T> implements ISerializer<T>
{

    HashMap<Field, String> keyFieldMap = new HashMap<>();
    HashMap<String, Field> fieldKeyMap = new HashMap<>();


    Field[] validFields = null;

    public KeyFieldSerializer(Jett kryo, Class type)
    {
        List<Field> allFields = new ArrayList();
        Class nextClass = type;
        while (nextClass != Object.class && nextClass != null)
        {
            Field[] declaredFields = nextClass.getDeclaredFields();
            if (declaredFields != null)
            {
                for (Field f : declaredFields)
                {
                    int modifiers = f.getModifiers();
                    if (Modifier.isStatic(modifiers)) continue;
                    if (!f.isAccessible())
                    {
                        f.setAccessible(true);
                    }
                    allFields.add(f);
                }
            }
            nextClass = nextClass.getSuperclass();
        }

        ArrayList<Field> keyFields = new ArrayList<>();

        for (Field field : allFields)
        {
            Annotation[] annotations = field.getDeclaredAnnotations();
            if (annotations != null)
            {
                for (Annotation a : annotations)
                {
                    if (a instanceof KeyField)
                    {
                        String fieldKey = ((KeyField) a).key();
                        keyFieldMap.put(field, fieldKey);
                        fieldKeyMap.put(fieldKey, field);
                        keyFields.add(field);
                    }
                }
            }
        }
        validFields = keyFields.toArray(new Field[0]);
    }

    public void write(Jett jett, Output output, T object)
    {

        Output outputChunked = new Output(1024);
        for (Field field : validFields)
        {
            Object value = null;
            try
            {
                value = field.get(object);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            if (value == null)
            {
                continue;
            }

            int classId = jett.getClassId(value.getClass());
            if (classId == CLASS_ID_NOT_FOUND)
            {
                throw new JettException("未找到对应的class id:" + value.toString());
            }

            //直接写入
            String fieldKey = keyFieldMap.get(field);
            jett.writeKeyFieldInfo(output, object.getClass(), fieldKey);//写入keyField信息

            jett.writeClassInfo(outputChunked, value);//写入类相关信息
            jett.writeObject(outputChunked, value);//写入内容
            output.writeInt(outputChunked.position() + 1, true);
            output.write(outputChunked.getBuffer(), 0, outputChunked.position());
            outputChunked.clear();
        }
        jett.writeKeyFieldInfo(output, Jett.FieldEndType.class, "");//写入结束信息
    }


    public T read(Jett kryo, Input input, Class<T> type)
    {
        T object = kryo.newInstance(type);

        while (true)
        {
            KeyFieldEntry keyFieldEntry = kryo.readKeyFieldInfo(input, type);
            if (keyFieldEntry.getFieldId() == Jett.KEYFIELD_END_FIELD_ID)
            {
                break;
            }
            String fieldKey = keyFieldEntry.getFieldKey();
            //当前字段内容长度
            int startPosition = input.position();
            int fieldSize = input.readInt(true);
            Field field = fieldKeyMap.get(fieldKey);
            if (field == null)
            {
                //该字段不存在，则跳过
                input.skip(fieldSize);
                continue;
            }

            Object value = null;


            //找出该字段最合适的类，
           Class fieldType = kryo.readClassInfo(input);
           if (fieldType == null)
           {
               JettLog.d(this, "Not found class type from input: fieldKey" + fieldKey+" field:"+ field);
               fieldType = field.getType();
               if (Util.isAbstractClass(fieldType) || fieldType == Object.class)
               {
                   //若为抽象类，则无合适的解析类
                   throw new JettException("No suitable class was found for serialization：fieldkey"+fieldKey + "fieldKey:"+ fieldKey + " field:"+ field);
               }
           }
           else if (Util.isAbstractClass(fieldType))
           {
               JettLog.d(this, "Field not allowed to be declared as abstract types : fieldKey" + fieldKey+" field:"+ field);
               fieldType = field.getType();
               if (Util.isAbstractClass(fieldType) || fieldType == Object.class)
               {
                   //若为抽象类，则无合适的解析类
                   throw new JettException("No suitable class was found for serialization：fieldkey"+fieldKey + "objectType:"+ type + " fieldType:"+ fieldType);
               }
           }

            value = kryo.readObject(input, fieldType);

            if (value != null)
            {
                try
                {
                    field.set(object, value);
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }

            //确认正确读取下一个field
            input.setPosition(startPosition+fieldSize);

        }


        return object;
    }

}
