package repositories;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import exceptions.PostException;
import models.Post;
import models.Tag;
import models.User;
import utils.Session;

public class PostRepository {
	private static final String POSTS_PATH = "posts.json";

	private static final String NOT_EXIST_POST_MESSAGE = "THERE IS NO SUCH A POST";

	private static final String NO_AUTHORIZATION = "You don't have permession to delete this post";

	public static PostRepository postRepository;

	private Map<Integer, Post> posts;
	private User user;

	private PostRepository() {
		this.posts = new HashMap<>();
	}

	public static PostRepository getInstance() {
		if (postRepository == null) {
			postRepository = new PostRepository();
		}
		return postRepository;
	}

	public void delete(int postId) throws PostException {
		if (!this.posts.containsKey(postId)) {
			throw new PostException(NOT_EXIST_POST_MESSAGE);
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
	


	public void serialize() throws IOException {
		File file = new File(POSTS_PATH);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		String jsonPosts = gson.toJson(this.posts);

		try (PrintStream pstream = new PrintStream(file)) {
			file.createNewFile();
			pstream.println(jsonPosts);
		}
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

		return this.posts
				.keySet()
				.stream()
				.sorted((id1, id2) -> Integer.compare(id1, id2))
				.findFirst()
				.get()
				.intValue();
	}

	public User getUser() {
		return user;
	}

	private void setUser(User user) {
		if (user != null) {
			this.user = user;	
		}
	}
	public Post getPostById(int postId) {
		if (!this.posts.containsKey(postId)) {
			return null;
		}
		
		return this.posts.get(postId);
	}
	

}
