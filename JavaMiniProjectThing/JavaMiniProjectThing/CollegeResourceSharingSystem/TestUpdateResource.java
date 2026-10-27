
import java.sql.SQLException;

public class TestUpdateResource {

    public static void main(String[] args) {
        ResourceDAO dao = new ResourceDAO();

        try {
            Resource resource = dao.getResourceById(1);

            if (resource == null) {
                System.out.println("Resource not found.");
                return;
            }

            String oldDescription = resource.getDescription();

            resource.setDescription(
                    "Updated description for testing CRUD"
            );

            boolean updated = dao.updateResource(resource);

            if (updated) {
                System.out.println("Resource updated successfully.");
            } else {
                System.out.println("Resource was not updated.");
            }

            Resource saved = dao.getResourceById(1);

            if (saved != null) {
                System.out.println("Saved description: "
                        + saved.getDescription());
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
