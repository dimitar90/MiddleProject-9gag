package repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import connection.DatabaseConnection;
import exceptions.CommentException;
import exceptions.PostException;
import models.Comment;
import models.Post;
import models.User;
import utils.Session;

public class CommentRepository {
	public static final Map<Integer, Comment> COMMENTS = new HashMap<>();
	private static final String NOT_HAVE_AUTHORIZATION_EDIT_MESSAGE = "Not have authorization for edit this comment!";
	private static final String MSG_NOT_SUCH_POST = "This post does not exist!";
	private static final String NOT_EXIST_COMMENT_MESSAGE = "This comment does not exist!";
	private static final String NOT_HAVE_AUTHORIZATION_MESSAGE = "Not have authorization for delete this comment!";
	private static final String UPDATE_COMMENT_QUERY = "UPDATE comments SET content = ? WHERE id = ?"; 
	private static final String SUCCESSFULLY_EDITED_COMMENT_MESSAGE = "Successfully edited comment with id %d. Old content: %s, new content: %s";
	private static final String INSERT_COMMENT_QUERY = "INSERT INTO comments (content, date_time, author_id, post_id) VALUES (?,?,?,?)";
	private static final String VIEW_COMMENT_DATA = "Successfully add comment with id: %d, content: %s, wrriten on post with id: %d, wrriten by %s";
	private static CommentRepository commentRepository;

	private CommentRepository() {

	}

	public static CommentRepository getInstance() {
		if (commentRepository == null) {
			commentRepository = new CommentRepository();
		}

		return commentRepository;
	}

	public String addComent(String content, int postId) throws CommentException {
		Post post = PostRepository.getInstance().getPostById(postId);
		// If there is such a post
		if (post == null) {
			throw new CommentException(MSG_NOT_SUCH_POST);
		}
		Comment comment = new Comment();
		User user = Session.getInstance().getUser();
		
		LocalDateTime curDateTime = LocalDateTime.now();
		Timestamp curTimestamp = Timestamp.valueOf(curDateTime);
		int authorId = user.getId();
		comment.setDateTime(curDateTime);
		comment.setContent(content);
		comment.setUser(user);
		comment.setPost(post);
		
		try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(INSERT_COMMENT_QUERY,
				PreparedStatement.RETURN_GENERATED_KEYS);) {
			ps.setString(1, content);
			ps.setTimestamp(2, curTimestamp);
			ps.setInt(3, authorId);
			ps.setInt(4, postId);
			ps.executeUpdate();

			ResultSet result = ps.getGeneratedKeys();
			result.next();
			comment.setId(result.getInt(1));
		}catch (SQLException e) {
			e.printStackTrace();
		}

//		Comment comment = new Comment(commentId, content, user, post, curDateTime);

		post.addComment(comment);
		user.addComment(comment);
		COMMENTS.put(comment.getId(), comment);
		
		return String.format(VIEW_COMMENT_DATA, comment.getId(), content, post.getId(), user.getUsername());
	}

	public String editComment(int commentId, String newContent)
			throws CommentException, PostException {
		Comment comment = COMMENTS.get(commentId);
		User user = Session.getInstance().getUser();
		if (user.getId() != comment.getUser().getId()) {
			throw new CommentException(NOT_HAVE_AUTHORIZATION_EDIT_MESSAGE);
		}
		
		isValidComment(commentId);
		isAuthorizated(commentId);

		try(PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(UPDATE_COMMENT_QUERY)){
			ps.setString(1, newContent);
			ps.setInt(2, commentId);
			ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int postId = comment.getPostId();
		String oldContent = comment.getContent();
		COMMENTS
		.values()
		.stream()
		.filter(v -> v.getPostId() == postId)
		.forEach(v -> v.setNewContent(newContent));
		
		return String.format(SUCCESSFULLY_EDITED_COMMENT_MESSAGE, commentId, oldContent, newContent);
	}
	

	private boolean isValidPost(int postId) {
		return COMMENTS
				.values()
				.stream()
				.filter(v -> v.getPostId() == postId)
				.findFirst()
				.isPresent();
	}

	public void delete(int commentId) throws CommentException {

		isValidComment(commentId);

		isAuthorizated(commentId);

		COMMENTS.remove(commentId);
	}

	
	private void isValidComment(int arg) throws CommentException {
		if (!COMMENTS.containsKey(arg)) {
			throw new CommentException(NOT_EXIST_COMMENT_MESSAGE);
		}
	}

	private void isAuthorizated(int arg) throws CommentException {
		if (Session.getInstance().getUser().getId() != COMMENTS.get(arg).getUser().getId()) {
			throw new CommentException(NOT_HAVE_AUTHORIZATION_MESSAGE);
		}
	}

	// public List<Comment> getCommentsByPostId(int postId) {
	// this.comments.values()
	// }

	public void deleteAllCommentsCurrentPostById(int postId) {
		COMMENTS.values().removeIf(v -> v.getPostId() == postId);
	}

	public int getLastId() {
		if (COMMENTS == null || COMMENTS.size() == 0) {
			return 0;
		}

		return COMMENTS.values().stream().sorted((c1, c2) -> Integer.compare(c2.getId(), c1.getId())).findFirst().get()
				.getId();
	}

	public Comment getCommentById(Integer commentId) {
		if (!COMMENTS.containsKey(commentId)) {
			return null;
		}

		return COMMENTS.get(commentId);
	}

	public void removeCommentById(int id) {
		COMMENTS.remove(id);
	}
}
