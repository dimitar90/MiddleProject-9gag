package models;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Section {
	private static int idGenerator;
	private int id;
	private String name;
	private Set<Integer> postIds;
	
	public Section(String name) {
		this.id = ++idGenerator;
		this.setName(name);
		this.postIds = new HashSet<>();
	}
	
	public void addPost(int postId) {
		this.postIds.add(postId);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	static {
		idGenerator = 0;
	}
	
	public Section() {
		this.id = ++idGenerator;
	}
	
	public static void setValueToIdGenerator(int lastId) {
		idGenerator = lastId;
	}
	
	public int getId() {
		return this.id;
	}

	public Set<Integer> getPostIds() {
		return Collections.unmodifiableSet(this.postIds);
	}
}
