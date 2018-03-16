package models;

import java.util.HashSet;
import java.util.Set;

import repositories.UserRepository;

public class User {
	private static int idGenerator;
	private int id;
	private String name;
	private String password;
	private Set<Post> posts;
	
	static {
		idGenerator = 0;
	}
	
	public User(String name, String password) {
		this.id = ++idGenerator;
		this.setName(name);
		this.setPassword(password);
		this.posts = new HashSet<>();
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
	
	public static void setValueToIdGenerator(int lastId) {
		idGenerator = lastId;
	}
}
