
package com.m.objectss.Serializer;



import com.m.objectss.Jett;
import com.m.objectss.io.Input;
import com.m.objectss.io.Output;

import java.util.Collection;

public class CollectionSerializer implements ISerializer<Collection>
{


    public CollectionSerializer(Jett jett, Class type)
    {

    }

    public void write(Jett jett, Output output, Collection collection)
    {
        int length = collection.size();
        output.writeVarInt(length, true);
        for (Object element : collection)
        {
            jett.writeClassInfo(output, element);
            jett.writeObject(output, element);
        }
    }


    public Collection read(Jett jett, Input input, Class<Collection> type)
    {
        Collection collection = jett.newInstance(type);
        int length = input.readVarInt(true);
        for (int i = 0; i < length; i++)
        {
            Class elementType = jett.readClassInfo(input);
            Object element = jett.readObject(input, elementType);
            collection.add(element);
        }
        return collection;
    }


}
