package models;

import java.util.HashSet;
import java.util.Set;

public class Post {
	private int id;
	private String name;
	private String imageUrl;
	private User user;
	private Set<Comment> comments;
	private Tag tag;
	
	public Post() {
		this.comments = new HashSet<>();
	}
	
	public void addComment(Comment comment) {
		this.comments.add(comment);
	}
}
