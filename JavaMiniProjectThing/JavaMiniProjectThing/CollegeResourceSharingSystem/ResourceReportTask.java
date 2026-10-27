
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ResourceReportTask implements Runnable {

    @Override
    public void run() {
        ResourceDAO dao = new ResourceDAO();

        try {
            List<Resource> resources = dao.getAllResources();
            StringBuilder report = new StringBuilder();

            report.append("COLLEGE RESOURCE SHARING SYSTEM\n");
            report.append("RESOURCE REPORT\n");
            report.append("====================================\n\n");

            if (resources.isEmpty()) {
                report.append("No resources found.\n");
            } else {
                for (Resource resource : resources) {
                    report.append("Resource ID: ")
                          .append(resource.getResourceId())
                          .append("\n");

                    report.append("Name: ")
                          .append(resource.getName())
                          .append("\n");

                    report.append("Category: ")
                          .append(resource.getCategory())
                          .append("\n");

                    report.append("Description: ")
                          .append(resource.getDescription())
                          .append("\n");

                    report.append("Condition: ")
                          .append(resource.getCondition())
                          .append("\n");

                    report.append("Owner: ")
                          .append(resource.getOwner().getName())
                          .append("\n");

                    report.append("Available: ")
                          .append(resource.isAvailable() ? "Yes" : "No")
                          .append("\n");

                    report.append("------------------------------------\n");
                }
            }

            try (BufferedWriter writer =
                    new BufferedWriter(
                            new FileWriter("resource_report.txt"))) {

                writer.write(report.toString());
            }

            System.out.println(
                    "Resource report created: resource_report.txt");

        } catch (SQLException e) {
            System.out.println(
                    "Could not retrieve resources: " + e.getMessage());

        } catch (IOException e) {
            System.out.println(
                    "Could not write the report: " + e.getMessage());
        }
    }
}
