package core;

import java.io.FileNotFoundException;

import models.Comment;
import models.Tag;
import models.User;
import repositories.CommentRepository;
import repositories.TagRepository;
import repositories.UserRepository;

public class Application {
	public static void main(String[] args) {
		try {
			UserRepository.getInstance().deserialize();
			User.setValueToIdGenerator(UserRepository.getInstance().getLastId());
			
			CommentRepository.getInstance().deserialize();
			Comment.setValueToIdGenerator(CommentRepository.getInstance().getLastId());
			
			TagRepository.getInstance().deserialize();
			Tag.setValueToIdGenerator(TagRepository.getInstance().getLastId());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
			
		Runnable engine = new Engine();
		engine.run();
	}
}
