package repositories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import exceptions.SectionException;
import models.Section;

public class SectionDao {
	public static final Map<Integer, Section> SECTION = new HashMap<>();

	private static final String INVALID_SECTION = "Section not found";

	private static SectionDao sectionRepository;

	private SectionDao() {
	}

	public static SectionDao getInstance() {
		if (sectionRepository == null) {
			sectionRepository = new SectionDao();
		}

		return sectionRepository;
	}

	public List<String> getAllSectionNames() {
		return SECTION
				.values()
				.stream()
				.map(s -> s.getName())
				.collect(Collectors.toList());
	}

	public Section getSectionByName(String name) {
		if (!SECTION.values().stream().anyMatch(s -> s.getName().equals(name))) {
			return null;
		}

		return SECTION
				.values()
				.stream()
				.filter(s -> s.getName().equals(name))
				.findFirst()
				.get();
	}

	public Section getSectionById(int id) throws SectionException {
		if (!SECTION.containsKey(id)) {
			throw new SectionException(INVALID_SECTION);
		}

		return SECTION.get(id);
	}

}
