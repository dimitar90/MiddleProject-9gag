package models;

import java.util.HashSet;
import java.util.Set;

import annotations.Column;
import utils.IDeserialize;

public class User implements IDeserialize{
	private int id;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "email")
	private String email;
	
	private Set<Integer> postIds;
	private Set<Integer> commentIds;
	private Set<Integer> ratedPostIds;
	
	public User() {
		this.postIds = new HashSet<>();
		this.commentIds = new HashSet<>();
		this.ratedPostIds = new HashSet<>();
	}

	public User(String username, String password, String email) {
		this();
		this.setUsername(username);
		this.setPassword(password);
		this.setEmail(email);
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void addPost(int postId) {
		this.postIds.add(postId);
	}


	public void addComment(int commentId) {
		this.commentIds.add(commentId);
	}

	public void deletePostById(int postId) {
		this.postIds.remove(postId);
	}
	
	public void addRatedPostId(int postId) {
		this.ratedPostIds.add(postId);
	}
	
	public boolean checkForRatedPostById(int postId) {
		return this.ratedPostIds.contains(postId);
	}
}
