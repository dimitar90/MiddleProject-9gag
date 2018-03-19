package repositories;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.sql.rowset.serial.SerialException;
import org.omg.Messaging.SyncScopeHelper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import exceptions.PostException;
import exceptions.SerializeException;
import models.Comment;
import models.Post;
import models.Tag;
import models.User;
import utils.JsonSerializer;
import utils.Session;

public class PostRepository {
	private static final int MIN_GRADE = 1;

	private static final int MAX_GRADE = 10;

	private static final String POSTS_PATH = "posts.json";

	private static final String NO_AUTHORIZATION = "You don't have permession to delete this post";

	private static final String INVALID_GRADE = String.format("Grade must be between %d and %d!", MIN_GRADE, MAX_GRADE);

	private static final String NOT_EXIST_POST_MEESAGE = "The post does not exist!";

	private static final String ALREADY_RATED_POST_MESSAGE = "You have already rated this post!";

	private static final String NO_POST_COMMENTS_MESSAGE = "This post no have comments.";

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

		this.posts.get(postId).getUser().deletePostById(postId);

		this.posts.remove(postId);
	}

	public Post addPost(String postName, String description, String url, String tagName) throws PostException {
		Tag tag = TagRepository.getInstance().getTagByName(tagName);
		if (tag == null) {
			tag = TagRepository.getInstance().addTag(tagName);
		}

		Post post = new Post(postName, description, url, tag);
		User user = Session.getInstance().getUser();
		post.setUser(user);
		user.addPost(post.getId());
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
		if (grade < MIN_GRADE || grade > MAX_GRADE) {
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
		
		this.posts
			.values()
			.stream()
			.filter(p -> p.getTag().getName().equals(tagName))
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
