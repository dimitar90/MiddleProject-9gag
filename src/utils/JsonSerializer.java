package utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.sql.rowset.serial.SerialException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import commands.Command;
import exceptions.DeserializeException;
import models.Comment;
import models.User;

public class JsonSerializer implements Serializer{
	private static final String SERIALIZE_FAILED_MESSAGE = "Serializing a %d file failed!";
	private static final String DESERIALIZE_FAILED_MESSAGE = "Deserializing a %d file failed!!";

	private Gson gson;
	private FileIO fileIO;

	public JsonSerializer() {
		this.gson = new GsonBuilder().setPrettyPrinting().create();
		this.fileIO = new FileIO();
	}

	public <T> void serialize(T t, String fileName) throws SerialException {
		String json = gson.toJson(t);

		try {
			fileIO.write(json, fileName);
		} catch (IOException e) {
			throw new SerialException(String.format(SERIALIZE_FAILED_MESSAGE, fileName));
		}
	}

	public <T,U> Map<T, U> deserialize(final String fileName) throws DeserializeException {
		try {
			String json = fileIO.read(fileName);
			Map<T, U> map = gson.fromJson(json, new TypeToken<Map<U, T>>() {}.getType());
			
			return map;
		} catch (FileNotFoundException e) {
			throw new DeserializeException(String.format(DESERIALIZE_FAILED_MESSAGE, fileName));
		}
	}
}
