
import java.sql.SQLException;

public class TestBorrowTransactionDAO {

    public static void main(String[] args) {
        BorrowTransactionDAO dao = new BorrowTransactionDAO();

        try {
            for (String transaction : dao.getAllTransactions()) {
                System.out.println(transaction);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
