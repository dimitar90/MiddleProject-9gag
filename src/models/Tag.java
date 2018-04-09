package models;

import exceptions.TagException;

public class Tag {
	private static final String MSG_INVALID_TAG_NAME = "Invalig name";
	private static final String MSG_INVALID_TAG_ID = "Invalid id";
	private int id;
	private String name;

	public Tag() {

	}

	public Tag(int id, String name) throws TagException {
		this(name);
		this.setId(id);
	}

	public Tag(String name) throws TagException {
		this.setName(name);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) throws TagException {
		if (id > 0) {
			this.id = id;
		} else {
			throw new TagException(MSG_INVALID_TAG_ID);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) throws TagException {
		if (name != null) {
			this.name = name;
		} else {
			throw new TagException(MSG_INVALID_TAG_NAME);
		}

	}

	@Override
	public int hashCode() {
		return this.id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Tag)) {
			return false;
		}

		Tag tag = (Tag) obj;
		return this.id == tag.id;
	}
}
