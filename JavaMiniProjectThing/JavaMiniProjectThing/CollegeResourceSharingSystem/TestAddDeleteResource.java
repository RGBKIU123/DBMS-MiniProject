
import java.sql.SQLException;
import java.util.List;

public class TestAddDeleteResource {

    public static void main(String[] args) {
        ResourceDAO dao = new ResourceDAO();

        try {
            List<Resource> resources = dao.getAllResources();

            System.out.println("Current resource count: "
                    + resources.size());

            System.out.println(
                    "Deletion test will be done after adding "
                    + "a separate test resource.");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
