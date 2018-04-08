package repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import connection.DatabaseConnection;
import exceptions.PostException;
import models.Comment;
import models.Post;
import models.Section;
import models.Tag;
import models.User;
import utils.Session;

public class PostRepository {
	private static final String MSG_SUCCESSFULY_GRADED = "Successfuly graded post";
	private static final String INSERT_POST_RATING_USER_QUERY = "INSERT INTO rating_post_user (post_id, user_id, grade) VALUES(?, ?, ?)";
	private static final String INSERT_POST_QUERY = "INSERT INTO posts (description, internet_url, date_time, author_id, section_id) VALUES(?, ?, ?, ?, ?)";
	private static final String INSERT_POST_TAG_QUERY = "INSERT INTO post_tag (post_id,tag_id) VALUES (?,?)";
	public static final Map<Integer, Post> POSTS = new HashMap<Integer, Post>();
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
	private SectionRepository refToSectionRepo;

	private PostRepository() {
		this.refToSectionRepo = SectionRepository.getInstance();
	}

	public static PostRepository getInstance() {
		if (postRepository == null) {
			postRepository = new PostRepository();
		}
		return postRepository;
	}

	// public void editCommentOfCurrentPost(int postId, int commentId,String
	// content) throws PostException {
	// if (!this.posts.containsKey(postId)) {
	// throw new PostException(NOT_EXIST_POST_MEESAGE);
	// }
	//
	// if (Session.getInstance().getUser().getId() !=
	// this.posts.get(postId).getUser().getId()) {
	// throw new PostException(NO_AUTHORIZATION);
	// }
	//
	// this.posts.get(postId).editComment(commentId);
	// }

	// public void delete(int postId) throws PostException {
	// if (!this.posts.containsKey(postId)) {
	// throw new PostException(NOT_EXIST_POST_MEESAGE);
	// }
	//
	// if (Session.getInstance().getUser().getId() !=
	// this.posts.get(postId).getUser().getId()) {
	// throw new PostException(NO_AUTHORIZATION);
	// }
	//
	// CommentRepository.getInstance().deleteAllCommentsCurrentPostById(postId);
	//
	// this.posts.get(postId).getUser().deletePostById(postId);
	//
	// this.posts.remove(postId);
	// }

	public Post addPost(String description, String url, String sectionName, List<String> tags) throws Exception {
		// Get section by name
		Section section = this.refToSectionRepo.getSectionByName(sectionName);
		List<String> allSectionNames = this.refToSectionRepo.getAllSectionNames();

		if (section == null) {
			throw new PostException(INVALID_SECTION_NAME + String.join(", ", allSectionNames));
		}

		// Open connection to insert values into posts table
		Post post = null;
		try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(INSERT_POST_QUERY,
				PreparedStatement.RETURN_GENERATED_KEYS);) {

			int sectionId = section.getId();
			int userId = Session.getInstance().getUser().getId();
			LocalDateTime dateTime = LocalDateTime.now();
			Timestamp timeStamp = Timestamp.valueOf(dateTime);

			ps.setString(1, description);
			ps.setString(2, url);
			ps.setTimestamp(3, timeStamp);
			ps.setInt(4, userId);
			ps.setLong(5, sectionId);
			ps.executeUpdate();

			ResultSet result = ps.getGeneratedKeys();
			result.next();
			int postId = result.getInt(1);

			post = new Post(postId, description, url, section, dateTime);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		TagRepository tagRepo = TagRepository.getInstance();
		Set<Integer> tagIds = new HashSet<>();

		for (String tagName : tags) {
			Tag t = tagRepo.getTagByName(tagName);
			if (t == null) {
				t = tagRepo.addTag(tagName);
			}
			int tagId = t.getId();
			tagIds.add(tagId);
			post.addTag(t);
		}

		// Open connection to insert values for post_tag table
		try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(INSERT_POST_TAG_QUERY);) {
			for (Integer id : tagIds) {
				ps.setInt(1, post.getId());
				ps.setInt(2, id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.POSTS.put(post.getId(), post);
		return post;
	}

	public void addGradeToPost(int postId, byte grade) throws PostException {
		if (grade != DOWN_GRADE && grade != UP_GRADE) {
			throw new PostException(INVALID_GRADE);
		}

		if (!this.POSTS.containsKey(postId)) {
			throw new PostException(NOT_EXIST_POST_MEESAGE);
		}

		User user = Session.getInstance().getUser();
		if (user.checkForRatedPostByPostId(postId)) {
			throw new PostException(ALREADY_RATED_POST_MESSAGE);
		}

		Post post = PostRepository.getInstance().getPostById(postId);
		post.addRating(grade);
		user.addRatedPost(post);

		int userId = user.getId();
		// Open connection to insert the grade
		try (PreparedStatement ps = DatabaseConnection.getConnection()
				.prepareStatement(INSERT_POST_RATING_USER_QUERY)) {
			ps.setInt(1, postId);
			ps.setInt(2, userId);
			ps.setInt(3, grade);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(MSG_SUCCESSFULY_GRADED);
	}

	public Post getPostById(int postId) {
		if (!this.POSTS.containsKey(postId)) {
			return null;
		}

		return this.POSTS.get(postId);
	}

	public void listPostsByTagName(String tagName) {
		this.POSTS.values().stream().filter(p -> p.getTags().stream().anyMatch(t -> t.getName().equals(tagName)))
				.sorted((p1, p2) -> p2.getDateTime().compareTo(p1.getDateTime())).forEach(p -> System.out.println(p));
	}

	public void listAllPostsSortedByLatest() {
		this.POSTS.values().stream().sorted((p1, p2) -> p2.getDateTime().compareTo(p1.getDateTime()))
				.forEach(p -> System.err.println(p));
	}

	public void listAllPostsSortedByRating() {
		this.POSTS.values().stream().sorted((p1, p2) -> Integer.compare(p2.getRating(), p1.getRating()))
				.forEach(p -> System.out.println(p));
	}

	public void listAllPostsBySectionName(String sectionName) throws PostException {
		Section section = SectionRepository.getInstance().getSectionByName(sectionName);

		List<String> allSectionNames = SectionRepository.getInstance().getAllSectionNames();
		if (section == null) {
			throw new PostException(INVALID_SECTION_NAME + String.join(", ", allSectionNames));
		}

		Set<Post> posts = section.getPost();

		if (posts.size() == 0) {
			System.out.println(NO_POST_MESSAGE);
		} else {
			posts.stream().filter(p -> p.getSection().getName().equals(sectionName))
					.forEach(p -> System.out.println(p));
		}

	}

	public void getProcess() {
		for (Post post : POSTS.values()) {
			// && Session.getInstance().getUser() != null
			// &&Session.getInstance().getUser().getId() == post.getUser().getId()
			if (!post.isDownload()) {
				post.downloadImage();
			}
		}

	}
}
