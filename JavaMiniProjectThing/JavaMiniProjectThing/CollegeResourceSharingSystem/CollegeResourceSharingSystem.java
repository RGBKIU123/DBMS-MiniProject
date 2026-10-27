import java.util.Scanner;
import java.util.Vector;

public class CollegeResourceSharingSystem
{
    private Vector<Student> students;
    private Vector<Resource> resources;
    private Vector<BorrowRequest> requests;
    private Vector<BorrowTransaction> transactions;

    private Scanner scanner;

public CollegeResourceSharingSystem()
{
    students = new Vector<Student>();
    resources = new Vector<Resource>();
    requests = new Vector<BorrowRequest>();
    transactions = new Vector<BorrowTransaction>();

    scanner = new Scanner(System.in);
}

    
public void start()
{
    boolean running = true;

    while (running)
    {
        displayMainMenu();

        int choice = readInteger("Enter your choice: ");

        switch (choice)
        {
            case 1:
                registerStudent();
                break;

            case 2:
                viewStudents();
                break;

            case 3:
                addResource();
                break;

            case 4:
                viewResources();
                break;

            case 5:
                searchResources();
                break;

            case 6:
                sendBorrowRequest();
                break;

            case 7:
                manageBorrowRequests();
                break;

            case 8:
                viewAllBorrowRequests();
                break;


            case 9:
                returnResource();
                break;

            case 10:
                viewAllTransactions();
                break;

            case 11:
                viewStudentBorrowingHistory();
                break;

            case 12:
                System.out.println("Thank you for using");
                System.out.println("College Resource Sharing System!");
                running = false;
                break;


            default:
                System.out.println(
                    "Invalid choice. Please try again."
                );
        }
    }

    scanner.close();
}




private void displayMainMenu()
{
    System.out.println();
    System.out.println("============================================");
    System.out.println("     COLLEGE RESOURCE SHARING SYSTEM");
    System.out.println("============================================");
    System.out.println("1. Register Student");
    System.out.println("2. View Registered Students");
    System.out.println("3. Add Resource");
    System.out.println("4. View Resources");
    System.out.println("5. Search Resources");
    System.out.println("6. Send Borrow Request");
    System.out.println("7. Manage Borrow Requests");
    System.out.println("8. View All Borrow Requests");
    System.out.println("9. Return Resource");
    System.out.println("10. View All Transactions");
    System.out.println("11. View Student Borrowing History");
    System.out.println("12. Exit");
    System.out.println("============================================");
}



    private void registerStudent()
    {
        System.out.println();
        System.out.println("----------- STUDENT REGISTRATION -----------");

        String name = readString("Enter student name: ");
        String email = readString("Enter email: ");
        String phone = readString("Enter phone number: ");
        String department = readString("Enter department: ");
        int year = readInteger("Enter year of study: ");

        Student student = new Student(
            name,
            email,
            phone,
            department,
            year
        );

        students.add(student);

        System.out.println();
        System.out.println("Student registered successfully!");
        System.out.println("Student ID: " + student.getStudentId());
    }

    private void viewStudents()
    {
        System.out.println();
        System.out.println("----------- REGISTERED STUDENTS -----------");

        if (students.isEmpty())
        {
            System.out.println("No students have been registered yet.");
            return;
        }

        for (Student student : students)
        {
            student.displayStudent();
        }
    }

    private void addResource()
    {
        System.out.println();
        System.out.println("--------------- ADD RESOURCE ---------------");

        if (students.isEmpty())
        {
            System.out.println("You must register at least one student first.");
            return;
        }

        System.out.println();
        System.out.println("Select Resource Type:");
        System.out.println("1. Textbook");
        System.out.println("2. Notes");
        System.out.println("3. Scientific Calculator");
        System.out.println("4. Lab Equipment");

        int type = readInteger("Enter resource type: ");

        String name = readString("Enter resource name: ");
        String description = readString("Enter description: ");
        String condition = readString("Enter condition: ");

        System.out.println();
        System.out.println("Select Owner:");

        for (Student student : students)
        {
            System.out.println(
                student.getStudentId()
                + " - "
                + student.getName()
            );
        }

        int ownerId = readInteger("Enter owner student ID: ");

        Student owner = findStudentById(ownerId);

        if (owner == null)
        {
            System.out.println("Student with that ID was not found.");
            return;
        }

        Resource resource = null;

        switch (type)
        {
            case 1:
            {
                String author = readString("Enter author: ");
                String subject = readString("Enter subject: ");
                int edition = readInteger("Enter edition: ");

                resource = new Textbook(
                    name,
                    description,
                    condition,
                    owner,
                    author,
                    subject,
                    edition
                );

                break;
            }

            case 2:
            {
                String author = readString("Enter author: ");
                String subject = readString("Enter subject: ");
                String noteType = readString(
                    "Enter note type (Handwritten/Digital/etc.): "
                );

                resource = new Notes(
                    name,
                    description,
                    condition,
                    owner,
                    author,
                    subject,
                    noteType
                );

                break;
            }

            case 3:
            {
                String brand = readString("Enter brand: ");
                String model = readString("Enter model: ");

                boolean hasCover =
                    readYesNo("Does it have a protective cover?");

                resource = new ScientificCalculator(
                    name,
                    description,
                    condition,
                    owner,
                    brand,
                    model,
                    hasCover
                );

                break;
            }

            case 4:
            {
                String equipmentType =
                    readString("Enter equipment type: ");

                String laboratory =
                    readString("Enter laboratory name: ");

                resource = new LabEquipment(
                    name,
                    description,
                    condition,
                    owner,
                    equipmentType,
                    laboratory
                );

                break;
            }

            default:
                System.out.println("Invalid resource type.");
                return;
        }

        resources.add(resource);

        System.out.println();
        System.out.println("Resource added successfully!");
        System.out.println("Resource ID: "
                           + resource.getResourceId());
    }

    private void viewResources()
    {
        System.out.println();
        System.out.println("--------------- RESOURCES -----------------");

        if (resources.isEmpty())
        {
            System.out.println("No resources have been added yet.");
            return;
        }

        for (Resource resource : resources)
        {
            resource.displayDetails();
        }
    }

    private void searchResources()
    {
        System.out.println();
        System.out.println("------------- SEARCH RESOURCES -------------");

        if (resources.isEmpty())
        {
            System.out.println("No resources are available to search.");
            return;
        }

        String searchTerm =
            readString("Enter search term: ");

        boolean found = false;

        for (Resource resource : resources)
        {
            if (resource.matchesSearch(searchTerm))
            {
                resource.displayDetails();
                found = true;
            }
        }

        if (!found)
        {
            System.out.println();
            System.out.println(
                "No resources matched your search."
            );
        }
    }

    private Student findStudentById(int studentId)
    {
        for (Student student : students)
        {
            if (student.getStudentId() == studentId)
            {
                return student;
            }
        }

        return null;
    }

    private String readString(String message)
    {
        while (true)
        {
            System.out.print(message);

            String input = scanner.nextLine().trim();

            if (!input.isEmpty())
            {
                return input;
            }

            System.out.println(
                "Input cannot be empty. Please try again."
            );
        }
    }

    private int readInteger(String message)
    {
        while (true)
        {
            System.out.print(message);

            String input = scanner.nextLine().trim();

            try
            {
                return Integer.parseInt(input);
            }
            catch (NumberFormatException e)
            {
                System.out.println(
                    "Please enter a valid number."
                );
            }
        }
    }

    private boolean readYesNo(String message)
    {
        while (true)
        {
            System.out.print(message + " (Y/N): ");

            String input =
                scanner.nextLine().trim();

            if (input.equalsIgnoreCase("Y"))
            {
                return true;
            }

            if (input.equalsIgnoreCase("N"))
            {
                return false;
            }

            System.out.println(
                "Please enter Y or N."
            );
        }
    }
    
private void sendBorrowRequest()
{
    System.out.println();
    System.out.println("----------- SEND BORROW REQUEST -----------");

    if (students.isEmpty() || resources.isEmpty())
    {
        System.out.println(
            "Register students and add resources first."
        );
        return;
    }

    int requesterId = readInteger("Enter your student ID: ");
    Student requester = findStudentById(requesterId);

    if (requester == null)
    {
        System.out.println("Student not found.");
        return;
    }

    viewResources();

    int resourceId = readInteger("Enter resource ID to request: ");
    Resource resource = findResourceById(resourceId);

    if (resource == null)
    {
        System.out.println("Resource not found.");
        return;
    }

    if (requester.getStudentId() == resource.getOwner().getStudentId())
    {
        System.out.println("You cannot borrow your own resource.");
        return;
    }

    if (!resource.isAvailable())
    {
        System.out.println("This resource is unavailable.");
        return;
    }

    for (BorrowRequest existing : requests)
    {
        if (existing.getResource().getResourceId() == resourceId
                && existing.getRequester().getStudentId() == requesterId
                && (existing.getStatus().equals("PENDING")
                || existing.getStatus().equals("ACCEPTED")))
        {
            System.out.println(
                "You already have a pending or accepted request for this resource."
            );
            return;
        }
    }

    BorrowRequest request = new BorrowRequest(requester, resource);
    requests.add(request);

    System.out.println();
    System.out.println("Borrow request submitted successfully!");
    System.out.println("Request ID: " + request.getRequestId());
    System.out.println("Owner: " + resource.getOwner().getName());
    System.out.println("Status: PENDING");
}

private Resource findResourceById(int resourceId)
{
    for (Resource resource : resources)
    {
        if (resource.getResourceId() == resourceId)
        {
            return resource;
        }
    }

    return null;
}

private void manageBorrowRequests()
{
    System.out.println();
    System.out.println("----------- MANAGE BORROW REQUESTS -----------");

    int ownerId = readInteger("Enter your student ID: ");
    Student owner = findStudentById(ownerId);

    if (owner == null)
    {
        System.out.println("Student not found.");
        return;
    }

    Vector<BorrowRequest> pendingRequests =
        new Vector<BorrowRequest>();

    for (BorrowRequest request : requests)
    {
        if (request.getResource().getOwner().getStudentId() == ownerId
                && request.isPending())
        {
            pendingRequests.add(request);
        }
    }

    if (pendingRequests.isEmpty())
    {
        System.out.println("You have no pending requests.");
        return;
    }

    System.out.println();
    System.out.println("Your pending requests:");

    for (BorrowRequest request : pendingRequests)
    {
        request.displayRequest();
    }

    int requestId = readInteger("Enter request ID to manage: ");
    BorrowRequest selected = null;

    for (BorrowRequest request : pendingRequests)
    {
        if (request.getRequestId() == requestId)
        {
            selected = request;
            break;
        }
    }

    if (selected == null)
    {
        System.out.println(
            "That request does not belong to your pending requests."
        );
        return;
    }

    if (!selected.getResource().isAvailable())
    {
        System.out.println(
            "This resource is no longer available."
        );
        return;
    }

    System.out.println("1. Accept request");
    System.out.println("2. Reject request");

    int choice = readInteger("Enter choice: ");

    if (choice == 1)
    {
        
if (selected.accept())
{
    selected.getResource().setAvailable(false);

    BorrowTransaction transaction =
        new BorrowTransaction(selected);

    transactions.add(transaction);

    // Reject other pending requests for this same resource.
    for (BorrowRequest other : requests)
    {
        if (other.getRequestId() != selected.getRequestId()
                && other.getResource().getResourceId()
                    == selected.getResource().getResourceId()
                && other.isPending())
        {
            other.reject();
        }
    }

    System.out.println("Request accepted successfully.");
    System.out.println("Resource marked as unavailable.");
    System.out.println(
        "Transaction created. Transaction ID: "
        + transaction.getTransactionId()
    );
}
else
{
    System.out.println(
        "This request has already been processed."
    );
}

    }
    else if (choice == 2)
    {
        if (selected.reject())
        {
            System.out.println("Request rejected successfully.");
        }
        else
        {
            System.out.println(
                "This request has already been processed."
            );
        }
    }
    else
    {
        System.out.println("Invalid choice.");
    }
}

private void viewAllBorrowRequests()
{
    System.out.println();
    System.out.println("----------- ALL BORROW REQUESTS -----------");

    if (requests.isEmpty())
    {
        System.out.println("No borrow requests have been created.");
        return;
    }

    for (BorrowRequest request : requests)
    {
        request.displayRequest();
    }
}


private void returnResource()
{
    System.out.println();
    System.out.println("--------------- RETURN RESOURCE ---------------");

    int borrowerId = readInteger("Enter your student ID: ");
    Student borrower = findStudentById(borrowerId);

    if (borrower == null)
    {
        System.out.println("Student not found.");
        return;
    }

    Vector<BorrowTransaction> activeTransactions =
        new Vector<BorrowTransaction>();

    for (BorrowTransaction transaction : transactions)
    {
        if (transaction.getBorrower().getStudentId() == borrowerId
                && transaction.isActive())
        {
            activeTransactions.add(transaction);
        }
    }

    if (activeTransactions.isEmpty())
    {
        System.out.println("You have no active borrowing transactions.");
        return;
    }

    System.out.println();
    System.out.println("Your borrowed resources:");

    for (BorrowTransaction transaction : activeTransactions)
    {
        transaction.displayTransaction();
    }

    int transactionId =
        readInteger("Enter transaction ID to return: ");

    BorrowTransaction selected = null;

    for (BorrowTransaction transaction : activeTransactions)
    {
        if (transaction.getTransactionId() == transactionId)
        {
            selected = transaction;
            break;
        }
    }

    if (selected == null)
    {
        System.out.println(
            "Transaction not found in your active borrowings."
        );
        return;
    }

    if (selected.markReturned())
    {
        selected.getResource().setAvailable(true);

        System.out.println("Resource returned successfully.");
        System.out.println("Resource is now available again.");
    }
    else
    {
        System.out.println(
            "This transaction has already been completed."
        );
    }
}


private void viewAllTransactions()
{
    System.out.println();
    System.out.println("--------------- ALL TRANSACTIONS ---------------");

    if (transactions.isEmpty())
    {
        System.out.println("No borrowing transactions exist yet.");
        return;
    }

    for (BorrowTransaction transaction : transactions)
    {
        transaction.displayTransaction();
    }
}


private void viewStudentBorrowingHistory()
{
    System.out.println();
    System.out.println("----------- STUDENT BORROWING HISTORY -----------");

    int studentId = readInteger("Enter student ID: ");
    Student student = findStudentById(studentId);

    if (student == null)
    {
        System.out.println("Student not found.");
        return;
    }

    boolean found = false;

    System.out.println("History for: " + student.getName());

    for (BorrowTransaction transaction : transactions)
    {
        if (transaction.getBorrower().getStudentId() == studentId)
        {
            transaction.displayTransaction();
            found = true;
        }
    }

    if (!found)
    {
        System.out.println(
            "This student has no borrowing history yet."
        );
    }
}

}