
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BorrowTransactionDAO {

    public List<String> getAllTransactions() throws SQLException {
        List<String> transactions = new ArrayList<>();

        String sql =
                "SELECT bt.transaction_id, bt.request_id, "
                + "s.name AS borrower, r.name AS resource, "
                + "bt.borrow_date, bt.due_date, bt.return_date, bt.status "
                + "FROM borrow_transactions bt "
                + "JOIN borrow_requests br ON bt.request_id = br.request_id "
                + "JOIN students s ON br.student_id = s.student_id "
                + "JOIN resources r ON br.resource_id = r.resource_id "
                + "ORDER BY bt.transaction_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String transaction =
                        "Transaction ID: " + rs.getInt("transaction_id")
                        + " | Request ID: " + rs.getInt("request_id")
                        + " | Borrower: " + rs.getString("borrower")
                        + " | Resource: " + rs.getString("resource")
                        + " | Borrow Date: " + rs.getDate("borrow_date")
                        + " | Due Date: " + rs.getDate("due_date")
                        + " | Return Date: " + rs.getDate("return_date")
                        + " | Status: " + rs.getString("status");

                transactions.add(transaction);
            }
        }

        return transactions;
    }

    public boolean returnResource(int transactionId, int studentId)
            throws SQLException {

        String checkSql =
                "SELECT bt.request_id, br.resource_id "
                + "FROM borrow_transactions bt "
                + "JOIN borrow_requests br ON bt.request_id = br.request_id "
                + "WHERE bt.transaction_id = ? "
                + "AND br.student_id = ? "
                + "AND bt.status = 'Borrowed' "
                + "FOR UPDATE OF bt, br";

        String transactionSql =
                "UPDATE borrow_transactions "
                + "SET return_date = CURRENT_DATE, status = 'Returned' "
                + "WHERE transaction_id = ? AND status = 'Borrowed'";

        String resourceSql =
                "UPDATE resources SET available = TRUE "
                + "WHERE resource_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int resourceId;

                try (PreparedStatement ps =
                             con.prepareStatement(checkSql)) {
                    ps.setInt(1, transactionId);
                    ps.setInt(2, studentId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }

                        resourceId = rs.getInt("resource_id");
                    }
                }

                try (PreparedStatement ps =
                             con.prepareStatement(transactionSql)) {
                    ps.setInt(1, transactionId);

                    if (ps.executeUpdate() != 1) {
                        con.rollback();
                        return false;
                    }
                }

                try (PreparedStatement ps =
                             con.prepareStatement(resourceSql)) {
                    ps.setInt(1, resourceId);

                    if (ps.executeUpdate() != 1) {
                        con.rollback();
                        return false;
                    }
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
