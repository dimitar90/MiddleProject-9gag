package models;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Section {
	private int id;
	private String name;
	private Set<Post> post;
	
	public Section() {
		this.post = new HashSet<>();
	}
	
	public Section(int id,String name) {
		this();
		this.setId(id);
		this.setName(name);
	}
	
	public void addPost(Post post) {
		this.post.add(post);
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

	public Set<Post> getPost() {
		return Collections.unmodifiableSet(this.post);
	}
	
	@Override
	public int hashCode() {
		return this.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Section)) {
			return false;
		}
		
		Section section = (Section) obj;
		return this.id == section.id;
	}
}
