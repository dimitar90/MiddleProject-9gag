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

	private static final String[] SECTION_NAMES = { "Travel", "Wallpaper", "Photography", "Animals", "Awesome", "Car",
			"Comic", "Country", "Crafts", "Food", "Funny", "Gaming", "Girl", "Horror", "Music", "Politics",
			"Relationship", "Savage", "School", "Sci-Tech", "Superhero", "Sport", "Timely", "Video", "WTF" };
	private static Downloader downloader = new Downloader();

	public static void main(String[] args) {

		try {
			File file = new File(SectionRepository.SECTION_PATH);
			if (!file.exists()) {
				// тука в то€ метод взех н€какви примерни секции от реални€ сайт и направо ги
				// зареждам в репоситорито
				loadSections();
			} else {
				SectionRepository.getInstance().deserialize();
				Section.setValueToIdGenerator(SectionRepository.getInstance().getLastId());
			}

			UserRepository.getInstance().deserialize();
			User.setValueToIdGenerator(UserRepository.getInstance().getLastId());

			CommentRepository.getInstance().deserialize();
			Comment.setValueToIdGenerator(CommentRepository.getInstance().getLastId());

			TagRepository.getInstance().deserialize();
			Tag.setValueToIdGenerator(TagRepository.getInstance().getLastId());

			PostRepository.getInstance().deserialize();
			Post.setValueToIdPostGenerator(PostRepository.getInstance().getLastId());

		} catch (IOException | SectionException e) {
			System.out.println(e.getMessage());
		}

		downloader.setDaemon(true);
		downloader.start();

		Runnable engine = new Engine();
		engine.run();
	}

	private static void loadSections() throws SectionException {
		for (String name : SECTION_NAMES) {
			SectionRepository.getInstance().addSection(name);
		}
	}
}
