package models;

import java.util.Date;

public class Comment {
	private static int idGenerator;
	private int id;
	private String content;
	private Date date;
	private Post post;
	private User user;

	static {
		idGenerator = 0;
	}
	
	public Comment(String content) {
		this.id = ++idGenerator;
		this.date = new Date();
		this.setContent(content);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public static void setValueToIdGenerator(int lastId) {
		idGenerator = lastId;
	}

	@Override
	public String toString() {
		return "Comment content: " + this.content + ". Author: " + this.user.getName() + ". Written on: " + this.getDate();
	}
}
