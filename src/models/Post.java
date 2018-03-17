package models;

import java.util.HashSet;
import java.util.Set;

import exceptions.PostException;

public class Post {
	private static final String MESSAGE_INVALID_DESCRIPTION = "Give a funny, creative and descriptive title to the post would give the post a boost!";
	private static final String MESSAGE_INVALID_NAME = "Invalid parameters for name";
	private static int nextPostId;
	private int id;
	private String name;
	private String imageUrl;
	private User user;
	private String description;
	private Set<Comment> comments;
	private Tag tag;

	static {
		nextPostId = 0;
	}

	// Taga go pravq da se set-va ne da se syzdava s posta. Gledam taka e w 9gag
	// suzdava se post s ime i opisanie, url. Ako ne otgowarqt ne se syzdava
	// konstruiraneto na obekta
	public Post(String name, String description, String url) throws PostException {
		this.setDescription(description);
		this.setName(name);
		this.id = ++nextPostId;
		this.comments = new HashSet<>();
		this.imageUrl = url;
	}

	public void addComment(Comment comment) {
		if (comment != null) {
			this.comments.add(comment);
		}
	}

	private void setName(String name) throws PostException {
		if (name != null && name.length() >= 2) {
			this.name = name;
		} else {
			throw new PostException(MESSAGE_INVALID_NAME);
		}

	}

	private void setDescription(String description) throws PostException {
		if (description != null && description.length() >= 5) {
			this.description = description;
		} else {
			throw new PostException(MESSAGE_INVALID_DESCRIPTION);
		}

	}

	public void setValueToIdPostGenerator(int lastId) {
		nextPostId = lastId;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		if (tag != null) {
			this.tag = tag;
		}
	}

	public int getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		if (user != null) {
			this.user = user;
		}
	}
}
