package repositories;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import exceptions.SerializeException;
import exceptions.UserException;
import models.User;
import utils.JsonSerializer;
import utils.Session;

public class UserRepository {
	private static final String ALREADY_EXIST = "Username already exist!";
	private static final String PASSWORD_MISMATCH = "Password mismatch!";
	private static final String USER_PATH = "users.json";
	private static UserRepository userRepository;
	private Map<String, User> users;
	private JsonSerializer serializer;
	
	private UserRepository() {
		this.serializer = new JsonSerializer();
		this.users = new HashMap<>();
	}

	public static UserRepository getInstance() {
		if (userRepository == null) {
			userRepository = new UserRepository();
		}

		return userRepository;
	}

	public User addUser(String username, String password, String confirmPassword) throws UserException {
		if (UserRepository.getInstance().isExistByUsername(username)) {
			throw new UserException(ALREADY_EXIST);
		}
		
		if (!password.equals(confirmPassword)) {
			throw new UserException(PASSWORD_MISMATCH);
		}
		
		User user = new User(username, password);
		this.users.put(user.getName(), user);
		
		return user;
	}
	
	public User login(String username, String password) {
		if (!UserRepository.getInstance().isExistByUsername(username)
				|| !UserRepository.getInstance().getUserByUsername(username).getPassword().equals(password)) {
			return null;
		}
		
		User user = UserRepository.getInstance().getUserByUsername(username);
		Session.getInstance().setUser(user);
		
		return user;
	}

	public User getUserByUsername(String username) {
		// User user = this.users().filter(u ->
		// u.getName().equals(username)).findFirst().get();
		return this.users.get(username);
	}

	public boolean isExistByUsername(String username) {
		if (this.users.containsKey(username)) {
			return true;
		}

		return false;
		// return this.users.stream().anyMatch(u -> u.getName().equals(username));
	}

//	public void serialize() throws IOException {
//		File file = new File(USER_PATH);
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//		String jsonUsers = gson.toJson(this.users);
//
//		try (PrintStream ps = new PrintStream(file)) {
//			file.createNewFile();
//			ps.println(jsonUsers);
//		}
//	}
	public void exportUser() throws SerializeException {
		serializer.serialize(this.users, USER_PATH);
	}

	public void deserialize() throws FileNotFoundException {
		File file = new File(USER_PATH);
		Gson gson = new GsonBuilder().create();
		StringBuilder sb = new StringBuilder();

		try (Scanner sc = new Scanner(file)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				sb.append(line);
			}
		}

		Map<String, User> map = gson.fromJson(sb.toString(), new TypeToken<Map<String, User>>() {
		}.getType());
		this.users = map;
	}
//	public void importUser() throws DeserializeException {
//		
//	}
	
	
	public int getLastId() {
		if (this.users == null || this.users.size() == 0) {
			return 0;
		}
		
		return this.users
				.values()
				.stream()
				.sorted((u1, u2) -> Integer.compare(u2.getId(), u1.getId()))
				.findFirst()
				.get()
				.getId();
	}
}
