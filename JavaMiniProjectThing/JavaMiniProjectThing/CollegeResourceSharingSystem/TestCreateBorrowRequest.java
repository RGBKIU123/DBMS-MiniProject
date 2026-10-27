
import java.sql.SQLException;

public class TestCreateBorrowRequest {

    public static void main(String[] args) {
        BorrowRequestDAO dao = new BorrowRequestDAO();

        int studentId = 2;
        int resourceId = 1;

        try {
            int requestId =
                    dao.createRequest(studentId, resourceId);

            System.out.println("Request created successfully.");
            System.out.println("Request ID: " + requestId);

        } catch (SQLException e) {
            System.out.println("Request failed: " + e.getMessage());
        }
    }
}
