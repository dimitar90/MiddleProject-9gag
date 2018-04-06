package repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import connection.DatabaseConnection;
import exceptions.TagException;
import models.Tag;

public class TagRepository {
	public static final Map<Integer, Tag> tags = new HashMap<>();
	
	private static final String INSERT_TAG_QUERY = "INSERT INTO tags (name) VALUES (?)";
	
	private static final String INVALID_TAG_MESSAGE = "Invalid tag!";
	private static final String INVALID_INSERT = "Invalid insert tag!";
	private static final String VIEW_DATA_TAG = "Create tag : id: %d, tag name: %s";
	private static TagRepository tagRepository;
	
	private TagRepository() {
	}
	
	public static TagRepository getInstance() {
		if (tagRepository == null) {
			tagRepository = new TagRepository();
		}
		
		return tagRepository;
	}
	
	public Tag addTag(String tagName) throws TagException {
		try (PreparedStatement pr = DatabaseConnection.getConnection().prepareStatement(INSERT_TAG_QUERY, new String[] { "id" })){
			pr.setString(1, tagName);
			pr.executeUpdate();
			
			ResultSet rs = pr.getGeneratedKeys();
			Tag tag = new Tag();
			tag.setName(tagName);
			if (rs != null && rs.next()) {
				int tagId = rs.getInt(1);
				tag.setId(tagId);
				
				tags.put(tagId, tag);
				System.out.println(String.format(VIEW_DATA_TAG, tagId, tagName));
			}
			
			return tag;
		} catch (Exception e) {
			throw new TagException(INVALID_INSERT);
		}
	}
	
	public Tag getTagByName(String name) {
		if (!tags.values().stream().anyMatch(t -> t.getName().equals(name))) {
			return null;
		}
		
		return tags.values().stream().filter(t -> t.getName().equals(name)).findFirst().get();
	}
	
	public Tag getTagById(int id) throws TagException {
		if (!tags.containsKey(id)) {
			throw new TagException(INVALID_TAG_MESSAGE);
		}
		
		return tags.get(id);
	}
}
