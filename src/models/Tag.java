package models;

public class Tag {
	private int id;
	private String name;

	public Tag() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
