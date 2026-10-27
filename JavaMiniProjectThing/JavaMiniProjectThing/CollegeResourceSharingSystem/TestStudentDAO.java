
import java.sql.SQLException;
import java.util.List;

public class TestStudentDAO {
    public static void main(String[] args) {
        StudentDAO dao = new StudentDAO();

        try {
            List<Student> students = dao.getAllStudents();

            System.out.println("All Students:");
            for (Student s : students) {
                System.out.println(
                    s.getStudentId() + " - " + s.getName()
                );
            }

            if (students.isEmpty()) {
                System.out.println("No students found.");
                return;
            }

            Student student = students.get(0);

            System.out.println("\nStudent found: " + student.getName());

            student.setPhone("9999999999");

            boolean updated = dao.updateStudent(student);
            System.out.println("Student updated: " + updated);

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    
}
