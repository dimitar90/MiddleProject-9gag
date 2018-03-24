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

			// ���� �� ��������� ������ ��� ������ ���� executUpdate ����� ����� ���� ��
			// ���������� ��
			// ���� ������ ���� ������ �������� �� 1 ���� � ������ ������ 1 ��� ��� ��
			// ������ �������
			// � �� ���� ���������� � ��� ����� 1 ����� � �������/����������� �������
			int affectedRows = preparedStatement.executeUpdate();
			if (affectedRows == 1) {
				System.out.println(SUCCESSFULLY_REGISTRATION);
			}
		}

		User user = null;
		// ���� ����� ���� ������ ��� ������ �� �� ����� ���� �� ������������� ���� ��
		// �� ����
		// �� �� ������ � ���� � �������. �� ��������� ������� ����� ������ ���� ��
		// ������ ���� ������
		// �� ������ ������� ��(������ � ���� ���������) � ���� ����� ��-�� �� ���� ��
		// ����� ����
		// ������ �� �� ����� ������ � ��-�� ��, ����� � ����������� �� �� ��������
		try (PreparedStatement preparedStatement = DatabaseConnection.getConnection()
				.prepareStatement(GET_USER_BY_NAME_QUERY)) {
			preparedStatement.setString(1, username);

			//���� ���� ������� ������ executeQuery ������ �� �� ����� ���������� ������ ���� �����,
			//����� � executeUpdate, � �� ���� �������� �������� ������(� ���� ������ ������ �������
			//�� user-���� �� �������� �� ����� ���� 1 ���, � �� ������� �� �������� � ������
			//������� �������, ����� � ������� ��������. ��� ��� ������ �� 1 ��� ��������
			//�� reusltSet ����� ������ ������ ����� �� �������� � 
			//while(resultSet.next){
			//� ���� ��� �� ����� ����� ������� �������� �� ����� �� ����� ������ ���
			//�������� ��� ��� 3 ���� while �� �� ������� 3 ���� � 3 ���� �� �������
			//id, username, password, email �� �������� user-�
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
			// ���� ��� �� ������� �� �� ������� �� ��� � ��������� ���� ������ � �� �
			// �������������
			// ��������� � ��-�� � �.�., � ����� ���� �� �������� ��� �������� �� �
			// ������������
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

		// ���� �� ����� ������ ��� ������ �� ���� resultSet � ���� �� ���� username 
		try (PreparedStatement preparedStatement = DatabaseConnection.getConnection().prepareStatement(LOGIN_QUERY)) {
			preparedStatement.setString(1, username);

			//��� ��� ����� ����������� �� �����, ��� ���� resultSet.next() ���� false
			//������� � if � �������� exception �� ��������� ��� ��� ������
			ResultSet resultSet = preparedStatement.executeQuery();
			if (!resultSet.next()) {
				throw new UserException(LoginCommand.INVALID_USER_ARGUMENTS);
			} else {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("username");
				String heshedPassword = resultSet.getString("password");
				String email = resultSet.getString("email");
				
				//���� ���� ��� ��� �������� ������� �������� ���� �������� �� �����������
				//� plain text(password) ������� � ���������� ������, ����� ��� �������� �� ������
				//� ��� ���� ���������� ��� �������� exception �� ��������� ��� ��� ������
				//�� �� ������� ���� ���� ����� ��� �������� � ��������� ������ �����������
				//�� ������� �� ������ � ���������� �� ������
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
			
			//��� ���� � ���� ����� ����� ���� �� resultSet � ����� ���������
			//���� �� ����� �������� ������������ ����� � �� �� ������ � ����
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String username = resultSet.getString("username");
				String email = resultSet.getString("email");
				
				User user = new User();
				user.setId(id);
				user.setUsername(username);
				user.setEmail(email);
				
				this.users.put(user.getId(), user);
				//���� sout ���� � ���� �� �� ������� ����� ����� � ���� ���� ��������� ������
				//������ � ������ � �������� �� �� �������
				System.out.println("Load user: " + username + " id: " + id + " email: " + email);
			}
		}
	}
}
