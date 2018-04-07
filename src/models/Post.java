package models;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import exceptions.PostException;

public class Post {
	private static final String FINAL_MESSAGE = "Download completed. . .";
	private static final int MULTIPLYER = 100;
	private static final double INITIAL_PERCENT_OF_DOWNLOAD = 0.00;
	private static final int KB = 1024;
	private static final String MESSAGE_INVALID_DESCRIPTION = "Give a funny, creative and descriptive title to the post would give the post a boost!";
	private static final String MESSAGE_INVALID_NAME = "Invalid parameters for name";
	private static final String PATH_RES = "resources" + File.separator;

	private int id;
	private String internetUrl;
	private String localUrl;
	public void setId(int id) {
		this.id = id;
	}

	private LocalDateTime dateTime;
	private User user;
	private String description;
	private Set<Comment> comments;
	private Set<Tag> tags;
	private List<Integer> ratings;
	private Section section;
	private volatile boolean hasDownload;

	public Post() {
		this.comments = new TreeSet<Comment>((c1, c2) -> c2.getDateTime().compareTo(c1.getDateTime()));
		this.tags = new HashSet<>();
		this.ratings = new ArrayList<>();
	}

	public Post(String description, String url, Section section) throws PostException {
		this();
		this.setDescription(description);
		this.internetUrl = url;
		this.dateTime = LocalDateTime.now();
		this.section = section;
		this.hasDownload = false;
	}
	
	public String getInternetUrl() {
		return internetUrl;
	}

	public void setInternetUrl(String internetUrl) {
		this.internetUrl = internetUrl;
	}

	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}

	public boolean isHasDownload() {
		return hasDownload;
	}

	public void setHasDownload(boolean hasDownload) {
		this.hasDownload = hasDownload;
	}

	public void addComment(Comment comment) {
		this.comments.add(comment);
	}
	
	public void removeComment(Comment comment) {
		if (this.comments.contains(comment)) {
			this.comments.remove(comment);
		}
	}

	public void setDescription (String description) {
		this.description = description;
	}

	public void addTag(Tag tag) {
		this.tags.add(tag);
	}

	public Set<Tag> getTags() {
		return Collections.unmodifiableSet(this.tags);
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

	public String getDescription() {
		return description;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public String getLocalUrl() {
		return localUrl;
	}

	public void setLocalUrl(String localUrl) {
		this.localUrl = localUrl;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public void addRating(int rating) {
		this.ratings.add(rating);
	}

	public int getRating() {
		return this.ratings.stream().mapToInt(r -> r).sum();
	}

	public boolean anyComments() {
		return this.comments.size() > 0;
	}

	public boolean isDownload() {
		return this.hasDownload;
	}

	public void setFlag(boolean hasUpload) {
		this.hasDownload = hasUpload;
	}
	
	public Set<Comment> getAllComments() {
		return Collections.unmodifiableSet(this.comments);
	}

	public void downloadImage() {
		String dirName = this.user.getUsername();
		File file = new File(PATH_RES + dirName);

		try {
			if (!file.exists()) {
				file.mkdirs();
			}

			URLConnection url = new URL(this.internetUrl).openConnection();
			url.addRequestProperty("User-Agent", 
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
			
			double fileSize = (double) url.getContentLengthLong();
			BufferedInputStream in = new BufferedInputStream(url.getInputStream());
			String localPath = file + File.separator + "image" + this.id + ".jpg";
			FileOutputStream fos = new FileOutputStream(localPath);

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
			this.localUrl = localPath;
			System.out.println(FINAL_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int hashCode() {
		return this.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Post)) {
			return false;
		}
		
		Post post = (Post) obj;
		return this.id == post.id;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		List<String> tagNames = this.tags.stream().map(t -> t.getName()).collect(Collectors.toList());
		
		sb.append("Post description: ").append(this.description).append(System.lineSeparator());
		sb.append("Post internetUrl: ").append(this.internetUrl).append(System.lineSeparator());
		sb.append("Post section: ").append(this.section.getName()).append(System.lineSeparator());
		sb.append("Post rating: ").append(this.getRating()).append(System.lineSeparator());
		sb.append("Author: ").append(this.user.getUsername()).append(System.lineSeparator());
		sb.append("Wrriten on: ").append(this.dateTime).append(System.lineSeparator());
		sb.append("Tags: ").append(String.join(", ", tagNames)).append(System.lineSeparator());
		this.comments.forEach(c -> sb.append(c));
		
		return sb.toString();
	}
}