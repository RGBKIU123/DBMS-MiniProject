import java.util.Scanner;
import java.util.Vector;

public class CollegeResourceSharingSystem
{
    private Vector<Student> students;
    private Scanner scanner;

    public CollegeResourceSharingSystem()
    {
        students = new Vector<Student>();
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
                    System.out.println();
                    System.out.println("Thank you for using");
                    System.out.println("College Resource Sharing System!");
                    running = false;
                    break;

                default:
                    System.out.println();
                    System.out.println("Invalid choice. Please try again.");
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
        System.out.println("3. Exit");
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

            System.out.println("Input cannot be empty. Please try again.");
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
                System.out.println("Please enter a valid number.");
            }
        }
    }
}