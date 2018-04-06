package models;

import java.time.LocalDateTime;
import java.util.Date;

import utils.IDeserialize;

public class Comment {
	private int id;
	private String content;
	private LocalDateTime dateTime;
	private Post post;
	private User user;

	public Comment() {
		
	}

	public void setId(int id) {
		this.id = id;
	}

	// TODO validation
	public Comment(String content) {
		this.dateTime = LocalDateTime.now();
		this.setContent(content);
	}

	public int getId() {
		return id;
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

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
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
		return "Comment content: " + this.content + ". Author: " + this.user.getUsername() + ". Written on: "
				+ this.dateTime.toString();
	}

}
