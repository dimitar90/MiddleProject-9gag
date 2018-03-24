package repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import connection.DatabaseConnection;
import models.Section;


public class SectionRepository {
	private static final String ALREADY_EXIST_MESSAGE = "Section already exist!";

	public static final String SECTION_PATH = "sections.json";

	private static final String GET_ALL_SECTIONS_QUERY = "SELECT id, name FROM sections";
	
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
	
	public List<String> getAllSectionNames() {
		return this.sections
				.values()
				.stream()
				.map(s -> s.getName())
				.collect(Collectors.toList());
	}

	public void loadSectionsFromDatabase() throws SQLException {
		try (PreparedStatement preparedStatment 
				= DatabaseConnection.getConnection().prepareStatement(GET_ALL_SECTIONS_QUERY)){
			ResultSet resultSet = preparedStatment.executeQuery();
			
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				
				Section section = new Section();
				section.setId(id);
				section.setName(name);
				
				this.sections.put(section.getId(), section);
				//този sout също е само за да гледаме какво става и вече като завършиме цялата
				//работа с базата и тестваме ще го изтрием
				System.out.println("Load section: " + section.getName() + " id: " + section.getId());
			}
		}
		
	}
}
