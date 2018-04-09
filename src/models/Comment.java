package models;

import java.time.LocalDateTime;

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
	public Comment(String content,User user,Post post,LocalDateTime dateTime) {
		this.dateTime = dateTime;
		this.setContent(content);
	}

	public Comment(int id,String content,User user,Post post,LocalDateTime dateTime) {
		this(content,user,post, dateTime);
		this.id = id;
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
			this.content = content;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Comment other = (Comment) obj;
		if (dateTime == null) {
			if (other.dateTime != null)
				return false;
		} else if (!dateTime.equals(other.dateTime))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("  -Comment content: ").append(this.content).append(System.lineSeparator());
		sb.append("   Author: ").append(this.user.getUsername()).append(System.lineSeparator());
		sb.append("   Written on: ").append(this.dateTime).append(System.lineSeparator()).append("===============================").append(System.lineSeparator());
	
		return sb.toString();
	}

}
