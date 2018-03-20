package models;

import utils.IDeserialize;

public class Tag implements IDeserialize{
	private static int idGenerator = 0;
	private int id;
	private String name;

	public Tag(String name) {
		this.id = ++idGenerator;
		this.setName(name);
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
	
	public static void setValueToIdGenerator(int lastId) {
		idGenerator = lastId;
	}
}
