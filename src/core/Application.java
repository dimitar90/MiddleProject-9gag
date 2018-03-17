package core;

import java.io.FileNotFoundException;

import repositories.UserRepository;

public class Application {
	public static void main(String[] args) {
		try {
			UserRepository.getInstance().deserialize();
		} catch (FileNotFoundException e) {
			System.out.println("Data is loss!");
		}
		
		Runnable engine = new Engine();
		engine.run();
	}
}
