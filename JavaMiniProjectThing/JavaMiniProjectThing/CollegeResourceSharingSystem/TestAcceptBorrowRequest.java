
import java.sql.SQLException;

public class TestAcceptBorrowRequest {

    public static void main(String[] args) {
        BorrowRequestDAO dao = new BorrowRequestDAO();

        int requestId = 1;
        int ownerId = 1;

        try {
            boolean accepted =
                    dao.acceptRequest(requestId, ownerId);

            if (accepted) {
                System.out.println("Request accepted.");
                System.out.println(
                        "Borrowing transaction created.");
            } else {
                System.out.println(
                        "Request could not be accepted.");
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
