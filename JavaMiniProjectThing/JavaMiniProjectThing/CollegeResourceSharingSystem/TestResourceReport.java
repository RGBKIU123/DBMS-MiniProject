
public class TestResourceReport {

    public static void main(String[] args) {
        ResourceReportTask task = new ResourceReportTask();

        Thread reportThread = new Thread(task);

        reportThread.start();
    }
}