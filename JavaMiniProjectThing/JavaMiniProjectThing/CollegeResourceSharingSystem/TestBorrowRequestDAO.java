
import java.sql.SQLException;
import java.util.List;

public class TestBorrowRequestDAO {

    public static void main(String[] args) {
        BorrowRequestDAO dao = new BorrowRequestDAO();

        try {
            List<String> requests = dao.getAllRequests();

            if (requests.isEmpty()) {
                System.out.println("No borrowing requests found.");
            } else {
                System.out.println("Borrowing Requests:");

                for (String request : requests) {
                    System.out.println(request);
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
