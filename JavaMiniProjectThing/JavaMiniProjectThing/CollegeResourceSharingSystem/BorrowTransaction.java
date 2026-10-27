
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BorrowTransaction
{
    private static int nextTransactionId = 9001;

    private final int transactionId;
    private final BorrowRequest request;
    private final LocalDateTime borrowDate;

    private LocalDateTime returnDate;
    private String status;

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public BorrowTransaction(BorrowRequest request)
    {
        this.transactionId = nextTransactionId++;
        this.request = request;
        this.borrowDate = LocalDateTime.now();
        this.returnDate = null;
        this.status = "ACTIVE";
    }

    public int getTransactionId()
    {
        return transactionId;
    }

    public BorrowRequest getRequest()
    {
        return request;
    }

    public Student getBorrower()
    {
        return request.getRequester();
    }

    public Resource getResource()
    {
        return request.getResource();
    }

    public LocalDateTime getBorrowDate()
    {
        return borrowDate;
    }

    public LocalDateTime getReturnDate()
    {
        return returnDate;
    }

    public String getStatus()
    {
        return status;
    }

    public boolean isActive()
    {
        return status.equals("ACTIVE");
    }

    public boolean markReturned()
    {
        if (!isActive())
        {
            return false;
        }

        returnDate = LocalDateTime.now();
        status = "RETURNED";

        return true;
    }

    public void displayTransaction()
    {
        System.out.println("--------------------------------------------");
        System.out.println("Transaction ID : " + transactionId);
        System.out.println("Request ID     : " + request.getRequestId());
        System.out.println("Resource       : " + getResource().getName());
        System.out.println("Resource ID    : " + getResource().getResourceId());
        System.out.println("Owner          : " + getResource().getOwner().getName());
        System.out.println("Borrower       : " + getBorrower().getName());
        System.out.println("Borrower ID    : " + getBorrower().getStudentId());
        System.out.println("Borrowed On    : " + borrowDate.format(FORMATTER));

        if (returnDate == null)
        {
            System.out.println("Returned On    : Not returned yet");
        }
        else
        {
            System.out.println("Returned On    : " + returnDate.format(FORMATTER));
        }

        System.out.println("Status         : " + status);
        System.out.println("--------------------------------------------");
    }
}
