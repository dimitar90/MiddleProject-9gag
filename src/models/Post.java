package models;

import java.util.ArrayList;
import java.util.Collections;
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
	private String imageUrl;
	private Date date;
	private User user;
	private String description;
	private Set<Integer> commentIds;
	private Set<Integer> tagIds;
	private List<Integer> ratings;
	private Section section;
	
	static {
		nextPostId = 0;
	}

	public Post(String description, String url, Section section) throws PostException {
		this.setDescription(description);
		this.id = ++nextPostId;
		this.commentIds = new HashSet<>();
		this.imageUrl = url;
		this.tagIds = new HashSet<>();
		this.date = new Date();
		this.ratings = new ArrayList<>();
		this.section = section;
	}

	public void addComment(int commentId) {
		this.commentIds.add(commentId);
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

	public void addTagId(int tagId) {
		this.tagIds.add(tagId);
	}
	
	public Set<Integer> getTagIds() {
		return Collections.unmodifiableSet(this.tagIds);
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
		return this.ratings.stream().mapToInt(r -> r).sum();
	}

	@Override
	public String toString() {
		int rating = this.getRating();
		TagRepository tagRepository = TagRepository.getInstance();
		Set<String> tagNames = new HashSet<>();
		
		for (Integer id : this.tagIds) {
			Tag tag = tagRepository.getTagById(id);
			if (tag != null) {
				tagNames.add(tag.getName());
			}
		}
		
		return "Post description: " + this.description + ". Author: " + user.getName() + " Content(Url): " + this.imageUrl 
				+ System.lineSeparator() + "Post rating: " + rating  + " Written on: " + this.date
				 + " Section: " + this.section.getName()  + " Tags: " + String.join(", ", tagNames);
	}

	public boolean anyComments() {
		return this.commentIds.size() > 0;
	}

}
