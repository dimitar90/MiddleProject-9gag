package repositories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import commands.LoginCommand;
import connection.DatabaseConnection;
import exceptions.UserException;
import models.User;
import utils.Crypt;
import utils.Session;

public class UserRepository {
	public static final Map<Integer, User> users = new HashMap<Integer, User>();

	private static final String ALREADY_EXIST = "Username already exist!";
	private static final String SUCCESSFULLY_REGISTRATION = "Successfully registration!";
	private static final String VIEW_DATA_USER = "Registered user: id: %d, username: %s, email: %s";
	private static final String NOT_EXISTS_USER = "User does not exist!";
	
	private static final String INSERT_USER_QUERY =  "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
	private static final String GET_ALL_USERS_QUERY = "SELECT id, username, email FROM users";
	private static final String CHECK_EXIST_USER_QUERY = "SELECT * FROM users WHERE username = ?";
	private static final String LOGIN_QUERY = "SELECT * FROM users WHERE username = ?";

	private static UserRepository userRepository;

	private UserRepository() {
	}

	public static UserRepository getInstance() {
		if (userRepository == null) {
			userRepository = new UserRepository();
		}

		return userRepository;
	}

	public void addUser(String username, String password, String email) throws UserException, SQLException {
		if (this.isUserExistByName(username)) {
			throw new UserException(ALREADY_EXIST);
		}

		try (PreparedStatement preparedStatement = DatabaseConnection.getConnection()
				.prepareStatement(INSERT_USER_QUERY, new String[] { "id" })) {
			// taka se setvat argumentite kum zaqvkata koqto izpra6tame kum bazata
			// 1,2,3 sa vse edno indexite na elementite a v samata zaqvka se otbelqzvat s
			// "?"
			password = Crypt.hashPassword(password);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			preparedStatement.setString(3, email);
			preparedStatement.executeUpdate();

			System.out.println(SUCCESSFULLY_REGISTRATION);
			ResultSet rs = preparedStatement.getGeneratedKeys();

			if (rs != null && rs.next()) {
				// ���� ��-�� �� ����� �� ����� �� ������ ������ �� ��� �� ������ ���� �� ������
				int id = rs.getInt(1);
				User user = new User();
				user.setId(id);
				user.setUsername(username);
				user.setEmail(email);
				users.put(id, user);
				// ���� ��� �� ������� �� �� ������� �� ��� � ��������� ���� ������ � �� �
				// �������������
				// ��������� � ��-�� � �.�., � ����� ���� �� �������� ��� �������� �� �
				// ������������
				System.out.println(String.format(VIEW_DATA_USER, id, username, email));
			}
		}
	}
	
	public User getUserById(int id)  {
		if (!users.containsKey(id)) {
			return null;
		}
		
		return users.get(id);
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

		// ���� �� ����� ������ ��� ������ �� ���� resultSet � ���� �� ���� username
		try (PreparedStatement preparedStatement = DatabaseConnection.getConnection().prepareStatement(LOGIN_QUERY)) {
			preparedStatement.setString(1, username);

			// ��� ��� ����� ����������� �� �����, ��� ���� resultSet.next() ���� false
			// ������� � if � �������� exception �� ��������� ��� ��� ������
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!resultSet.next()) {
				throw new UserException(LoginCommand.INVALID_USER_ARGUMENTS);
			} else {
				int id = resultSet.getInt("id");
				String heshedPassword = resultSet.getString("password");

				// ���� ���� ��� ��� �������� ������� �������� ���� �������� �� �����������
				// � plain text(password) ������� � ���������� ������, ����� ��� �������� ��
				// ������
				// � ��� ���� ���������� ��� �������� exception �� ��������� ��� ��� ������
				// �� �� ������� ���� ���� ����� ��� �������� � ��������� ������ �����������
				// �� ������� �� ������ � ���������� �� ������
				if (!Crypt.checkPassword(password, heshedPassword)) {
					throw new UserException(LoginCommand.INVALID_USER_ARGUMENTS);
				}

				user = users.get(id);
			}
		}

		Session.getInstance().setUser(user);
		return user;
	}
}
