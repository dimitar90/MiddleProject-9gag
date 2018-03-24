package models;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Section {
	private int id;
	private String name;
	private Set<Integer> postIds;
	
	public Section() {
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
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}

	public Set<Integer> getPostIds() {
		return Collections.unmodifiableSet(this.postIds);
	}
}
