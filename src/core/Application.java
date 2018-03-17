package core;

import java.io.FileNotFoundException;

import models.User;
import repositories.UserRepository;

public class Application {
	public static void main(String[] args) {
		
		try {
			UserRepository.getInstance().deserialize();
			User.setValueToIdGenerator(UserRepository.getInstance().getLastId());
		} catch (FileNotFoundException e) {
			System.out.println("Data is loss!");
		}
		
		Runnable engine = new Engine();
		engine.run();
	}
}
