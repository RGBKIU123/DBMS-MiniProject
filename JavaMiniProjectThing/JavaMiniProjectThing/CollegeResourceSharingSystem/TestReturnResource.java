
import java.sql.SQLException;

public class TestReturnResource {

    public static void main(String[] args) {
        BorrowTransactionDAO dao = new BorrowTransactionDAO();

        int transactionId = 1;
        int studentId = 2;

        try {
            boolean returned =
                    dao.returnResource(transactionId, studentId);

            if (returned) {
                System.out.println("Resource returned successfully.");
                System.out.println("Resource is available again.");
            } else {
                System.out.println(
                        "Return failed. Check the transaction and student IDs.");
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
