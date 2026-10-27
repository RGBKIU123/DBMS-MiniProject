
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResourceDAO {

    public boolean addResource(Resource resource) throws SQLException {
        String sql = "INSERT INTO resources "
                + "(name, category, description, item_condition, owner_id, available, "
                + "author, subject, edition, note_type, equipment_type, laboratory, "
                + "brand, model, has_cover) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, resource.getName());
            ps.setString(2, resource.getCategory());
            ps.setString(3, resource.getDescription());
            ps.setString(4, resource.getCondition());
            ps.setInt(5, resource.getOwner().getStudentId());
            ps.setBoolean(6, resource.isAvailable());

            ps.setString(7, null);
            ps.setString(8, null);
            ps.setObject(9, null);
            ps.setString(10, null);
            ps.setString(11, null);
            ps.setString(12, null);
            ps.setString(13, null);
            ps.setString(14, null);
            ps.setObject(15, null);

            if (resource instanceof Textbook) {
                Textbook book = (Textbook) resource;
                ps.setString(7, book.getAuthor());
                ps.setString(8, book.getSubject());
                ps.setInt(9, book.getEdition());
            } else if (resource instanceof Notes) {
                Notes notes = (Notes) resource;
                ps.setString(7, notes.getAuthor());
                ps.setString(8, notes.getSubject());
                ps.setString(10, notes.getNoteType());
            } else if (resource instanceof LabEquipment) {
                LabEquipment equipment = (LabEquipment) resource;
                ps.setString(11, equipment.getEquipmentType());
                ps.setString(12, equipment.getLaboratory());
            } else if (resource instanceof ScientificCalculator) {
                ScientificCalculator calculator = (ScientificCalculator) resource;
                ps.setString(13, calculator.getBrand());
                ps.setString(14, calculator.getModel());
                ps.setBoolean(15, calculator.hasCover());
            }

            return ps.executeUpdate() > 0;
        }
    }

    public List<Resource> getAllResources() throws SQLException {
        List<Resource> resources = new ArrayList<>();

        String sql = "SELECT r.*, s.name AS owner_name, s.email, s.phone, "
                + "s.department, s.year_of_study "
                + "FROM resources r JOIN students s "
                + "ON r.owner_id = s.student_id "
                + "ORDER BY r.resource_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Student owner = new Student(
                        rs.getInt("owner_id"),
                        rs.getString("owner_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("department"),
                        rs.getInt("year_of_study")
                );

                Resource resource = createResource(rs, owner);

                resource.setResourceId(rs.getInt("resource_id"));
                resource.setAvailable(rs.getBoolean("available"));

                resources.add(resource);
            }
        }

        return resources;
    }

    private Resource createResource(ResultSet rs, Student owner)
            throws SQLException {

        String category = rs.getString("category");
        String name = rs.getString("name");
        String description = rs.getString("description");
        String condition = rs.getString("item_condition");

        switch (category.toLowerCase()) {
            case "textbook":
                return new Textbook(
                        name, description, condition, owner,
                        rs.getString("author"),
                        rs.getString("subject"),
                        rs.getInt("edition")
                );

            case "notes":
                return new Notes(
                        name, description, condition, owner,
                        rs.getString("author"),
                        rs.getString("subject"),
                        rs.getString("note_type")
                );

            case "lab equipment":
                return new LabEquipment(
                        name, description, condition, owner,
                        rs.getString("equipment_type"),
                        rs.getString("laboratory")
                );

            case "scientific calculator":
                return new ScientificCalculator(
                        name, description, condition, owner,
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getBoolean("has_cover")
                );

            default:
    return new OtherResource(
        rs.getString("name"),
        category,
        rs.getString("description"),
        rs.getString("item_condition"),
        owner
    );
        }
    }

    public Resource getResourceById(int id) throws SQLException {
        for (Resource resource : getAllResources()) {
            if (resource.getResourceId() == id) {
                return resource;
            }
        }

        return null;
    }

    
    public List<Resource> searchResources(String term) throws SQLException {
        List<Resource> results = new ArrayList<>();

        for (Resource resource : getAllResources()) {
            if (resource.matchesSearch(term)) {
                results.add(resource);
            }
        }

        return results;
    }

    public boolean updateResource(Resource resource) throws SQLException {
        String sql = "UPDATE resources "
                + "SET name = ?, description = ?, item_condition = ?, available = ? "
                + "WHERE resource_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, resource.getName());
            ps.setString(2, resource.getDescription());
            ps.setString(3, resource.getCondition());
            ps.setBoolean(4, resource.isAvailable());
            ps.setInt(5, resource.getResourceId());

            return ps.executeUpdate() > 0;
        }
    }

    

public boolean deleteResource(int id) throws SQLException {
    String checkSql =
            "SELECT COUNT(*) FROM borrow_requests WHERE resource_id = ?";
    String deleteSql =
            "DELETE FROM resources WHERE resource_id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement check = con.prepareStatement(checkSql)) {

        check.setInt(1, id);

        try (ResultSet rs = check.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException(
                    "Cannot delete this resource because borrowing requests are linked to it."
                );
            }
        }

        try (PreparedStatement ps = con.prepareStatement(deleteSql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}

}