package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/9gagdatabase?useSSL=false";

    private static final String USER = "root";

    private static final String PASSWORD = "1234";

    private static Connection connetion;
    
    public static Connection getConnection() throws SQLException {
    	if (connetion == null) {
    		connetion = DriverManager.getConnection(URL, USER, PASSWORD);
		}
    	
        return connetion;
    }
}
