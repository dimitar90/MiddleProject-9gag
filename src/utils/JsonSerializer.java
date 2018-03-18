package utils;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sql.rowset.serial.SerialException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import exceptions.DeserializeException;

public class JsonSerializer {
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

	public <T> T deserialize(Class<T> clazz, String fileName) throws DeserializeException {
		try {
			String json = fileIO.read(fileName);
			T map = gson.fromJson(json, new TypeToken<T>() {}.getType());
			
			return map;
		} catch (FileNotFoundException e) {
			throw new DeserializeException(String.format(DESERIALIZE_FAILED_MESSAGE, fileName));
		}
	}
}
