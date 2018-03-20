package repositories;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.sql.rowset.serial.SerialException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import exceptions.CommentException;
import exceptions.PostException;
import exceptions.SerializeException;
import models.Comment;
import models.Post;
import models.User;
import utils.JsonSerializer;
import utils.Session;

public class CommentRepository {
	private static final String THIS_POST_DOES_NOT_EXIST = "This post does not exist!";
	private static final String NOT_EXIST_COMMENT_MESSAGE = "This comment does not exist!";
	private static final String NOT_HAVE_AUTHORIZATION_MESSAGE = "Not have authorization for delete this comment!";
	private static final String COMMENT_PATH = "comments.json";

	private static CommentRepository commentRepository;
	private Map<Integer, Comment> comments;
	private JsonSerializer serializer;

	private CommentRepository() {
		this.serializer = new JsonSerializer();
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
			throw new CommentException(THIS_POST_DOES_NOT_EXIST);
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

	public void editCommentOfCurrentPost(int postId, int commentId, String content) throws CommentException, PostException {
		
		if(!isValidPost(postId)) {//����������� ID �� �� ����� � Optional � ������, ����� � !
			throw new PostException(THIS_POST_DOES_NOT_EXIST);
		}
		
		isValidComment(commentId);
		
		isAuthorizated(commentId);
		
//		this.comments
//		.entrySet()
//		.stream()
//		.filter(k -> k.getKey() == commentId)
//		.filter(v -> v.getValue().getPostId() == postId)
//		.forEach(v -> v.setContent(content));
		this.comments
		.values()
		.stream()
		.filter(v -> v.getPostId() == postId)
		.forEach(v -> v.setNewContent(content));
	}

	private boolean isValidPost(int postId) {		
			return this.comments
					.values()
					.stream()
					.filter(v -> v.getPostId() == postId)
					.findFirst()
					.isPresent();
	}

	public void delete(int commentId) throws CommentException {
		
		isValidComment(commentId);
		
		isAuthorizated(commentId);

		this.comments.remove(commentId);
	}

//	public void serialize() throws IOException {
//		File file = new File(COMMENT_PATH);
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//		String jsonComments = gson.toJson(this.comments);
//
//		try (PrintStream ps = new PrintStream(file)) {
//			file.createNewFile();
//			ps.println(jsonComments);
//		}
//	}
	public void exportComment() throws SerializeException, SerialException {
		this.serializer.serialize(this.comments, COMMENT_PATH);
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

//	public void importComment() {
//		this.serializer.deserialize(this.comments, COMMENT_PATH);
//	}
	private void isValidComment(int arg) throws CommentException {
		if (!this.comments.containsKey(arg)) {
			throw new CommentException(NOT_EXIST_COMMENT_MESSAGE);
		}
	}

	private void isAuthorizated(int arg) throws CommentException {
		if (Session.getInstance().getUser().getId() != this.comments.get(arg).getUser().getId()) {
			throw new CommentException(NOT_HAVE_AUTHORIZATION_MESSAGE);
		}
	}

	public List<Comment> getCommentsByPostId(int postId) {
		return this.comments.values().stream().filter(c -> c.getPost().getId() == postId)
				.sorted((c1, c2) -> c2.getDate().compareTo(c1.getDate())).collect(Collectors.toList());
	}

	public void deleteAllCommentsCurrentPostById(int postId) {
		this.comments.values().removeIf(v -> v.getPostId() == postId);
	}

	public int getLastId() {
		if (this.comments == null || this.comments.size() == 0) {
			return 0;
		}

		return this.comments.values().stream().sorted((c1, c2) -> Integer.compare(c2.getId(), c1.getId())).findFirst()
				.get().getId();
	}
}
