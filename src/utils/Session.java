package utils;

import models.User;

public class Session {
	private static Session session;
	private User user;
	
	private Session() {
		this.user = null;
	}
	
	public static Session getInstance() {
		if (session == null) {
			session = new Session();
		}
		
		return session;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user2) {
		this.user = user2;
	}
}
