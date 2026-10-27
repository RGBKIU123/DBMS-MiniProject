
import java.sql.Connection;
import java.sql.SQLException;

public class TestDB {

    public static void main(String[] args) {

        try (Connection con = DBConnection.getConnection()) {
            System.out.println("Database connected successfully!");

        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            System.out.println(e.getMessage());
        }
    }
}
