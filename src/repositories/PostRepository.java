package repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
		 try(PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(INSERT_POST_RATING_USER_QUERY)){
			 ps.setInt(1, postId);
			 ps.setInt(2, userId);
			 ps.setInt(3, grade);
			 ps.executeUpdate();
		 }
	}

	
	public int getLastId() {
		if (this.POSTS == null || this.POSTS.size() == 0) {
			return 0;
		}

		return this.POSTS.keySet().stream().sorted((id1, id2) -> Integer.compare(id1, id2)).findFirst().get()
				.intValue();
	}

	public Post getPostById(int postId) {
		if (!this.POSTS.containsKey(postId)) {
			return null;
		}

		return this.POSTS.get(postId);
	}

	
	public void listPostsByTagName(String tagName) {
		CommentRepository commentsRepository = CommentRepository.getInstance();

		// тука има малка промяна понеже вече постовете имат колекция от тагове(а самите
		// постове си пазят само ид-то на таговете
		// заради цикличния проблем при сериализация ако пазят само обекти).
		// Та за да пресея всички постове, които съдържат подадения таг съм направил тоя
		// помощен метод
		// при филтрацията, който по всичките ид-та на тагове в поста бърка в таг
		// репоситорито и връща имената
		// на тези тагове и филтрирам само тези постове, които съдържат този таг
		this.POSTS.values().stream().filter(p -> getTagNamesByTagIds(p.getTagIds()).contains(tagName))
				.sorted((p1, p2) -> p2.getDate().compareTo(p1.getDate()))
				.forEach(p -> System.out.println(p + System.lineSeparator()
						+ getPrintedComments(commentsRepository.getCommentsByPostId(p.getId()))
						+ System.lineSeparator()));
	}

	public void listAllPostsSortedByLatest() {
		CommentRepository commentsRepository = CommentRepository.getInstance();

		this.POSTS.values().stream().sorted((p1, p2) -> p2.getDate().compareTo(p1.getDate()))
				.forEach(p -> System.out.println(p + System.lineSeparator()
						+ getPrintedComments(commentsRepository.getCommentsByPostId(p.getId()))
						+ System.lineSeparator()));
	}

	public void listAllPostsSortedByRating() {
		CommentRepository commentsRepository = CommentRepository.getInstance();

		this.POSTS.values().stream().sorted((p1, p2) -> Integer.compare(p2.getRating(), p1.getRating()))
				.forEach(p -> System.out.println(p + System.lineSeparator()
						+ getPrintedComments(commentsRepository.getCommentsByPostId(p.getId()))
						+ System.lineSeparator()));
	}

	public void listAllPostsBySectionName(String sectionName) throws PostException {
		Section section = SectionRepository.getInstance().getSectionByName(sectionName);

		// тука правя проверка ако секцията не съществува хвърлям ексепшън и показвам
		// като съобщение
		// всичките налични секции от които може да се избере
		List<String> allSectionNames = SectionRepository.getInstance().getAllSectionNames();
		if (section == null) {
			throw new PostException(INVALID_SECTION_NAME + String.join(", ", allSectionNames));
		}

		CommentRepository commentsRepository = CommentRepository.getInstance();

		// тука взимам всичките ид-та на постовете в избраната секция и след това долу в
		// stream-a
		// филтрирам мапа-а с постове по ид-та, които се съдържат в сета който съм взел
		// от секцията от която трябва
		// да се листнат постовете след това взимам само value-тата от мапа понеже само
		// те трябват
		// сортирам по дата от най-нови към най-стари постове и другото е стандарно като
		// в другите
		// команди показвам всеки пост от секцията с всичките му коментари.
		Set<Integer> postIds = section.getPostIds();

		if (postIds.size() == 0) {
			System.out.println(NO_POST_MESSAGE);
		} else {
			this.POSTS.entrySet().stream().filter(kvp -> postIds.contains(kvp.getKey())).map(kvp -> kvp.getValue())
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
