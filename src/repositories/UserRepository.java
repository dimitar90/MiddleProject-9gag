package repositories;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import models.User;

public class UserRepository {
	private static final String USER_PATH = "users.json";
	private static UserRepository userRepository;
	private Map<String, User> users;
	
	private UserRepository() {
		this.users = new HashMap<>();
	}
	
	public static UserRepository getInstance() {
		if (userRepository == null) {
			userRepository = new UserRepository();
		}
		
		return userRepository;
	}
	
	public void addUser(User user) {
		this.users.put(user.getName(), user);
	}
	
	public User getUserByUsername(String username) {
		//User user = this.users().filter(u -> u.getName().equals(username)).findFirst().get();
		return this.users.get(username);
	}
	
	public boolean isExistByUsername(String username) {
		if (this.users.containsKey(username)) {
			return true;
		}
		
		return false;
		//return this.users.stream().anyMatch(u -> u.getName().equals(username));
	}
	
	public void serialize() {
		File file = new File(USER_PATH);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonUsers = gson.toJson(this.users);
		
		try(PrintStream ps = new PrintStream(file)) {
			file.createNewFile();
			ps.println(jsonUsers);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deserialize() throws FileNotFoundException {
		File file = new File(USER_PATH);
		Gson gson = new GsonBuilder().create();
		StringBuilder sb = new StringBuilder();
		
		try (Scanner sc = new Scanner(file)){
			while(sc.hasNextLine()) {
				String line = sc.nextLine();
				sb.append(line);
			}
		}
		
		Map<String, User> users = gson.fromJson(sb.toString(), Map.class);
		this.users = users;
	}
}
