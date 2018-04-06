package repositories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import exceptions.SectionException;
import models.Section;


public class SectionRepository {
	public static final Map<Integer,Section> sections = new HashMap<>();
	
	private static final String INVALID_SECTION = "Section not found";
	
	private static SectionRepository sectionRepository;
	
	private SectionRepository() {
	}
	
	public static SectionRepository getInstance() {
		if (sectionRepository == null) {
			sectionRepository = new SectionRepository();
		}
		
		return sectionRepository;
	}
	
	public Section getSectionByName(String name) {
		if (!sections.values().stream().anyMatch(s -> s.getName().equals(name))) {
			return null;
		}
		
		return sections.values().stream().filter(s -> s.getName().equals(name)).findFirst().get();
	}
	
	public Section getSectionById(int id) throws SectionException {
		if (!sections.containsKey(id)) {
			throw new SectionException(INVALID_SECTION);
		}
		
		return sections.get(id);
	}
	
	public List<String> getAllSectionNames() {
		return sections
				.values()
				.stream()
				.map(s -> s.getName())
				.collect(Collectors.toList());
	}
}
