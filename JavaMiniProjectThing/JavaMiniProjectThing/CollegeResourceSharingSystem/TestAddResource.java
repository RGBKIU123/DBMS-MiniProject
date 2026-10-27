
public class TestAddResource {

    public static void main(String[] args) {
        Student owner = new Student(
                1,
                "Test Student",
                "teststudent@example.com",
                "",
                "CSE",
                3
        );

        Resource resource = new ScientificCalculator(
                "Casio FX-991EX",
                "Scientific calculator for engineering students",
                "Good",
                owner,
                "Casio",
                "FX-991EX",
                true
        );

        ResourceDAO dao = new ResourceDAO();

        try {
            boolean added = dao.addResource(resource);

            if (added) {
                System.out.println("Resource added successfully.");
            } else {
                System.out.println("Resource was not added.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
