public class Student
{
    private static int nextStudentId = 1001;

    private final int studentId;
    private String name;
    private String email;
    private String phone;
    private String department;
    private int year;

    public Student(String name, String email, String phone,
                   String department, int year)
    {
        this.studentId = nextStudentId++;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.year = year;
    }

    public int getStudentId()
    {
        return studentId;
    }

    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }

    public String getPhone()
    {
        return phone;
    }

    public String getDepartment()
    {
        return department;
    }

    public int getYear()
    {
        return year;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public void setDepartment(String department)
    {
        this.department = department;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public void displayStudent()
    {
        System.out.println("--------------------------------------------");
        System.out.println("Student ID : " + studentId);
        System.out.println("Name       : " + name);
        System.out.println("Email      : " + email);
        System.out.println("Phone      : " + phone);
        System.out.println("Department : " + department);
        System.out.println("Year       : " + year);
        System.out.println("--------------------------------------------");
    }
}