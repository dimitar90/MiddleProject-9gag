package utils;


import java.util.Map;

import javax.sql.rowset.serial.SerialException;

import exceptions.DeserializeException;
import exceptions.SerializeException;

public interface Serializer {
	<T> void serialize(T t, String fileName) throws SerializeException, SerialException;
	
	<T, U> Map<T, U> deserialize(String fileName) throws DeserializeException;
}
