import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public int addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students "
                + "(name, email, phone, department, year_of_study) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING student_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setString(3, student.getPhone());
            ps.setString(4, student.getDepartment());
            ps.setInt(5, student.getYear());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("student_id");
                }
            }
        }

        throw new SQLException("Student could not be added.");
    }

    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();

        String sql = "SELECT student_id, name, email, phone, "
                + "department, year_of_study FROM students "
                + "ORDER BY student_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Student student = new Student(
                        rs.getInt("student_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("department"),
                        rs.getInt("year_of_study")
                );

                students.add(student);
            }
        }

        return students;
    }

    public Student getStudentById(int id) throws SQLException {
        String sql = "SELECT student_id, name, email, phone, "
                + "department, year_of_study FROM students "
                + "WHERE student_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getInt("student_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("department"),
                            rs.getInt("year_of_study")
                    );
                }
            }
        }

        return null;
    }

    
public boolean updateStudent(Student student) throws SQLException {
    String sql = "UPDATE students SET name = ?, email = ?, phone = ?, "
            + "department = ?, year_of_study = ? WHERE student_id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, student.getName());
        ps.setString(2, student.getEmail());
        ps.setString(3, student.getPhone());
        ps.setString(4, student.getDepartment());
        ps.setInt(5, student.getYear());
        ps.setInt(6, student.getStudentId());

        return ps.executeUpdate() > 0;
    }
}

public boolean deleteStudent(int id) throws SQLException {
    String sql = "DELETE FROM students WHERE student_id = ?";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, id);
        return ps.executeUpdate() > 0;
    }
}

}
