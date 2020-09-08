
package com.m.objectss.Serializer;

import com.m.objectss.Jett;
import com.m.objectss.JettException;
import com.m.objectss.JettLog;
import com.m.objectss.io.Input;
import com.m.objectss.io.Output;
import com.m.objectss.util.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.m.objectss.ClassResoverManager.CLASS_ID_NOT_FOUND;

/**
 * 根据对象的field来系列化对象
 * @param <T>
 */
public class FieldSerializer<T> implements ISerializer<T>
{
    List<Field> validFields = new ArrayList<>();

    public FieldSerializer(Jett jett, Class type)
    {
        Class nextClass = type;
        while (nextClass != Object.class)
        {
            Field[] declaredFields = nextClass.getDeclaredFields();
            if (declaredFields != null)
            {
                for (Field field : declaredFields)
                {
                    int modifiers = field.getModifiers();
                    if (Modifier.isTransient(modifiers))
                    {
                        continue;
                    }
                    if (Modifier.isStatic(modifiers))
                    {
                        continue;
                    }

                    if (!field.isAccessible())
                    {
                        try
                        {
                            field.setAccessible(true);
                        }
                        catch (AccessControlException ex)
                        {
                            continue;
                        }
                    }

                    validFields.add(field);
                }
            }
            nextClass = nextClass.getSuperclass();
        }

        Collections.sort(validFields, new Comparator<Field>()
        {
            @Override
            public int compare(Field o1, Field o2)
            {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    public void write(Jett jett, Output output, T object)
    {
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
                jett.writeClassInfo(output, null);
                jett.writeObject(output, null);
                continue;
            }

            Class parseClass = value.getClass();
            int classId = jett.getClassId(parseClass);
            if (classId == CLASS_ID_NOT_FOUND)
            {
                parseClass = field.getType();
                classId = jett.getClassId(parseClass);
            }

            if (classId == CLASS_ID_NOT_FOUND )
            {
                //无法识别的class type
                jett.writeClassInfo(output, null);
                jett.writeObject(output, null);
                continue;
            }
            else
            {
                jett.writeClassInfo(output, parseClass);
                jett.writeObject(output, value);
            }
        }
    }


    public T read(Jett jett, Input input, Class<T> type)
    {
        T object = jett.newInstance(type);
        for (Field field : validFields)
        {
            //找出该字段最合适的类，
            Class fieldType = jett.readClassInfo(input);
            if (fieldType == null)
            {
                JettLog.d(this, "Not found class type from input: type" + type+" field:"+ field);
                fieldType = field.getType();
                if (Util.isAbstractClass(fieldType) || fieldType == Object.class)
                {
                    //若为抽象类，则无合适的解析类
                    throw new JettException("No suitable class was found for serialization：type" + type+" field:"+ field);
                }
            }
            else if (Util.isAbstractClass(fieldType))
            {
                JettLog.d(this, "Field not allowed to be declared as abstract types : type" + type+" field:"+ field);
                fieldType = field.getType();
                if (Util.isAbstractClass(fieldType) || fieldType == Object.class)
                {
                    //若为抽象类，则无合适的解析类
                    throw new JettException("No suitable class was found for serialization：type" + type+" field:"+ field);
                }
            }

            Object value = jett.readObject(input, fieldType);
            try
            {
                field.set(object, value);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }

        return object;
    }


}
