package models;

import java.util.Date;

import utils.IDeserialize;

public class Comment implements IDeserialize{
	private static int idGenerator;
	private int id;
	private String content;
	private Date date;
	private Post post;
	private User user;

	static {
		idGenerator = 0;
	}

	// TODO validation
	public Comment(String content) {
		this.id = ++idGenerator;
		this.date = new Date();
		this.setContent(content);
	}

	public int getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

	private void setContent(String content) {
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

	public int getPostId() {
		return this.post.getId();
	}

	public void setNewContent(String content) {
		if (content != null) {
			this.content = content;
		} else {
			System.out.println("right something funny and nice for new content please");
		}
	}

	@Override
	public String toString() {
		return "Comment content: " + this.content + ". Author: " + this.user.getName() + ". Written on: "
				+ this.getDate();
	}

}
