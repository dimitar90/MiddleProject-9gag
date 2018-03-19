package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import exceptions.PostException;
import repositories.TagRepository;
import utils.IDeserialize;

public class Post implements IDeserialize{
	private static final String MESSAGE_INVALID_DESCRIPTION = "Give a funny, creative and descriptive title to the post would give the post a boost!";
	private static final String MESSAGE_INVALID_NAME = "Invalid parameters for name";
	private static int nextPostId;
	private int id;
	private String name;
	private String imageUrl;
	private Date date;
	private User user;
	private String description;
	private Set<Integer> commentIds;
	private Tag tag;
	private List<Integer> ratings;

	static {
		nextPostId = 0;
	}

	public Post(String name, String description, String url, Tag tag) throws PostException {
		this.setDescription(description);
		this.setName(name);
		this.id = ++nextPostId;
		this.commentIds = new HashSet<>();
		this.imageUrl = url;
		this.tag = tag;
		this.date = new Date();
		this.ratings = new ArrayList<>();
	}

	public void addComment(int commentId) {
		this.commentIds.add(commentId);
	}

	private void setName(String name) throws PostException {
		if (name != null && name.length() >= 2) {
			this.name = name;
		} else {
			throw new PostException(MESSAGE_INVALID_NAME);
		}

	}

	private void setDescription(String description) throws PostException {
		if (description != null && description.length() >= 5) {
			this.description = description;
		} else {
			throw new PostException(MESSAGE_INVALID_DESCRIPTION);
		}

	}

	public static void setValueToIdPostGenerator(int lastId) {
		nextPostId = lastId;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		if (tag != null) {
			this.tag = tag;
		}
	}

	public int getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		if (user != null) {
			this.user = user;
		}
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void addRating(int rating) {
		this.ratings.add(rating);
	}

	public int getRating() {
		if (this.ratings.size() == 0) {
			return 0;
		}

		return (int) this.ratings.stream().mapToInt(r -> r).average().getAsDouble();
	}

	@Override
	public String toString() {
		double rating = this.getRating();
		
		return "Post: " + this.name + ". Description: " + this.description + ". Author: " + user.getName() + " Content(Url): " + this.imageUrl 
				+ System.lineSeparator() + "Post rating: " + rating  + " Written on: " + this.date
				 + " Tag: " + this.tag.getName();
	}

	public boolean anyComments() {
		return this.commentIds.size() > 0;
	}

}
