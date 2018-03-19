package utils;


import javax.sql.rowset.serial.SerialException;

import exceptions.DeserializeException;
import exceptions.SerializeException;

public interface Serializer {
	<T> void serialize(T t, String fileName) throws SerializeException, SerialException;
	
	<T> T deserialize(String fileName) throws DeserializeException;
}
