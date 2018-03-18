package repositories;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import exceptions.SerializeException;
import models.Tag;
import utils.JsonSerializer;

public class TagRepository {
	private static final String TAG_PATH = "tags.json";
	private static TagRepository tagRepository;
	private Map<String, Tag> tags;
	private JsonSerializer serializer;
	public TagRepository() {
		this.serializer = new JsonSerializer();
		this.tags = new HashMap<>();
	}
	
	public static TagRepository getInstance() {
		if (tagRepository == null) {
			tagRepository = new TagRepository();
		}
		
		return tagRepository;
	}
	
	public Tag addTag(String name) {
		Tag tag = new Tag(name);
		this.tags.put(tag.getName(), tag);
		
		return tag;
	}
	
	public Tag getTagByName(String name) {
		if (!this.tags.containsKey(name)) {
			return null;
		}
		
		return this.tags.get(name);
	}
	
//	public void serialize() throws IOException {
//		File file = new File(TAG_PATH);
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//		String jsonTags = gson.toJson(this.tags);
//
//		try (PrintStream ps = new PrintStream(file)) {
//			file.createNewFile();
//			ps.println(jsonTags);
//		}
//	}
	public void exportTag() throws SerializeException {
		this.serializer.serialize(this.tags, TAG_PATH);
	}
	public void deserialize() throws FileNotFoundException {
		File file = new File(TAG_PATH);
		Gson gson = new GsonBuilder().create();
		StringBuilder sb = new StringBuilder();

		try (Scanner sc = new Scanner(file)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				sb.append(line);
			}
		}

		Map<String, Tag> map = gson.fromJson(sb.toString(), new TypeToken<Map<String, Tag>>() {
		}.getType());
		
		this.tags = map;
	}

	public int getLastId() {
		if (this.tags == null || this.tags.size() == 0) {
			return 0;
		}
		
		return this.tags
				.values()
				.stream()
				.sorted((t1, t2) -> Integer.compare(t2.getId(), t1.getId()))
				.findFirst()
				.get()
				.getId();
	}
}
