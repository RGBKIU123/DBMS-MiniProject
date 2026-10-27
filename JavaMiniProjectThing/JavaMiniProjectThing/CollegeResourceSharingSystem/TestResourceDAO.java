
import java.sql.SQLException;
import java.util.List;

public class TestResourceDAO {

    public static void main(String[] args) {
        ResourceDAO dao = new ResourceDAO();

        try {
            List<Resource> resources = dao.getAllResources();

            System.out.println("All Resources:");

            for (Resource resource : resources) {
                resource.displayDetails();
            }

            Resource found = null;

            if (!resources.isEmpty()) {
                int id = resources.get(0).getResourceId();
                found = dao.getResourceById(id);

                if (found != null) {
                    System.out.println("\nResource found by ID:");
                    found.displayDetails();
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
