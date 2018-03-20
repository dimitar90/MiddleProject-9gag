package core;

import java.io.File;
import java.io.IOException;

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

public class Application {
	public static void main(String[] args) {
		try {
			File file = new File(SectionRepository.SECTION_PATH);
			if (!file.exists()) {
				//тука в то€ метод взех н€какви примерни секции от реални€ сайт и направо ги зареждам в репоситорито
				loadSections();
			} else {
				SectionRepository.getInstance().deserialize();
				Section.setValueToIdGenerator(SectionRepository.getInstance().getLastId());
			}
			
			UserRepository.getInstance().deserialize();
			// UserRepository.getInstance().importUser();
			User.setValueToIdGenerator(UserRepository.getInstance().getLastId());

			CommentRepository.getInstance().deserialize();
			// CommentRepository.getInstance().importComment();
			Comment.setValueToIdGenerator(CommentRepository.getInstance().getLastId());

			TagRepository.getInstance().deserialize();
			Tag.setValueToIdGenerator(TagRepository.getInstance().getLastId());

			PostRepository.getInstance().deserialize();
			Post.setValueToIdPostGenerator(PostRepository.getInstance().getLastId());
			
		} catch (IOException | SectionException e) {
			System.out.println(e.getMessage());
		}
		

		Runnable engine = new Engine();
		engine.run();
	}

	private static void loadSections() throws SectionException {
		String[] sectionNames = { "Travel", "Wallpaper", "Photography", "Animals", "Awesome", "Car", "Comic", "Country",
				"Crafts", "Food", "Funny", "Gaming", "Girl", "Horror", "Music", "Politics", "Relationship", "Savage",
				"School", "Sci-Tech", "Superhero", "Sport", "Timely", "Video", "WTF" };
		
		for (String name : sectionNames) {
			SectionRepository.getInstance().addSection(name);
		}
	}
}
