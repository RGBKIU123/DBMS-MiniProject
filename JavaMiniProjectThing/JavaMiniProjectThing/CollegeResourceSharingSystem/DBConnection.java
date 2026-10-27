
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection getConnection()
            throws SQLException {

        String url =
            "jdbc:postgresql://localhost:5432/college_resource_sharing";

        String username = "postgres";
        String password = "bts183001";

        return DriverManager.getConnection(
            url, username, password
        );
    }
}
