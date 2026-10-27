
import java.sql.SQLException;
import java.util.List;

public class TestResourceSearch {

    public static void main(String[] args) {
        ResourceDAO dao = new ResourceDAO();

        try {
            List<Resource> results =
                    dao.searchResources("calculator");

            if (results.isEmpty()) {
                System.out.println("No matching resources found.");
            } else {
                System.out.println("Matching resources:");

                for (Resource resource : results) {
                    resource.displayDetails();
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
