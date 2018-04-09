package models;

import java.util.HashSet;
import java.util.Set;

import exceptions.UserException;

public class User {
	private static final String MSG_INVALID_EMAIL = "Invalid email address";

	private static final String MSG_INVALID_NAME = "Invalid user name";

	private static final String MSG_INVALID_ID = "Invalid id";

	private int id;

	private String username;
	private String password;
	private String email;

	private Set<Post> posts;
	private Set<Comment> comments;
	private Set<Post> ratedPosts;

	public User() {
		this.posts = new HashSet<>();
		this.comments = new HashSet<>();
		this.ratedPosts = new HashSet<>();
	}

	public User(String username, String password, String email) throws UserException {
		this();
		this.setUsername(username);
		this.setPassword(password);
		this.setEmail(email);
	}

	public void removeComment(Comment comment) {
		if (this.comments.contains(comment)) {
			this.comments.remove(comment);
		}
	}

	public void removeRatedPost(Post post) {
		this.ratedPosts.remove(post);
	}

	public void addPost(Post post) {
		this.posts.add(post);
	}

	public void addComment(Comment comment) {
		this.comments.add(comment);
	}

	public void deletePost(Post post) {
		if (this.posts.contains(post)) {
			this.posts.remove(post);
		}
	}

	public void addRatedPost(Post post) {
		this.ratedPosts.add(post);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) throws UserException {
		if (username != null && username.length() > 2) {
			this.username = username;
		} else {
			throw new UserException(MSG_INVALID_NAME);
		}
	}

	public void setId(int id) throws UserException {
		if (id > 0) {
			this.id = id;
		} else {
			throw new UserException(MSG_INVALID_ID);
		}
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

	public boolean checkForRatedPostByPostId(int postId) {
		return this.ratedPosts.stream().anyMatch(p -> p.getId() == postId);
	}

	public void setPosts(Set<Post> posts) {
		this.posts = posts;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}

	public Post getUsersPostById(int id) {
		return this.posts.stream().filter(p -> p.getId() == id).findFirst().get();
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof User)) {
			return false;
		}

		User user = (User) obj;
		return this.id == user.id;
	}

}
