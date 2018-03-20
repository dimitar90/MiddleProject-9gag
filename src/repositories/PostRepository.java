package repositories;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.rowset.serial.SerialException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import exceptions.PostException;
import exceptions.SectionException;
import exceptions.SerializeException;
import models.Comment;
import models.Post;
import models.Section;
import models.Tag;
import models.User;
import utils.JsonSerializer;
import utils.Session;

public class PostRepository {
	private static final int DOWN_GRADE = -1;

	private static final int UP_GRADE = 1;

	private static final String POSTS_PATH = "posts.json";

	private static final String NO_AUTHORIZATION = "You don't have permession to delete this post";

	private static final String INVALID_GRADE = String.format("Grade must be %d or %d!", DOWN_GRADE, UP_GRADE);

	private static final String NOT_EXIST_POST_MEESAGE = "The post does not exist!";

	private static final String ALREADY_RATED_POST_MESSAGE = "You have already rated this post!";

	private static final String NO_POST_COMMENTS_MESSAGE = "This post no have comments.";

	private static final String INVALID_SECTION_NAME = "The section must be one of these: ";

	private static final String NO_POST_MESSAGE = "There are no posts in this section";

	public static PostRepository postRepository;

	private Map<Integer, Post> posts;
	private JsonSerializer serializer;

	private PostRepository() {
		this.serializer = new JsonSerializer();
		this.posts = new HashMap<>();
	}

	public static PostRepository getInstance() {
		if (postRepository == null) {
			postRepository = new PostRepository();
		}
		return postRepository;
	}

	
//	public void editCommentOfCurrentPost(int postId, int commentId,String content) throws PostException {
//		if (!this.posts.containsKey(postId)) {
//			throw new PostException(NOT_EXIST_POST_MEESAGE);
//		}
//		
//		if (Session.getInstance().getUser().getId() != this.posts.get(postId).getUser().getId()) {
//			throw new PostException(NO_AUTHORIZATION);
//		}
//		
//		this.posts.get(postId).editComment(commentId);
//	}
	
	public void delete(int postId) throws PostException {
		if (!this.posts.containsKey(postId)) {
			throw new PostException(NOT_EXIST_POST_MEESAGE);
		}

		if (Session.getInstance().getUser().getId() != this.posts.get(postId).getUser().getId()) {
			throw new PostException(NO_AUTHORIZATION);
		}

		CommentRepository.getInstance().deleteAllCommentsCurrentPostById(postId);
		
		this.posts.get(postId).getUser().deletePostById(postId);

		this.posts.remove(postId);
	}

	public Post addPost(String description, String url, String sectionName, List<String> tags) throws PostException, SectionException {
		Section section = SectionRepository.getInstance().getSectionByName(sectionName);
		List<String> allSectionNames = SectionRepository.getInstance().getAllSectionNames();
		
		if (section == null) {
			throw new PostException(INVALID_SECTION_NAME + String.join(", ", allSectionNames));
		}
//		if (section == null) {
//			section = SectionRepository.getInstance().addSection(sectionName);
//		}
		
		Post post = new Post(description, url, section);

		//и тука се променя логиката вече не проверяваме само за 1 таг, а за всички подадени и ги сетваме на поста
		for (String tagName : tags) {
			Tag tag = TagRepository.getInstance().getTagByName(tagName);
			if (tag == null) {
				tag = TagRepository.getInstance().addTag(tagName);
			}
			
			post.addTagId(tag.getId());
		}

		User user = Session.getInstance().getUser();
		post.setUser(user);
		user.addPost(post.getId());
		section.addPost(post.getId());
		this.posts.put(post.getId(), post);

		return post;
	}

//	public void serialize() throws IOException {
//		File file = new File(POSTS_PATH);
//
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//
//		String jsonPosts = gson.toJson(this.posts);
//
//		try (PrintStream pstream = new PrintStream(file)) {
//			file.createNewFile();
//			pstream.println(jsonPosts);
//		}
//	}
	
	public void exportPost() throws SerializeException, SerialException {
		this.serializer.serialize(this.posts, POSTS_PATH);
	}

	public void deserialize() throws IOException {
		File file = new File(POSTS_PATH);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		StringBuilder sb = new StringBuilder();

		try (Scanner sc = new Scanner(file)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				sb.append(line);
			}
		}

		Map<Integer, Post> map = gson.fromJson(sb.toString(), new TypeToken<Map<Integer, Post>>() {
		}.getType());

		this.posts = map;
	}

	public int getLastId() {
		if (this.posts == null || this.posts.size() == 0) {
			return 0;
		}

		return this.posts.keySet().stream().sorted((id1, id2) -> Integer.compare(id1, id2)).findFirst().get()
				.intValue();
	}

	public Post getPostById(int postId) {
		if (!this.posts.containsKey(postId)) {
			return null;
		}

		return this.posts.get(postId);
	}

	public void addGradeToPost(int postId, int grade) throws PostException {
		if (grade != DOWN_GRADE && grade != UP_GRADE) {
			throw new PostException(INVALID_GRADE);
		}

		if (!this.posts.containsKey(postId)) {
			throw new PostException(NOT_EXIST_POST_MEESAGE);
		}

		User user = Session.getInstance().getUser();
		if (user.checkForRatedPostById(postId)) {
			throw new PostException(ALREADY_RATED_POST_MESSAGE);
		}

		this.posts.get(postId).addRating(grade);
		user.addRatedPostId(postId);
	}
	
	public void listPostsByTagName(String tagName) {
		CommentRepository commentsRepository = CommentRepository.getInstance();
		
		//тука има малка промяна понеже вече постовете имат колекция от тагове(а самите постове си пазят само ид-то на таговете
		//заради цикличния проблем при сериализация ако пазят само обекти).
		//Та за да пресея всички постове, които съдържат подадения таг съм направил тоя помощен метод
		//при филтрацията, който по всичките ид-та на тагове в поста бърка в таг репоситорито и връща имената
		//на тези тагове и филтрирам само тези постове, които съдържат този таг
		this.posts
			.values()
			.stream()
			.filter(p -> getTagNamesByTagIds(p.getTagIds()).contains(tagName))
			.sorted((p1, p2) -> p2.getDate().compareTo(p1.getDate()))
			.forEach(p -> System.out.println(p + System.lineSeparator()
				+ getPrintedComments(commentsRepository.getCommentsByPostId(p.getId()))
				+ System.lineSeparator()));
	}

	public void listAllPostsSortedByLatest() {
		CommentRepository commentsRepository = CommentRepository.getInstance();

		this.posts
				.values()
				.stream()
				.sorted((p1, p2) -> p2.getDate().compareTo(p1.getDate()))
				.forEach(p -> System.out.println(p + System.lineSeparator()
						+ getPrintedComments(commentsRepository.getCommentsByPostId(p.getId()))
						+ System.lineSeparator()));
	}
	
	public void listAllPostsSortedByRating() {
		CommentRepository commentsRepository = CommentRepository.getInstance();

		this.posts
				.values()
				.stream()
				.sorted((p1, p2) -> Integer.compare(p2.getRating(), p1.getRating()))
				.forEach(p -> System.out.println(p + System.lineSeparator()
						+ getPrintedComments(commentsRepository.getCommentsByPostId(p.getId()))
						+ System.lineSeparator()));
	}
	
	public void listAllPostsBySectionName(String sectionName) throws PostException {
		Section section = SectionRepository.getInstance().getSectionByName(sectionName);
		
		//тука правя проверка ако секцията не съществува хвърлям ексепшън и показвам като съобщение
		//всичките налични секции от които може да се избере
		List<String> allSectionNames = SectionRepository.getInstance().getAllSectionNames();
		if (section == null) {
			throw new PostException(INVALID_SECTION_NAME + String.join(", ", allSectionNames));
		}
		
		CommentRepository commentsRepository = CommentRepository.getInstance();
		
		//тука взимам всичките ид-та на постовете в избраната секция и след това долу в stream-a
		//филтрирам мапа-а с постове по ид-та, които се съдържат в сета който съм взел от секцията от която трябва
		//да се листнат постовете след това взимам само value-тата от мапа понеже само те трябват
		//сортирам по дата от най-нови към най-стари постове и другото е стандарно като в другите 
		//команди показвам всеки пост от секцията с всичките му коментари.
		Set<Integer> postIds = section.getPostIds();
		if (postIds.size() == 0) {
			System.out.println(NO_POST_MESSAGE);
		} else {
			this.posts
				.entrySet()
				.stream()
				.filter(kvp -> postIds.contains(kvp.getKey()))
				.map(kvp -> kvp.getValue())
				.sorted((p1, p2) -> p2.getDate().compareTo(p1.getDate()))
				.forEach(p -> System.out.println(p + System.lineSeparator()
						+ getPrintedComments(commentsRepository.getCommentsByPostId(p.getId()))
						+ System.lineSeparator()));
		}
		
	}
	
	private Set<String> getTagNamesByTagIds(Set<Integer> tagIds) {
		TagRepository tagRepository = TagRepository.getInstance();
		Set<String> tagNames = new HashSet<>();
		
		for (Integer tagId : tagIds) {
			Tag tag = tagRepository.getTagById(tagId);
			
			if (tag != null) {
				tagNames.add(tag.getName());
			}
		}
		
		return tagNames;
	}

	private String getPrintedComments(List<Comment> comments) {
		if (comments.size() == 0) {
			return NO_POST_COMMENTS_MESSAGE;
		}

		StringBuilder sb = new StringBuilder();
		for (Comment comment : comments) {
			sb.append("  -" + comment + System.lineSeparator());
		}
		sb.append("---------------------------------");
		sb.append(System.lineSeparator());

		return sb.toString();
	}
}
