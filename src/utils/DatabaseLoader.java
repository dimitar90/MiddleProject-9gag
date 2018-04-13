package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;

import connection.DatabaseConnection;
import exceptions.CommentException;
import exceptions.PostException;
import exceptions.SectionException;
import exceptions.UserException;
import models.Comment;
import models.Post;
import models.Section;
import models.Tag;
import models.User;
import repositories.CommentDao;
import repositories.PostDao;
import repositories.SectionDao;
import repositories.TagDao;
import repositories.UserDao;

public class DatabaseLoader {
	private static final String LOADING_DATABASE_MESSAGE = "Loading database...";
	private static final String SUCCESSFULLY_LOAD_DATABASE_MESSAGE = "Successfully loaded database for %d ms.";
	
	private static final String GET_ALL_USERS_QUERY = "SELECT id, username, email FROM users";
	private static final String GET_ALL_POSTS_QUERY = "SELECT id, description, internet_url, local_url, has_download, date_time, section_id, author_id FROM posts";
	private static final String GET_ALL_COMMENTS_QUERY = "SELECT id, content, date_time, author_id, post_id FROM comments";
	private static final String GET_ALL_TAGS_QUERY = "SELECT id, name FROM tags";
	private static final String GET_ALL_SECTIONS_QUERY = "SELECT id, name FROM sections";
	private static final String GET_ALL_COMENT_IDS_BY_POST_ID_QUERY = "SELECT id FROM comments WHERE post_id = ?";
	private static final String GET_ALL_TAGS_BY_POST_ID_QUERY = "SELECT tag_id FROM post_tag WHERE post_id = ?";
	private static final String GET_ALL_COMMENT_IDS_BY_USER_ID = "SELECT id FROM comments WHERE author_id = ?";
	private static final String GET_ALL_POST_IDS_BY_USER_ID = "SELECT id FROM posts WHERE author_id = ?";
	private static final String GET_RATING_FOR_POST_BY_ID = "SELECT post_id, SUM(grade) AS rating FROM rating_post_user GROUP BY post_id";
	private static final String GET_RATED_POST_IDS_BY_USER_ID = "SELECT post_id FROM rating_post_user WHERE user_id = ?";
	private static final String GET_POST_IDS_BY_SECTION_ID = "SELECT id FROM posts WHERE section_id = ?";
	
	
	public static void loadDatabase() throws SQLException, SectionException, CommentException, PostException, UserException {
		Connection conn = DatabaseConnection.getConnection();
		System.out.println(LOADING_DATABASE_MESSAGE);
		long startLoadingTime = System.currentTimeMillis();

		// Load users
		try (PreparedStatement preparedStatment = conn.prepareStatement(GET_ALL_USERS_QUERY)) {
			ResultSet resultSet = preparedStatment.executeQuery();

			while (resultSet.next()) {
				int userId = resultSet.getInt("id");
				String username = resultSet.getString("username");
				String email = resultSet.getString("email");

				User user = new User();
				user.setId(userId);
				user.setUsername(username);
				user.setEmail(email);

				UserDao.users.put(userId, user);
				System.out.println("Load user: " + username + " id: " + userId + " email: " + email);
			}
		}

		// Load sections
		try (PreparedStatement preparedStatment = DatabaseConnection.getConnection()
				.prepareStatement(GET_ALL_SECTIONS_QUERY)) {
			ResultSet resultSet = preparedStatment.executeQuery();

			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");

				Section section = new Section();
				section.setId(id);
				section.setName(name);

				SectionDao.SECTION.put(section.getId(), section);
				System.out.println("Load section: " + section.getName() + " id: " + section.getId());
			}
		}

		// Load tags
		try (PreparedStatement preparedStatement = conn.prepareStatement(GET_ALL_TAGS_QUERY)) {
			ResultSet rsTags = preparedStatement.executeQuery();

			while (rsTags.next()) {
				int tagId = rsTags.getInt("id");
				String tagName = rsTags.getString("name");

				Tag tag = new Tag();
				tag.setId(tagId);
				tag.setName(tagName);

				TagDao.tags.put(tagId, tag);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Load posts with their author and section
		try (PreparedStatement preparedStatment = conn.prepareStatement(GET_ALL_POSTS_QUERY)) {
			ResultSet rsPosts = preparedStatment.executeQuery();

			while (rsPosts.next()) {
				int postId = rsPosts.getInt("id");
				String description = rsPosts.getString("description");
				String internetUrl = rsPosts.getString("internet_url");
				String localUrl = rsPosts.getString("local_url");
				boolean hasDownload = rsPosts.getBoolean("has_download");
				LocalDateTime dateTime = rsPosts.getTimestamp("date_time").toLocalDateTime();
				int sectionId = rsPosts.getInt("section_id");
				User author = UserDao.getInstance().getUserById(rsPosts.getInt("author_id"));
				Section section = SectionDao.getInstance().getSectionById(sectionId);
				
				Post post = new Post();
				post.setId(postId);
				post.setDescription(description);
				post.setLocalUrl(localUrl);
				post.setInternetUrl(internetUrl);
				post.setHasDownload(hasDownload);
				post.setDateTime(dateTime);
				post.setUser(author);
				post.setSection(section);
				PostDao.POSTS.put(postId, post);
			}
		}
		
		// load all tags of each post
				for (Post post : PostDao.POSTS.values()) {
					try (PreparedStatement pr = conn.prepareStatement(GET_ALL_TAGS_BY_POST_ID_QUERY)) {
						pr.setInt(1, post.getId());
						ResultSet rs = pr.executeQuery();

						HashSet<Integer> tagIds = new HashSet<>();
						while (rs.next()) {
							tagIds.add(rs.getInt("tag_id"));
						}
						
						TagDao tagRepository = TagDao.getInstance();
						for (Integer tagId : tagIds) {
							post.addTag(tagRepository.getTagById(tagId));
						}
					} catch (Exception e) {
						e.getMessage();
					}
				}

		// load rating for each post
		try (PreparedStatement pr = conn.prepareStatement(GET_RATING_FOR_POST_BY_ID)) {
			ResultSet rs = pr.executeQuery();
			
			while (rs.next()) {
				int postId = rs.getInt("post_id");
				int rating = rs.getInt("rating");
				
				PostDao.POSTS.get(postId).addRating(rating);
			}
		} catch (Exception e) {
			e.getMessage();
		}
		
		// Load comments with their post and author
		try (PreparedStatement preparedStatment = conn.prepareStatement(GET_ALL_COMMENTS_QUERY)) {
			ResultSet rsComments = preparedStatment.executeQuery();

			while (rsComments.next()) {
				int commentId = rsComments.getInt("id");
				String content = rsComments.getString("content");
				LocalDateTime dateTime = rsComments.getTimestamp("date_time").toLocalDateTime();
				Post post = PostDao.getInstance().getPostById(rsComments.getInt("post_id"));
				User author = UserDao.getInstance().getUserById(rsComments.getInt("author_id"));

				Comment comment = new Comment(commentId, content, author, post, dateTime);
				
//				comment.setId(commentId);
//				comment.setContent(content);
//				comment.setDateTime(dateTime);
//				comment.setPost(post);
//				comment.setUser(author);

				CommentDao.COMMENTS.put(commentId, comment);
			}
		}

		// load all comments of each post
		for (Post post : PostDao.POSTS.values()) {
			try (PreparedStatement pr = conn.prepareStatement(GET_ALL_COMENT_IDS_BY_POST_ID_QUERY)) {
				pr.setInt(1, post.getId());
				ResultSet rs = pr.executeQuery();

				HashSet<Integer> commentIds = new HashSet<>();
				while (rs.next()) {
					commentIds.add(rs.getInt("id"));
				}
				
				CommentDao commentRepository = CommentDao.getInstance();
				for (Integer commentId : commentIds) {
					post.addComment(commentRepository.getCommentById(commentId));
				}

			} catch (Exception e) {
				e.getMessage();
			}
		}

		

		// load all comments for each user
		for (User user : UserDao.users.values()) {
			try (PreparedStatement pr = conn.prepareStatement(GET_ALL_COMMENT_IDS_BY_USER_ID)) {
				pr.setInt(1, user.getId());
				ResultSet rs = pr.executeQuery();

				HashSet<Integer> commentIds = new HashSet<>();
				while (rs.next()) {
					commentIds.add(rs.getInt("id"));
				}
				
				CommentDao commentRepository = CommentDao.getInstance();
				for (Integer commentId : commentIds) {
					user.addComment(commentRepository.getCommentById(commentId));
				}
			} catch (Exception e) {
				e.getMessage();
			}
		}

		// load all posts for each user
		for (User user : UserDao.users.values()) {
			try (PreparedStatement pr = conn.prepareStatement(GET_ALL_POST_IDS_BY_USER_ID)) {
				pr.setInt(1, user.getId());
				ResultSet rs = pr.executeQuery();

				HashSet<Integer> postIds = new HashSet<>();
				while (rs.next()) {
					postIds.add(rs.getInt("id"));
				}

				PostDao postRepository = PostDao.getInstance();
				for (Integer postId : postIds) {
					user.addPost(postRepository.getPostById(postId));
				}
			} catch (Exception e) {
				e.getMessage();
			}
		}

		//load rated posts for each user
		for (User user : UserDao.users.values()) {
			try (PreparedStatement pr = conn.prepareStatement(GET_RATED_POST_IDS_BY_USER_ID)) {
				pr.setInt(1, user.getId());
				
				ResultSet rs = pr.executeQuery();
				HashSet<Integer> postIds = new HashSet<>();
				while (rs.next()) {
					postIds.add(rs.getInt("post_id"));
				}
				
				PostDao postRepository = PostDao.getInstance();
				for (Integer postId : postIds) {
					user.addRatedPost(postRepository.getPostById(postId));
				}
			} catch (Exception e) {
				e.getMessage();
			}
		}
		
		//load all posts for each sections
		for (Section section : SectionDao.SECTION.values()) {
			try (PreparedStatement pr = conn.prepareStatement(GET_POST_IDS_BY_SECTION_ID)) {
				pr.setInt(1, section.getId());
				
				ResultSet rs = pr.executeQuery();
				HashSet<Integer> postIds = new HashSet<>();
				while (rs.next()) {
					postIds.add(rs.getInt("id"));
				}
				
				PostDao postRepository = PostDao.getInstance();
				for (Integer postId : postIds) {
					section.addPost(postRepository.getPostById(postId));
				}
			} catch (Exception e) {
				e.getMessage();
			}
		}
		
		long loadingTime = System.currentTimeMillis() - startLoadingTime;
		System.out.println(String.format(SUCCESSFULLY_LOAD_DATABASE_MESSAGE, loadingTime));
	}
}
