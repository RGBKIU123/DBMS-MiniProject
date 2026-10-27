
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BorrowRequestDAO {

    public int createRequest(int studentId, int resourceId)
            throws SQLException {

        String checkSql =
                "SELECT owner_id, available FROM resources "
                + "WHERE resource_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement check = con.prepareStatement(checkSql)) {

            check.setInt(1, resourceId);

            try (ResultSet rs = check.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Resource not found.");
                }

                int ownerId = rs.getInt("owner_id");
                boolean available = rs.getBoolean("available");

                if (ownerId == studentId) {
                    throw new SQLException(
                            "You cannot request your own resource.");
                }

                if (!available) {
                    throw new SQLException(
                            "This resource is not available.");
                }
            }

            String sql =
                    "INSERT INTO borrow_requests "
                    + "(student_id, resource_id, status, request_date) "
                    + "VALUES (?, ?, 'PENDING', CURRENT_DATE)";

            try (PreparedStatement ps = con.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, studentId);
                ps.setInt(2, resourceId);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        }

        throw new SQLException("Could not create borrowing request.");
    }

    public List<String> getAllRequests() throws SQLException {
        List<String> requests = new ArrayList<>();

        String sql =
                "SELECT br.request_id, br.student_id, s.name AS requester, "
                + "br.resource_id, r.name AS resource, br.status, br.request_date "
                + "FROM borrow_requests br "
                + "JOIN students s ON br.student_id = s.student_id "
                + "JOIN resources r ON br.resource_id = r.resource_id "
                + "ORDER BY br.request_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String row =
                        "Request ID: " + rs.getInt("request_id")
                        + " | Requester: " + rs.getString("requester")
                        + " (ID " + rs.getInt("student_id") + ")"
                        + " | Resource: " + rs.getString("resource")
                        + " (ID " + rs.getInt("resource_id") + ")"
                        + " | Status: " + rs.getString("status")
                        + " | Date: " + rs.getDate("request_date");

                requests.add(row);
            }
        }

        return requests;
    }

    public boolean rejectRequest(int requestId, int ownerId)
            throws SQLException {

        String sql =
                "UPDATE borrow_requests br "
                + "SET status = 'REJECTED' "
                + "FROM resources r "
                + "WHERE br.resource_id = r.resource_id "
                + "AND br.request_id = ? "
                + "AND r.owner_id = ? "
                + "AND br.status = 'PENDING'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            ps.setInt(2, ownerId);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean acceptRequest(int requestId, int ownerId)
            throws SQLException {

        String checkSql =
                "SELECT br.resource_id, br.student_id, r.available "
                + "FROM borrow_requests br "
                + "JOIN resources r ON br.resource_id = r.resource_id "
                + "WHERE br.request_id = ? "
                + "AND r.owner_id = ? "
                + "AND br.status = 'PENDING' "
                + "FOR UPDATE OF br, r";

        String requestSql =
                "UPDATE borrow_requests SET status = 'ACCEPTED' "
                + "WHERE request_id = ? AND status = 'PENDING'";

        String transactionSql =
                "INSERT INTO borrow_transactions "
                + "(request_id, borrow_date, due_date, status) "
                + "VALUES (?, CURRENT_DATE, CURRENT_DATE + 7, 'Borrowed')";

        String resourceSql =
                "UPDATE resources SET available = FALSE "
                + "WHERE resource_id = ? AND available = TRUE";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int resourceId;
                int requesterId;
                boolean available;

                try (PreparedStatement ps =
                             con.prepareStatement(checkSql)) {
                    ps.setInt(1, requestId);
                    ps.setInt(2, ownerId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }

                        resourceId = rs.getInt("resource_id");
                        requesterId = rs.getInt("student_id");
                        available = rs.getBoolean("available");
                    }
                }

                if (requesterId == ownerId || !available) {
                    con.rollback();
                    return false;
                }

                try (PreparedStatement ps =
                             con.prepareStatement(resourceSql)) {
                    ps.setInt(1, resourceId);

                    if (ps.executeUpdate() != 1) {
                        con.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps =
                             con.prepareStatement(requestSql)) {
                    ps.setInt(1, requestId);

                    if (ps.executeUpdate() != 1) {
                        con.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps =
                             con.prepareStatement(transactionSql)) {
                    ps.setInt(1, requestId);
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
