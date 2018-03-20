package repositories;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.sql.rowset.serial.SerialException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import exceptions.SectionException;
import exceptions.SerializeException;
import models.Section;
import utils.JsonSerializer;

public class SectionRepository {
	private static final String ALREADY_EXIST_MESSAGE = "Section already exist!";

	public static final String SECTION_PATH = "sections.json";
	
	private static SectionRepository sectionRepository;
	private Map<Integer, Section> sections;
	
	private SectionRepository() {
		this.sections = new HashMap<>();
	}
	
	public static SectionRepository getInstance() {
		if (sectionRepository == null) {
			sectionRepository = new SectionRepository();
		}
		
		return sectionRepository;
	}
	
	public Section getSectionByName(String name) {
		if (!this.sections.values().stream().anyMatch(s -> s.getName().equals(name))) {
			return null;
		}
		
		return this.sections.values().stream().filter(s -> s.getName().equals(name)).findFirst().get();
	}
	
	public Section addSection(String name) throws SectionException {
		if (this.sections.values().stream().anyMatch(s -> s.getName().equals(name))) {
			throw new SectionException(ALREADY_EXIST_MESSAGE);
		}
		
		Section section = new Section(name);
		this.sections.put(section.getId(), section);
		
		return section;
	}
	
	public int getLastId() {
		if (this.sections == null || this.sections.size() == 0) {
			return 0;
		}
		
		return this.sections
					.values()
					.stream()
					.map(s -> s.getId())
					.max((id1, id2) -> Integer.compare(id2, id1))
					.get();
	}
	
	public void exportSections() throws SerializeException, SerialException {
		JsonSerializer serializer = new JsonSerializer();
		serializer.serialize(this.sections, SECTION_PATH);
	}

	public void deserialize() throws IOException {
		File file = new File(SECTION_PATH);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		StringBuilder sb = new StringBuilder();

		try (Scanner sc = new Scanner(file)) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				sb.append(line);
			}
		}

		Map<Integer, Section> map = gson.fromJson(sb.toString(), new TypeToken<Map<Integer, Section>>() {
		}.getType());

		this.sections = map;
	}

	public List<String> getAllSectionNames() {
		return this.sections
				.values()
				.stream()
				.map(s -> s.getName())
				.collect(Collectors.toList());
	}
}
