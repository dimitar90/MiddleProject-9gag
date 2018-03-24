package core;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import exceptions.SectionException;
import models.Comment;
import models.Post;
import models.Section;
import models.Tag;
import models.User;
import repositories.CommentRepository;
import repositories.PostRepository;
import repositories.SectionRepository;
import repositories.TagRepository;
import repositories.UserRepository;
import utils.Downloader;

public class Application {
	private static final String MESSAGE_ITERRUPT_EXCEPTION = "Thread has been interrupted";

	private static Downloader downloader = new Downloader();

	public static void main(String[] args) {

		try {
			try {
				SectionRepository.getInstance().loadSectionsFromDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				UserRepository.getInstance().loadUsersFromDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}

			CommentRepository.getInstance().deserialize();
			Comment.setValueToIdGenerator(CommentRepository.getInstance().getLastId());

			TagRepository.getInstance().deserialize();
			Tag.setValueToIdGenerator(TagRepository.getInstance().getLastId());

			PostRepository.getInstance().deserialize();
			Post.setValueToIdPostGenerator(PostRepository.getInstance().getLastId());

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		downloader.setDaemon(true);
		downloader.start();

		Runnable engine = new Engine();
		engine.run();
	}
}
