package models;

import java.util.HashSet;
import java.util.Set;

import utils.IDeserialize;

public class User implements IDeserialize{
	private static int idGenerator;
	private int id;
	private String name;
	private String password;
	private Set<Integer> postIds;
	private Set<Integer> commentIds;
	private Set<Integer> ratedPostIds;
	
	static {
		idGenerator = 0;
	}

	public User(String name, String password) {
		this.id = ++idGenerator;
		this.setName(name);
		this.setPassword(password);
		this.postIds = new HashSet<>();
		this.commentIds = new HashSet<>();
		this.ratedPostIds = new HashSet<>();
	}

	public int getId() {
		return this.id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public static void setValueToIdGenerator(int lastId) {
		idGenerator = lastId;
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
