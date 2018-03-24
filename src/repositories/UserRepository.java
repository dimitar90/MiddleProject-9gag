package repositories;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.sql.rowset.serial.SerialException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import commands.LoginCommand;
import connection.DatabaseConnection;
import exceptions.DeserializeException;
import exceptions.SerializeException;
import exceptions.UserException;
import models.User;
import utils.Crypt;
import utils.IDeserialize;
import utils.JsonSerializer;
import utils.Query;
import utils.Session;

public class UserRepository {
	private static final String ALREADY_EXIST = "Username already exist!";
	private static final String SUCCESSFULLY_REGISTRATION = "Successfully registration!";
	private static final String VIEW_DATA_USER = "Registered user: id: %d, username: %s, : password: %s, email: %s";

	private static final String USER_PATH = "users.json";

	private static final String INSERT_USER_QUERY = Query.getInstance().getInsertQuery(new User());
	private static final String GET_ALL_USERS_QUERY = "SELECT id, username, email FROM users";
	private static final String CHECK_EXIST_USER_QUERY = "SELECT * FROM users WHERE username = ?";
	private static final String LOGIN_QUERY = "SELECT * FROM users WHERE username = ?";
	private static final String GET_USER_BY_NAME_QUERY = "SELECT * FROM users WHERE username = ?";

	private static UserRepository userRepository;
	private Map<Integer, User> users;
	private JsonSerializer serializer;

	private UserRepository() {
		this.serializer = new JsonSerializer();
		this.users = new HashMap<>();
	}

	public static UserRepository getInstance() {
		if (userRepository == null) {
			userRepository = new UserRepository();
		}

		return userRepository;
	}

	public User addUser(String username, String password, String email) throws UserException, SQLException {
		if (this.isUserExistByName(username)) {
			throw new UserException(ALREADY_EXIST);
		}

		try (PreparedStatement preparedStatement = DatabaseConnection.getConnection()
				.prepareStatement(INSERT_USER_QUERY)) {
			// taka se setvat argumentite kum zaqvkata koqto izpra6tame kum bazata
			// 1,2,3 sa vse edno indexite na elementite a v samata zaqvka se otbelqzvat s
			// "?"
			password = Crypt.hashPassword(password);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			preparedStatement.setString(3, email);

			// тука се изпълнява заявка към базата като executUpdate връща колко реда са
			// афектирани от
			// тази заявка като реално вкарване на 1 юзър в базата добавя 1 ред ако се
			// добави успешно
			// и за това проверката е ако върне 1 значи е добавен/регистриран успешно
			int affectedRows = preparedStatement.executeUpdate();
			if (affectedRows == 1) {
				System.out.println(SUCCESSFULLY_REGISTRATION);
			}
		}

		User user = null;
		// тука правя нова заявка към базата да ми върне току що регистрирания юзър за
		// да мога
		// да го вкарам в мапа с ъзърите. Не използвам горните данни понеже като се
		// добави юзър базата
		// му добавя някакво ид(понеже е ауто инкремент) а горе нямам ид-то за това се
		// прави нова
		// заявка да го върне заедно с ид-то му, което в последствие ще се използва
		try (PreparedStatement preparedStatement = DatabaseConnection.getConnection()
				.prepareStatement(GET_USER_BY_NAME_QUERY)) {
			preparedStatement.setString(1, username);

			//тука вече правиме заявка executeQuery понеже тя не връща афектирани редове като число,
			//както е executeUpdate, а ни дава всичките намерени редове(в този случай понеже имената
			//на user-рите са уникални ще върне само 1 ред, и по имената на колоните в базата
			//взимаме данните, които е върнала заявката. Ако има повече от 1 ред резултат
			//се reusltSet връща повече редове които се обикалят с 
			//while(resultSet.next){
			//и тука пак по същия начин взимаме колоните по имена от всеки върнат ред
			//примерно ако има 3 реда while ще се завърти 3 пъти и 3 пъти ще вземеме
			//id, username, password, email на различни user-и
			//}
			ResultSet resultSet = preparedStatement.executeQuery();

			int id = resultSet.getInt("id");
			String uName = resultSet.getString("username");
			String pass = resultSet.getString("password");
			String em = resultSet.getString("email");

			user = new User();
			user.setId(id);
			user.setUsername(uName);
			user.setEmail(em);

			this.users.put(id, user);
			// това съм го оставил да си гледаме за нас в конзолата дали всичко е ОК с
			// регистрацията
			// връщането с ид-то и т.н., а самия юзър го добавяме без паролата му в
			// репоситорито
			System.out.println(String.format(VIEW_DATA_USER, id, uName, pass, em));

		}

		return user;
	}

	private boolean isUserExistByName(String username) throws SQLException {
		// tuka pravim proverka dali user s takuv username sushtestvuva v bazata
		try (PreparedStatement preparedStatement = DatabaseConnection.getConnection()
				.prepareStatement(CHECK_EXIST_USER_QUERY)) {
			preparedStatement.setString(1, username);

			ResultSet resultSet = preparedStatement.executeQuery();

			// vij zaqvkata gore priema argument username i ako ima takuv user shte vurne
			// rezultat v result set, ako nqma
			// nqma da vurne i tova se razbira s resultSet.next();
			if (resultSet.next()) {
				return true;
			} else {
				return false;
			}
		}
	}

	public User login(String username, String password) throws SQLException, UserException {
		User user = null;

		// тука се прави заявка към базата да даде resultSet с реда от този username 
		try (PreparedStatement preparedStatement = DatabaseConnection.getConnection().prepareStatement(LOGIN_QUERY)) {
			preparedStatement.setString(1, username);

			//ако има такъв регистриран го връща, ако няма resultSet.next() дава false
			//влизаме в if и хвърляме exception за невалидно име или парола
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!resultSet.next()) {
				throw new UserException(LoginCommand.INVALID_USER_ARGUMENTS);
			} else {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("username");
				String heshedPassword = resultSet.getString("password");
				String email = resultSet.getString("email");
				
				//тука вече ако има резултат правиме проверка дали паролата от потребителя
				//в plain text(password) съвпада с хешираната парола, която сме извлекли от базата
				//и ако няма съвпадение пак хвърляме exception за невалидно име или парола
				//не се изписва дали само името или паролата е невалидна заради предпазване
				//от хакване на профил с налучкване на парола
				if (!Crypt.checkPassword(password, heshedPassword)) {
					throw new UserException(LoginCommand.INVALID_USER_ARGUMENTS);
				}

				user = new User();
				user.setId(id);
				user.setUsername(name);
				user.setEmail(email);
			}
		}

		Session.getInstance().setUser(user);
		return user;
	}

	public void loadUsersFromDatabase() throws SQLException {
		try (PreparedStatement preparedStatment 
				= DatabaseConnection.getConnection().prepareStatement(GET_ALL_USERS_QUERY)){
			ResultSet resultSet = preparedStatment.executeQuery();
			
			//ето тука е това което писах горе за resultSet с много резултати
			//това ще върне всичките регистрирани юзъри и ще ги зареди в мапа
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String username = resultSet.getString("username");
				String email = resultSet.getString("email");
				
				User user = new User();
				user.setId(id);
				user.setUsername(username);
				user.setEmail(email);
				
				this.users.put(user.getId(), user);
				//този sout също е само за да гледаме какво става и вече като завършиме цялата
				//работа с базата и тестваме ще го изтрием
				System.out.println("Load user: " + username + " id: " + id + " email: " + email);
			}
		}
	}
}
