package models;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import exceptions.PostException;
import repositories.TagRepository;
import utils.IDeserialize;

public class Post implements IDeserialize {
	private static final String FINAL_MESSAGE = "Download completed. . .";
	private static final int MULTIPLYER = 100;
	private static final double INITIAL_PERCENT_OF_DOWNLOAD = 0.00;
	private static final int KB = 1024;
	private static final String MESSAGE_INVALID_DESCRIPTION = "Give a funny, creative and descriptive title to the post would give the post a boost!";
	private static final String MESSAGE_INVALID_NAME = "Invalid parameters for name";
	private static final String PATH_RES = "C:\\Users\\Dimitar\\middleProject-9gag\\MiddleProject-9gag\\resources\\";

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
	private volatile boolean hasDownload;

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
		this.hasDownload = false;
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

	public boolean anyComments() {
		return this.commentIds.size() > 0;
	}

	public boolean isDownload() {
		return this.hasDownload;
	}

	public void setFlag(boolean hasUpload) {
		this.hasDownload = hasUpload;
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

		return "Post description: " + this.description + ". Author: " + user.getName() + " Content(Url): "
				+ this.imageUrl + System.lineSeparator() + "Post rating: " + rating + " Written on: " + this.date
				+ " Section: " + this.section.getName() + " Tags: " + String.join(", ", tagNames);
	}

	public void downloadImage() {

		String dirName = this.user.getName();
		dirName += this.user.getId();
		File file = new File(PATH_RES + dirName);

		try {
			if (!file.exists()) {
				file.mkdirs();
			}

			URL url = new URL(this.imageUrl);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			double fileSize = (double) http.getContentLengthLong();
			BufferedInputStream in = new BufferedInputStream(http.getInputStream());
			FileOutputStream fos = new FileOutputStream(file + "\\Image" + (this.tagIds) + ".jpg");

			BufferedOutputStream bout = new BufferedOutputStream(fos, KB);
			byte[] buffer = new byte[KB];

			double downloaded = INITIAL_PERCENT_OF_DOWNLOAD;
			int read = 0;
			double percentDownloaded = INITIAL_PERCENT_OF_DOWNLOAD;

			while ((read = in.read(buffer, 0, KB)) >= 0) {
				bout.write(buffer, 0, read);
				downloaded += read;
				percentDownloaded = (downloaded * MULTIPLYER) / fileSize;
				String percent = String.format("%.2f", percentDownloaded);
				System.out.println("Downloaded " + percent + "% of a file.");
			}
			bout.close();
			in.close();
			this.setFlag(true);
			System.out.println(FINAL_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}