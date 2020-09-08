
package com.m.objectss.Serializer;



import com.m.objectss.Jett;
import com.m.objectss.io.Input;
import com.m.objectss.io.Output;


public interface ISerializer<T> {
	
	void write(Jett kryo, Output output, T object);

	T read(Jett kryo, Input input, Class<T> type);
	
}
