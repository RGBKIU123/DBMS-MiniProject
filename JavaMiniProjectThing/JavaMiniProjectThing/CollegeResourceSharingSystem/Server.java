
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

        server.createContext("/api/resources", Server::handleResources);
        server.createContext("/api/students", Server::handleStudents);
        server.createContext("/", Server::handleFrontend);
        server.createContext(
        "/api/borrow-requests", Server::handleBorrowRequests);
        server.createContext(
        "/api/borrow-transactions", Server::handleBorrowTransactions);
        server.createContext( "/api/reports/resources-by-category", Server::handleResourcesByCategory );
        

        server.setExecutor(null);
        server.start();

        System.out.println("Server started at http://localhost:8080");
    }

    private static void handleResources(HttpExchange exchange)
            throws IOException {

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            ResourceDAO dao = new ResourceDAO();

            if (path.equals("/api/resources")) {

                if (method.equals("GET")) {
                    String query = exchange.getRequestURI().getRawQuery();
                    List<Resource> resources;

                    if (query != null && query.startsWith("search=")) {
                        String term = URLDecoder.decode(
                                query.substring(7),
                                StandardCharsets.UTF_8);
                        resources = dao.searchResources(term);
                    } else {
                        resources = dao.getAllResources();
                    }

                    StringBuilder json = new StringBuilder("[");
                    boolean first = true;

                    for (Resource resource : resources) {
                        if (!first) {
                            json.append(",");
                        }
                        first = false;

                        json.append("{")
                            .append("\"resourceId\":")
                            .append(resource.getResourceId()).append(",")
                            .append("\"name\":\"")
                            .append(escape(resource.getName())).append("\",")
                            .append("\"category\":\"")
                            .append(escape(resource.getCategory())).append("\",")
                            .append("\"description\":\"")
                            .append(escape(resource.getDescription())).append("\",")
                            .append("\"condition\":\"")
                            .append(escape(resource.getCondition())).append("\",")
                            .append("\"ownerName\":\"")
                            .append(escape(resource.getOwner().getName())).append("\",")
                            .append("\"ownerId\":")
                            .append(resource.getOwner().getStudentId()).append(",")
                            .append("\"available\":")
                            .append(resource.isAvailable())
                            .append("}");
                    }

                    json.append("]");
                    sendResponse(exchange, 200, json.toString(),
                            "application/json");
                    return;
                }

                if (method.equals("POST")) {
                    Map<String, String> data = readForm(exchange);
                    Resource resource = createResource(data);

                    boolean added = dao.addResource(resource);

                    if (added) {
                        sendResponse(exchange, 201,
                                "{\"message\":\"Resource added successfully\"}",
                                "application/json");
                    } else {
                        sendResponse(exchange, 400,
                                "{\"error\":\"Resource was not added\"}",
                                "application/json");
                    }
                    return;
                }

                sendMethodNotAllowed(exchange);
                return;
            }

            if (path.startsWith("/api/resources/")) {
                String idText = path.substring(
                        "/api/resources/".length());

                int id;

                try {
                    id = Integer.parseInt(idText);
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400,
                            "{\"error\":\"Invalid resource ID\"}",
                            "application/json");
                    return;
                }

                if (method.equals("PUT")) {
                    Resource resource = dao.getResourceById(id);

                    if (resource == null) {
                        sendResponse(exchange, 404,
                                "{\"error\":\"Resource not found\"}",
                                "application/json");
                        return;
                    }

                    Map<String, String> data = readForm(exchange);

                    if (data.containsKey("name")) {
                        resource.setName(data.get("name"));
                    }

                    if (data.containsKey("description")) {
                        resource.setDescription(data.get("description"));
                    }

                    if (data.containsKey("condition")) {
                        resource.setCondition(data.get("condition"));
                    }

                    boolean updated = dao.updateResource(resource);

                    if (updated) {
                        sendResponse(exchange, 200,
                                "{\"message\":\"Resource updated successfully\"}",
                                "application/json");
                    } else {
                        sendResponse(exchange, 404,
                                "{\"error\":\"Resource was not updated\"}",
                                "application/json");
                    }
                    return;
                }

                if (method.equals("DELETE")) {
                    boolean deleted = dao.deleteResource(id);

                    if (deleted) {
                        sendResponse(exchange, 200,
                                "{\"message\":\"Resource deleted successfully\"}",
                                "application/json");
                    } else {
                        sendResponse(exchange, 404,
                                "{\"error\":\"Resource not found\"}",
                                "application/json");
                    }
                    return;
                }

                sendMethodNotAllowed(exchange);
                return;
            }

            sendResponse(exchange, 404,
                    "{\"error\":\"Endpoint not found\"}",
                    "application/json");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            sendResponse(exchange, 500,
                    "{\"error\":\"Database operation failed\"}",
                    "application/json");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400,
                    "{\"error\":\"Invalid input. Check the fields and category.\"}",
                    "application/json");
        }
    }

   
private static Resource createResource(Map<String, String> data)
        throws SQLException {

    String name = required(data, "name");
    String category = required(data, "category");
    String description = required(data, "description");
    String condition = required(data, "condition");
    int ownerId = Integer.parseInt(required(data, "ownerId"));

    StudentDAO studentDAO = new StudentDAO();
    Student owner = studentDAO.getStudentById(ownerId);

    if (owner == null) {
        throw new IllegalArgumentException("Owner not found.");
    }

    switch (category.toLowerCase()) {
        case "textbook":
            return new Textbook(
                    name, description, condition, owner,
                    required(data, "author"),
                    required(data, "subject"),
                    Integer.parseInt(required(data, "edition")));

        case "notes":
            return new Notes(
                    name, description, condition, owner,
                    required(data, "author"),
                    required(data, "subject"),
                    required(data, "noteType"));

        case "lab equipment":
            return new LabEquipment(
                    name, description, condition, owner,
                    required(data, "equipmentType"),
                    required(data, "laboratory"));

        case "scientific calculator":
            return new ScientificCalculator(
                    name, description, condition, owner,
                    required(data, "brand"),
                    required(data, "model"),
                    Boolean.parseBoolean(required(data, "hasCover")));

        default:
            // Create a general resource using its custom category.
            return new OtherResource(
                    name, category, description, condition, owner);
    }
}


    private static String required(
            Map<String, String> data, String key) {

        String value = data.get(key);

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Missing field: " + key);
        }

        return value.trim();
    }

    private static Map<String, String> readForm(HttpExchange exchange)
            throws IOException {

        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8);

        Map<String, String> data = new HashMap<>();

        if (body.isEmpty()) {
            return data;
        }

        String[] pairs = body.split("&");

        for (String pair : pairs) {
            String[] parts = pair.split("=", 2);

            String key = URLDecoder.decode(
                    parts[0], StandardCharsets.UTF_8);

            String value = "";

            if (parts.length == 2) {
                value = URLDecoder.decode(
                        parts[1], StandardCharsets.UTF_8);
            }

            data.put(key, value);
        }

        return data;
    }

    
private static void handleStudents(HttpExchange exchange)
        throws IOException {

    String method = exchange.getRequestMethod();
    String path = exchange.getRequestURI().getPath();

    try {
        StudentDAO dao = new StudentDAO();

        if (path.equals("/api/students")) {

            if (method.equals("GET")) {
                List<Student> students = dao.getAllStudents();
                StringBuilder json = new StringBuilder("[");
                boolean first = true;

                for (Student student : students) {
                    if (!first) {
                        json.append(",");
                    }
                    first = false;

                    json.append("{")
                        .append("\"studentId\":")
                        .append(student.getStudentId()).append(",")
                        .append("\"name\":\"")
                        .append(escape(student.getName())).append("\",")
                        .append("\"email\":\"")
                        .append(escape(student.getEmail())).append("\",")
                        .append("\"phone\":\"")
                        .append(escape(student.getPhone())).append("\",")
                        .append("\"department\":\"")
                        .append(escape(student.getDepartment())).append("\",")
                        .append("\"year\":")
                        .append(student.getYear())
                        .append("}");
                }

                json.append("]");
                sendResponse(exchange, 200, json.toString(),
                        "application/json");
                return;
            }

            if (method.equals("POST")) {
                Map<String, String> data = readForm(exchange);

                String name = required(data, "name");
                String email = required(data, "email");
                String phone = required(data, "phone");
                String department = required(data, "department");
                int year = Integer.parseInt(required(data, "year"));

                if (year < 1 || year > 6) {
                    throw new IllegalArgumentException(
                            "Year of study must be between 1 and 6.");
                }

                Student student = new Student(
                        name, email, phone, department, year);

                int id = dao.addStudent(student);

                sendResponse(exchange, 201,
                        "{\"message\":\"Student added successfully\","
                        + "\"studentId\":" + id + "}",
                        "application/json");
                return;
            }

            sendMethodNotAllowed(exchange);
            return;
        }

        if (path.startsWith("/api/students/")) {
            String idText = path.substring(
                    "/api/students/".length());

            int id = Integer.parseInt(idText);

            if (id <= 0) {
                throw new IllegalArgumentException("Invalid student ID.");
            }

            if (method.equals("PUT")) {
                Student student = dao.getStudentById(id);

                if (student == null) {
                    sendResponse(exchange, 404,
                            "{\"error\":\"Student not found\"}",
                            "application/json");
                    return;
                }

                Map<String, String> data = readForm(exchange);

                student.setName(required(data, "name"));
                student.setEmail(required(data, "email"));
                student.setPhone(required(data, "phone"));
                student.setDepartment(required(data, "department"));

                int year = Integer.parseInt(required(data, "year"));

                if (year < 1 || year > 6) {
                    throw new IllegalArgumentException(
                            "Year of study must be between 1 and 6.");
                }

                student.setYear(year);

                if (dao.updateStudent(student)) {
                    sendResponse(exchange, 200,
                            "{\"message\":\"Student updated successfully\"}",
                            "application/json");
                } else {
                    sendResponse(exchange, 404,
                            "{\"error\":\"Student not found\"}",
                            "application/json");
                }
                return;
            }

            if (method.equals("DELETE")) {
                if (dao.deleteStudent(id)) {
                    sendResponse(exchange, 200,
                            "{\"message\":\"Student deleted successfully\"}",
                            "application/json");
                } else {
                    sendResponse(exchange, 404,
                            "{\"error\":\"Student not found\"}",
                            "application/json");
                }
                return;
            }

            sendMethodNotAllowed(exchange);
            return;
        }

        sendResponse(exchange, 404,
                "{\"error\":\"Student endpoint not found\"}",
                "application/json");

    } catch (NumberFormatException e) {
        sendResponse(exchange, 400,
                "{\"error\":\"Invalid student ID or year of study\"}",
                "application/json");

    } catch (IllegalArgumentException e) {
        sendResponse(exchange, 400,
                "{\"error\":\"Invalid input. Check all student fields.\"}",
                "application/json");

    } catch (SQLException e) {
        System.out.println("Student error: " + e.getMessage());

        if ("23503".equals(e.getSQLState())) {
            sendResponse(exchange, 409,
                    "{\"error\":\"This student is linked to existing "
                    + "resources or borrowing records and cannot be deleted.\"}",
                    "application/json");
        } else if ("23505".equals(e.getSQLState())) {
            sendResponse(exchange, 409,
                    "{\"error\":\"A student with this email already exists.\"}",
                    "application/json");
        } else {
            sendResponse(exchange, 500,
                    "{\"error\":\"Student database operation failed\"}",
                    "application/json");
        }
    }
}


private static void handleResourcesByCategory(
        HttpExchange exchange) throws IOException {

    if (!exchange.getRequestMethod().equals("GET")) {
        sendMethodNotAllowed(exchange);
        return;
    }

    String sql =
            "SELECT category, COUNT(*) AS total_resources " +
            "FROM resources " +
            "GROUP BY category " +
            "ORDER BY category";

    try (java.sql.Connection con = DBConnection.getConnection();
         java.sql.PreparedStatement ps = con.prepareStatement(sql);
         java.sql.ResultSet rs = ps.executeQuery()) {

        StringBuilder json = new StringBuilder("[");
        boolean first = true;

        while (rs.next()) {
            if (!first) {
                json.append(",");
            }
            first = false;

            json.append("{")
                .append("\"category\":\"")
                .append(escape(rs.getString("category")))
                .append("\",")
                .append("\"totalResources\":")
                .append(rs.getInt("total_resources"))
                .append("}");
        }

        json.append("]");

        sendResponse(exchange, 200, json.toString(),
                "application/json");

    } catch (SQLException e) {
        System.out.println(
                "Resource category report error: " + e.getMessage());

        sendResponse(exchange, 500,
                "{\"error\":\"Unable to load category report\"}",
                "application/json");
    }
}


private static void handleBorrowRequests(HttpExchange exchange)
        throws IOException {

    String method = exchange.getRequestMethod();
    String path = exchange.getRequestURI().getPath();
    BorrowRequestDAO dao = new BorrowRequestDAO();

    try {
        if (path.equals("/api/borrow-requests")) {

            if (method.equals("GET")) {
                List<String> requests = dao.getAllRequests();
                StringBuilder json = new StringBuilder("[");
                boolean first = true;

                for (String request : requests) {
                    if (!first) {
                        json.append(",");
                    }
                    first = false;

                    json.append("\"").append(escape(request)).append("\"");
                }

                json.append("]");
                sendResponse(exchange, 200, json.toString(),
                        "application/json");
                return;
            }

            if (method.equals("POST")) {
                Map<String, String> data = readForm(exchange);

                int studentId =
                        Integer.parseInt(required(data, "studentId"));
                int resourceId =
                        Integer.parseInt(required(data, "resourceId"));

                int requestId = dao.createRequest(studentId, resourceId);

                sendResponse(exchange, 201,
                        "{\"message\":\"Borrowing request created\","
                        + "\"requestId\":" + requestId + "}",
                        "application/json");
                return;
            }

            sendMethodNotAllowed(exchange);
            return;
        }

        String prefix = "/api/borrow-requests/";
        if (path.startsWith(prefix)) {
            String[] parts = path.substring(prefix.length()).split("/");

            if (parts.length != 2) {
                sendResponse(exchange, 404,
                        "{\"error\":\"Invalid borrowing request endpoint\"}",
                        "application/json");
                return;
            }

            int requestId = Integer.parseInt(parts[0]);
            Map<String, String> data = readForm(exchange);
            int ownerId = Integer.parseInt(required(data, "ownerId"));

            if (!method.equals("POST")) {
                sendMethodNotAllowed(exchange);
                return;
            }

            boolean updated;

            if (parts[1].equals("approve")) {
                updated = dao.acceptRequest(requestId, ownerId);
            } else if (parts[1].equals("reject")) {
                updated = dao.rejectRequest(requestId, ownerId);
            } else {
                sendResponse(exchange, 404,
                        "{\"error\":\"Unknown request action\"}",
                        "application/json");
                return;
            }

            if (updated) {
                sendResponse(exchange, 200,
                        "{\"message\":\"Request processed successfully\"}",
                        "application/json");
            } else {
                sendResponse(exchange, 409,
                        "{\"error\":\"Request could not be processed. "
                        + "Check the owner, status and availability.\"}",
                        "application/json");
            }
            return;
        }

        sendResponse(exchange, 404,
                "{\"error\":\"Borrowing request endpoint not found\"}",
                "application/json");

    } catch (NumberFormatException e) {
        sendResponse(exchange, 400,
                "{\"error\":\"Student, resource or request ID is invalid\"}",
                "application/json");
    } catch (IllegalArgumentException e) {
        sendResponse(exchange, 400,
                "{\"error\":\"Invalid borrowing request details\"}",
                "application/json");
    } catch (SQLException e) {
        System.out.println("Borrow request error: " + e.getMessage());
        sendResponse(exchange, 400,
                "{\"error\":\"Borrowing request failed: "
                + escape(e.getMessage()) + "\"}",
                "application/json");
    }
}



private static void handleBorrowTransactions(HttpExchange exchange)
        throws IOException {

    String method = exchange.getRequestMethod();
    String path = exchange.getRequestURI().getPath();
    BorrowTransactionDAO dao = new BorrowTransactionDAO();

    try {
        if (path.equals("/api/borrow-transactions")) {
            if (!method.equals("GET")) {
                sendMethodNotAllowed(exchange);
                return;
            }

            List<String> transactions = dao.getAllTransactions();
            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            for (String transaction : transactions) {
                if (!first) {
                    json.append(",");
                }
                first = false;
                json.append("\"").append(escape(transaction)).append("\"");
            }

            json.append("]");
            sendResponse(exchange, 200, json.toString(),
                    "application/json");
            return;
        }

        String prefix = "/api/borrow-transactions/";

        if (path.startsWith(prefix)) {
            String remainder = path.substring(prefix.length());

            // Accept /{transactionId} or /{transactionId}/return
            if (remainder.endsWith("/return")) {
                remainder = remainder.substring(
                        0, remainder.length() - "/return".length());
            }

            int transactionId = Integer.parseInt(remainder);

            if (transactionId <= 0) {
                throw new IllegalArgumentException(
                        "Invalid transaction ID");
            }

            if (!method.equals("POST")) {
                sendMethodNotAllowed(exchange);
                return;
            }

            Map<String, String> data = readForm(exchange);
            int studentId = Integer.parseInt(
                    required(data, "studentId"));

            if (studentId <= 0) {
                throw new IllegalArgumentException(
                        "Invalid student ID");
            }

            boolean returned =
                    dao.returnResource(transactionId, studentId);

            if (returned) {
                sendResponse(exchange, 200,
                        "{\"message\":\"Resource returned successfully\"}",
                        "application/json");
            } else {
                sendResponse(exchange, 409,
                        "{\"error\":\"Return failed. Check that the "
                        + "student is the borrower and the transaction "
                        + "is still borrowed.\"}",
                        "application/json");
            }
            return;
        }

        sendResponse(exchange, 404,
                "{\"error\":\"Borrowing transaction endpoint not found\"}",
                "application/json");

    } catch (NumberFormatException e) {
        sendResponse(exchange, 400,
                "{\"error\":\"Invalid transaction or student ID\"}",
                "application/json");
    } catch (IllegalArgumentException e) {
        sendResponse(exchange, 400,
                "{\"error\":\"Invalid return details\"}",
                "application/json");
    } catch (SQLException e) {
        System.out.println(
                "Borrow transaction error: " + e.getMessage());
        sendResponse(exchange, 500,
                "{\"error\":\"Borrowing transaction database operation failed\"}",
                "application/json");
    }
}



    private static void handleFrontend(HttpExchange exchange)
            throws IOException {

        String path = exchange.getRequestURI().getPath();

        if (path.equals("/")) {
            path = "/index.html";
        }

        String fileName;

        if (path.equals("/index.html")) {
            fileName = "index.html";
        } else if (path.equals("/style.css")) {
            fileName = "style.css";
        } else if (path.equals("/script.js")) {
            fileName = "script.js";
        } else {
            sendResponse(exchange, 404, "Page not found",
                    "text/plain; charset=UTF-8");
            return;
        }

        File file = new File("web", fileName);

        if (!file.isFile()) {
            sendResponse(exchange, 404, "Frontend file not found",
                    "text/plain; charset=UTF-8");
            return;
        }

        String contentType = "text/html; charset=UTF-8";

        if (fileName.endsWith(".css")) {
            contentType = "text/css; charset=UTF-8";
        } else if (fileName.endsWith(".js")) {
            contentType = "text/javascript; charset=UTF-8";
        }

        byte[] content = Files.readAllBytes(file.toPath());
        sendResponse(exchange, 200, content, contentType);
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    private static void sendMethodNotAllowed(HttpExchange exchange)
            throws IOException {

        sendResponse(exchange, 405,
                "{\"error\":\"Method not allowed\"}",
                "application/json");
    }

    private static void sendResponse(
            HttpExchange exchange, int status,
            String response, String contentType) throws IOException {

        sendResponse(exchange, status,
                response.getBytes(StandardCharsets.UTF_8), contentType);
    }

    private static void sendResponse(
            HttpExchange exchange, int status,
            byte[] response, String contentType) throws IOException {

        exchange.getResponseHeaders().set(
                "Content-Type", contentType);
        exchange.getResponseHeaders().set(
                "Cache-Control", "no-store");

        exchange.sendResponseHeaders(status, response.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }
}
