
package com.m.objectss.Serializer;

import com.m.objectss.Jett;
import com.m.objectss.io.Input;
import com.m.objectss.io.Output;

import java.util.Iterator;
import java.util.Map;

public class MapSerializer implements ISerializer<Map>
{


    public MapSerializer(Jett jett, Class type)
    {

    }

    public void write(Jett jett, Output output, Map map)
    {
        int length = map.size();
        output.writeVarInt(length, true);
        for (Iterator iter = map.entrySet().iterator(); iter.hasNext();)
        {
            Map.Entry entry = (Map.Entry)iter.next();
            jett.writeClassInfo(output, entry.getKey());
            jett.writeObject(output, entry.getKey());
            jett.writeClassInfo(output, entry.getValue());
            jett.writeObject(output, entry.getValue());
        }
    }


    public Map read(Jett jett, Input input, Class<Map> type)
    {
        Map map = jett.newInstance(type);
        int length = input.readVarInt(true);
        for (int i = 0; i < length; i++)
        {
            Class keyType = jett.readClassInfo(input);
            Object key = jett.readObject(input, keyType);
            Class valueType = jett.readClassInfo(input);
            Object value = jett.readObject(input, valueType);
            map.put(key, value);
        }
        return map;
    }


}
