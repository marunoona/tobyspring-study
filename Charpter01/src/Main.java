import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/testdb?serverTimezone=UTC", "marunoona", "maru1"
            );
            System.out.println("성공");
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("오류");
            e.printStackTrace();
        }
    }
}
