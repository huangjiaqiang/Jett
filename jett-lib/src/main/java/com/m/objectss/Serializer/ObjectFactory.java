package com.m.objectss.Serializer;


import com.m.objectss.Jett;

import static com.m.objectss.util.Util.className;

/**
 * Project Name: Paper
 * File Name:    ObjectFactory.java
 * ClassName:    ObjectFactory
 *
 * Description: 对象构造.
 *
 * @author jiaqianghuang
 * @date 2020年08月31日 6:54 PM
 *
 *
 */
public class ObjectFactory
{
    /** Creates a new instance of the specified serializer for serializing the specified class. Serializers must have a zero
     * argument constructor or one that takes (Kryo), (Class), or (Kryo, Class). */
    public static ISerializer makeSerializer (Jett kryo, Class<? extends ISerializer> serializerClass, Class<?> type) {
        try {
            try {
                return serializerClass.newInstance();
            } catch (InstantiationException ex1) {
                try {
                    return serializerClass.getConstructor(Jett.class, Class.class).newInstance(kryo, type);

                } catch (NoSuchMethodException ex2) {
                    try {
                        return serializerClass.getConstructor(Jett.class).newInstance(kryo);

                    } catch (NoSuchMethodException ex3) {
                        return serializerClass.getConstructor(Class.class).newInstance(type);
                    }
                }
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException(
                    "Unable to create serializer \"" + serializerClass.getName() + "\" for class: " + className(type), ex);
        }

    }
}
