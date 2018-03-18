package repositories;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import exceptions.CommentException;
import models.Comment;
import models.Post;
import models.User;
import utils.Session;

public class CommentRepository {
	private static final String NOT_EXIST_COMMENT_MESSAGE = "This comment does not exist!";
	private static final String NOT_HAVE_AUTHORIZATION_MESSAGE = "Not have authorization for delete this comment!";
	private static final String COMMENT_PATH = "comments.json";
	
	private static CommentRepository commentRepository;
	private Map<Integer, Comment> comments;
	
	private CommentRepository() {
		this.comments = new HashMap<>();
	}
	
	public static CommentRepository getInstance() {
		if (commentRepository == null) {
			commentRepository = new CommentRepository();
		}
		
		return commentRepository;
	}
	
	public Comment add(String content, int postId) throws CommentException {
		Post post = PostRepository.getInstance().getPostById(postId);
		if (post == null) {
			throw new CommentException("This post does not exist!");
		}
		
		User user = Session.getInstance().getUser();
		Comment comment = new Comment(content);
		comment.setPost(post);
		comment.setUser(Session.getInstance().getUser());
		post.addComment(comment.getId());
		user.addComment(comment.getId());
		this.comments.put(comment.getId(), comment);
		
		return comment;
	}

	public void delete(int commentId) throws CommentException {
		if (!this.comments.containsKey(commentId)) {
			throw new CommentException(NOT_EXIST_COMMENT_MESSAGE);
		}
		
		//тука се прави проверка ако логнатия юзер не е автор на коментара да няма право да го изтрие
		if (Session.getInstance().getUser().getId() != this.comments.get(commentId).getUser().getId()) {
			throw new CommentException(NOT_HAVE_AUTHORIZATION_MESSAGE);
		}
		
		this.comments.remove(commentId);
	}
	
	public void serialize() throws IOException {
		File file = new File(COMMENT_PATH);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonComments = gson.toJson(this.comments);

		try (PrintStream ps = new PrintStream(file)) {
			file.createNewFile();
			ps.println(jsonComments);
		}
	}

	public void deserialize() throws FileNotFoundException {
		File file = new File(COMMENT_PATH);
		Gson gson = new GsonBuilder().create();
		StringBuilder sb = new StringBuilder();

		try (Scanner sc = new Scanner(file)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				sb.append(line);
			}
		}

		Map<Integer, Comment> map = gson.fromJson(sb.toString(), new TypeToken<Map<Integer, Comment>>() {
		}.getType());
		
		this.comments = map;
	}

	public int getLastId() {
		if (this.comments == null || this.comments.size() == 0) {
			return 0;
		}
		
		return this.comments
				.values()
				.stream()
				.sorted((c1, c2) -> Integer.compare(c2.getId(), c1.getId()))
				.findFirst()
				.get()
				.getId();
	}
}
