// Switch between the four frontend sections without reloading the page.
function showPage(pageId, clickedButton) {
    document.querySelectorAll(".page").forEach(function (page) {
        page.classList.add("hidden");
    });

    const selectedPage = document.getElementById(pageId);
    if (selectedPage) {
        selectedPage.classList.remove("hidden");
    }

    document.querySelectorAll(".nav-button").forEach(function (button) {
        button.classList.toggle("active", button.dataset.page === pageId);
    });

    // Refresh data when a section is opened.
    if (pageId === "studentsPage" && typeof loadStudentTable === "function") {
        loadStudentTable(document.getElementById("studentSearchInput").value);
    } else if (pageId === "addResourcePage" && typeof loadStudents === "function") {
        loadStudents();
    } else if (pageId === "cataloguePage" && typeof loadResources === "function") {
        loadResources(document.getElementById("searchInput").value);
    }
    else if (pageId === "borrowingPage") {
    loadBorrowingPage();
    } 
}


// ---------------- RESOURCE MANAGEMENT ----------------

const resourceForm = document.getElementById("resourceForm");
const resourceTable = document.getElementById("resourceTable");
const message = document.getElementById("message");
const formMessage = document.getElementById("formMessage");
const category = document.getElementById("category");
const ownerSelect = document.getElementById("ownerId");
const currentStudentSelect = document.getElementById("currentStudent");
const borrowStudentSelect = document.getElementById("borrowStudent");

let currentStudentId = "";

function showCategoryFields() {
    const selected = category.value;

    const customCategoryField = document.getElementById("customCategoryField");
const customCategory = document.getElementById("customCategory");

customCategoryField.classList.toggle("hidden", selected !== "Other");
customCategory.required = selected === "Other";

    document.getElementById("bookFields").classList.toggle(
        "hidden", selected !== "Textbook" && selected !== "Notes");

    document.getElementById("editionField").classList.toggle(
        "hidden", selected !== "Textbook");

    document.getElementById("notesField").classList.toggle(
        "hidden", selected !== "Notes");

    document.getElementById("equipmentFields").classList.toggle(
        "hidden", selected !== "Lab Equipment");

    document.getElementById("calculatorFields").classList.toggle(
        "hidden", selected !== "Scientific Calculator");

    const bookSelected =
        selected === "Textbook" || selected === "Notes";

    document.getElementById("author").required = bookSelected;
    document.getElementById("subject").required = bookSelected;

    document.getElementById("edition").required =
        selected === "Textbook";

    document.getElementById("noteType").required =
        selected === "Notes";

    document.getElementById("equipmentType").required =
        selected === "Lab Equipment";

    document.getElementById("laboratory").required =
        selected === "Lab Equipment";

    document.getElementById("brand").required =
        selected === "Scientific Calculator";

    document.getElementById("model").required =
        selected === "Scientific Calculator";

    document.getElementById("hasCover").required =
        selected === "Scientific Calculator";
}


async function loadStudents() {
    try {
        const response = await fetch("/api/students");

        if (!response.ok) {
            throw new Error("Could not load students.");
        }

        const students = await response.json();

        // Populate the resource owner dropdown.
        ownerSelect.innerHTML =
            '<option value="">Select resource owner</option>';

        students.forEach(function (student) {
            const option = document.createElement("option");
            option.value = student.studentId;
            option.textContent =
                student.name + " (ID " + student.studentId + ")";
            ownerSelect.appendChild(option);
        });

        // Populate the Current Student dropdown.
        const previousStudentId = currentStudentId;

        currentStudentSelect.innerHTML =
            '<option value="">Select a student</option>';

        students.forEach(function (student) {
            const option = document.createElement("option");
            option.value = student.studentId;
            option.textContent =
                student.name + " (ID " + student.studentId + ")";
            currentStudentSelect.appendChild(option);
        });

        const studentStillExists = students.some(function (student) {
            return String(student.studentId) ===
                String(previousStudentId);
        });

        if (studentStillExists) {
            currentStudentSelect.value = previousStudentId;
        } else {
            currentStudentId = "";
            currentStudentSelect.value = "";
        }

        updateCurrentStudent();

    } catch (error) {
        ownerSelect.innerHTML =
            '<option value="">Unable to load students</option>';

        currentStudentSelect.innerHTML =
            '<option value="">Unable to load students</option>';

        document.getElementById("formMessage").textContent =
            error.message;
    }
}

function updateCurrentStudent() {
    currentStudentId = currentStudentSelect.value;

    if (currentStudentId) {
        borrowStudentSelect.innerHTML = "";

        const currentOption = document.createElement("option");
        currentOption.value = currentStudentId;
        currentOption.textContent =
            currentStudentSelect.options[
                currentStudentSelect.selectedIndex
            ].textContent;

        borrowStudentSelect.appendChild(currentOption);
        borrowStudentSelect.value = currentStudentId;
    } else {
        borrowStudentSelect.innerHTML =
            '<option value="">Select a Current Student above</option>';
    }

    loadBorrowingRequests();
    loadBorrowingTransactions();
}

currentStudentSelect.addEventListener(
    "change", updateCurrentStudent
);


async function loadResources(searchTerm = "") {
    message.textContent = "Loading resources...";
    resourceTable.innerHTML = "";

    try {
        let url = "/api/resources";

        if (searchTerm.trim() !== "") {
            url += "?search=" + encodeURIComponent(searchTerm.trim());
        }

        const response = await fetch(url);

        if (!response.ok) {
            throw new Error("Could not load resources.");
        }

        const resources = await response.json();

        if (resources.length === 0) {
            message.textContent = "No resources found.";
            return;
        }

        message.textContent = "";

        resources.forEach(function (resource) {
            const row = document.createElement("tr");

            const values = [
                resource.resourceId,
                resource.name,
                resource.category,
                resource.description,
                resource.condition,
                resource.ownerName,
                resource.available ? "Available" : "Unavailable"
            ];

            values.forEach(function (value, index) {
                const cell = document.createElement("td");
                cell.textContent = value ?? "";

                if (index === 6) {
                    cell.className = resource.available
                        ? "status"
                        : "unavailable";
                }

                row.appendChild(cell);
            });

            const actionCell = document.createElement("td");

            const editButton = document.createElement("button");
            editButton.textContent = "Edit";
            editButton.type = "button";
            editButton.addEventListener("click", function () {
                editResource(resource);
            });

            const deleteButton = document.createElement("button");
            deleteButton.textContent = "Delete";
            deleteButton.type = "button";
            deleteButton.className = "secondary";
            deleteButton.addEventListener("click", function () {
                deleteResource(resource.resourceId);
            });

            actionCell.appendChild(editButton);
            actionCell.appendChild(deleteButton);
            row.appendChild(actionCell);
            resourceTable.appendChild(row);
        });

    } catch (error) {
        message.textContent =
            "Unable to load resources. Check that the Java server is running.";
    }
}


resourceForm.addEventListener("submit", async function (event) {
    event.preventDefault();
    formMessage.textContent = "Adding resource...";

    const formData = new FormData(resourceForm);

if (formData.get("category") === "Other") {
    const customCategory = formData.get("customCategory").trim();

    if (!customCategory) {
        formMessage.textContent = "Please enter a resource category.";
        return;
    }

    formData.set("category", customCategory);
}

const data = new URLSearchParams();

for (const [key, value] of formData.entries()) {
    if (key !== "customCategory") {
        data.append(key, value);
    }
}

    try {
        const response = await fetch("/api/resources", {
            method: "POST",
            body: data
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.error || "Could not add resource.");
        }

        formMessage.textContent = result.message;
        resourceForm.reset();
        showCategoryFields();

        await loadResources();
        await loadStudents();

    } catch (error) {
        formMessage.textContent = error.message;
    }
});


async function editResource(resource) {
    const name = prompt("Enter the resource name:", resource.name);

    if (name === null) {
        return;
    }

    const description = prompt(
        "Enter the description:", resource.description);

    if (description === null) {
        return;
    }

    const condition = prompt(
        "Enter condition (New, Good or Fair):", resource.condition);

    if (condition === null) {
        return;
    }

    if (!name.trim() || !description.trim() || !condition.trim()) {
        alert("Fields cannot be empty.");
        return;
    }

    const data = new URLSearchParams();
    data.append("name", name.trim());
    data.append("description", description.trim());
    data.append("condition", condition.trim());

    try {
        const response = await fetch(
            "/api/resources/" + resource.resourceId, {
                method: "PUT",
                body: data
            });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.error || "Could not update resource.");
        }

        alert(result.message);
        await loadResources();

    } catch (error) {
        alert(error.message);
    }
}


async function deleteResource(id) {
    if (!confirm("Are you sure you want to delete resource ID " + id + "?")) {
        return;
    }

    try {
        const response = await fetch("/api/resources/" + id, {
            method: "DELETE"
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.error || "Could not delete resource.");
        }

        alert(result.message);
        await loadResources();

    } catch (error) {
        alert(error.message);
    }
}


category.addEventListener("change", showCategoryFields);

resourceForm.addEventListener("reset", function () {
    setTimeout(showCategoryFields, 0);
});

document.getElementById("refreshButton").addEventListener(
    "click", function () {
        document.getElementById("searchInput").value = "";
        loadResources();
    });

document.getElementById("searchButton").addEventListener(
    "click", function () {
        loadResources(document.getElementById("searchInput").value);
    });

document.getElementById("clearSearchButton").addEventListener(
    "click", function () {
        document.getElementById("searchInput").value = "";
        loadResources();
    });

showCategoryFields();
loadStudents();
loadResources();


// ---------------- STUDENT MANAGEMENT ----------------

const studentForm = document.getElementById("studentForm");
const studentTable = document.getElementById("studentTable");
const studentMessage = document.getElementById("studentMessage");
const studentFormMessage =
    document.getElementById("studentFormMessage");


async function loadStudentTable(searchTerm = "") {
    studentMessage.textContent = "Loading students...";
    studentTable.innerHTML = "";

    try {
        const response = await fetch("/api/students");
        const students = await response.json();

        if (!response.ok) {
            throw new Error(students.error || "Could not load students.");
        }

        const term = searchTerm.trim().toLowerCase();

        const filteredStudents = students.filter(function (student) {
            return student.name.toLowerCase().includes(term)
                || student.email.toLowerCase().includes(term);
        });

        if (filteredStudents.length === 0) {
            studentMessage.textContent = "No students found.";
            return;
        }

        studentMessage.textContent = "";

        filteredStudents.forEach(function (student) {
            const row = document.createElement("tr");

            const values = [
                student.studentId,
                student.name,
                student.email,
                student.phone,
                student.department,
                student.year
            ];

            values.forEach(function (value) {
                const cell = document.createElement("td");
                cell.textContent = value ?? "";
                row.appendChild(cell);
            });

            const actionCell = document.createElement("td");

            const editButton = document.createElement("button");
            editButton.type = "button";
            editButton.textContent = "Edit";
            editButton.addEventListener("click", function () {
                editStudent(student);
            });

            const deleteButton = document.createElement("button");
            deleteButton.type = "button";
            deleteButton.textContent = "Delete";
            deleteButton.className = "secondary";
            deleteButton.addEventListener("click", function () {
                deleteStudent(student.studentId);
            });

            actionCell.appendChild(editButton);
            actionCell.appendChild(deleteButton);
            row.appendChild(actionCell);
            studentTable.appendChild(row);
        });

    } catch (error) {
        studentMessage.textContent = error.message;
    }
}


studentForm.addEventListener("submit", async function (event) {
    event.preventDefault();
    studentFormMessage.textContent = "Adding student...";

    const formData = new FormData(studentForm);
    const data = new URLSearchParams();

    for (const [key, value] of formData.entries()) {
        data.append(key, value);
    }

    try {
        const response = await fetch("/api/students", {
            method: "POST",
            body: data
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.error || "Could not add student.");
        }

        studentFormMessage.textContent =
            result.message + ". Student ID: " + result.studentId;

        studentForm.reset();

        await loadStudentTable();
        await loadStudents();

    } catch (error) {
        studentFormMessage.textContent = error.message;
    }
});


async function editStudent(student) {
    const name = prompt("Enter student name:", student.name);
    if (name === null) return;

    const email = prompt("Enter student email:", student.email);
    if (email === null) return;

    const phone = prompt("Enter student phone:", student.phone);
    if (phone === null) return;

    const department = prompt(
        "Enter department:", student.department);
    if (department === null) return;

    const year = prompt(
        "Enter year of study (1 to 6):", student.year);
    if (year === null) return;

    if (!name.trim() || !email.trim() || !phone.trim()
            || !department.trim() || !year.trim()
            || !Number.isInteger(Number(year))
            || Number(year) < 1 || Number(year) > 6) {
        alert("Enter valid details. Year must be between 1 and 6.");
        return;
    }

    const data = new URLSearchParams();
    data.append("name", name.trim());
    data.append("email", email.trim());
    data.append("phone", phone.trim());
    data.append("department", department.trim());
    data.append("year", year.trim());

    try {
        const response = await fetch(
            "/api/students/" + student.studentId, {
                method: "PUT",
                body: data
            });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.error || "Could not update student.");
        }

        alert(result.message);

        await loadStudentTable(
            document.getElementById("studentSearchInput").value);

        await loadStudents();

    } catch (error) {
        alert(error.message);
    }
}


async function deleteStudent(id) {
    if (!confirm(
        "Delete student ID " + id + "? This cannot be undone.")) {
        return;
    }

    try {
        const response = await fetch("/api/students/" + id, {
            method: "DELETE"
        });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.error || "Could not delete student.");
        }

        alert(result.message);

        await loadStudentTable(
            document.getElementById("studentSearchInput").value);

        await loadStudents();

    } catch (error) {
        alert(error.message);
    }
}


document.getElementById("studentSearchButton").addEventListener(
    "click", function () {
        loadStudentTable(
            document.getElementById("studentSearchInput").value);
    });

document.getElementById("clearStudentSearchButton").addEventListener(
    "click", function () {
        document.getElementById("studentSearchInput").value = "";
        loadStudentTable();
    });

document.getElementById("refreshStudentsButton").addEventListener(
    "click", function () {
        document.getElementById("studentSearchInput").value = "";
        loadStudentTable();
        loadStudents();
    });

document.getElementById("studentSearchInput").addEventListener(
    "keydown", function (event) {
        if (event.key === "Enter") {
            event.preventDefault();
            loadStudentTable(this.value);
        }
    });

loadStudentTable();


// Show Home when the application first opens.
showPage("homePage");


 // ---------------- BORROWING MANAGEMENT ----------------

async function loadBorrowingPage() {
    await Promise.all([
        loadBorrowingOptions(),
        loadBorrowingRequests(),
        loadBorrowingTransactions()
    ]);
}


async function loadBorrowingOptions() {
    const studentSelect = document.getElementById("borrowStudent");
    const resourceSelect = document.getElementById("borrowResource");

    try {
        const [studentResponse, resourceResponse] = await Promise.all([
            fetch("/api/students"),
            fetch("/api/resources")
        ]);

        if (!studentResponse.ok || !resourceResponse.ok) {
            throw new Error("Could not load students or resources.");
        }

        const students = await studentResponse.json();
        const resources = await resourceResponse.json();

        studentSelect.innerHTML =
            '<option value="">Select requesting student</option>';

        students.forEach(function (student) {
            const option = document.createElement("option");
            option.value = student.studentId;
            option.textContent =
                student.name + " (ID " + student.studentId + ")";
            studentSelect.appendChild(option);
        });

        resourceSelect.innerHTML =
            '<option value="">Select available resource</option>';

        resources.filter(function (resource) {
            return resource.available;
        }).forEach(function (resource) {
            const option = document.createElement("option");
            option.value = resource.resourceId;
            option.textContent =
                resource.name + " (ID " + resource.resourceId + ")";
            option.dataset.ownerId = resource.ownerId;
            resourceSelect.appendChild(option);
        });

    } catch (error) {
        studentSelect.innerHTML = '<option value="">Unable to load students</option>';
        resourceSelect.innerHTML = '<option value="">Unable to load resources</option>';
        document.getElementById("borrowFormMessage").textContent =
            error.message;
    }
}


document.getElementById("borrowRequestForm").addEventListener(
    "submit", async function (event) {
        event.preventDefault();

        const studentId = currentStudentId;
        const resourceId = document.getElementById("borrowResource").value;
        const message = document.getElementById("borrowFormMessage");

        if (!studentId) {
    message.textContent = "Select a Current Student first.";
    return;
}

if (!resourceId) {
    message.textContent = "Select a resource to borrow.";
    return;
}

        const data = new URLSearchParams();
        data.append("studentId", studentId);
        data.append("resourceId", resourceId);

        message.textContent = "Submitting request...";

        try {
            const response = await fetch("/api/borrow-requests", {
                method: "POST",
                body: data
            });

            const result = await response.json();

            if (!response.ok) {
                throw new Error(result.error || result.message
                    || "Could not create borrowing request.");
            }

            message.textContent = result.message
                + " (Request ID: " + result.requestId + ")";

            this.reset();
            await loadBorrowingPage();

        } catch (error) {
            message.textContent = error.message;
        }
    }
);


// The current GET endpoint returns each request as a formatted string.
function parseBorrowRequest(line) {
    const match = line.match(
        /^Request ID: (\d+) \| Requester: (.*?) \(ID (\d+)\) \| Resource: (.*?) \(ID (\d+)\) \| Status: (.*?) \| Date: (.*)$/
    );

    if (!match) return null;

    return {
        id: Number(match[1]),
        requester: match[2],
        studentId: Number(match[3]),
        resource: match[4],
        resourceId: Number(match[5]),
        status: match[6],
        date: match[7]
    };
}


async function loadBorrowingRequests() {
    const table = document.getElementById("borrowRequestsTable");
    const message = document.getElementById("borrowRequestsMessage");

    table.innerHTML = "";
    message.textContent = "Loading requests...";

    try {
        const [requestResponse, resourceResponse] = await Promise.all([
            fetch("/api/borrow-requests"),
            fetch("/api/resources")
        ]);

        if (!requestResponse.ok || !resourceResponse.ok) {
            throw new Error("Could not load borrowing requests.");
        }

        const requestLines = await requestResponse.json();
        const resources = await resourceResponse.json();

        const requests = requestLines.map(parseBorrowRequest)
            .filter(Boolean);

        if (requests.length === 0) {
            message.textContent = "No borrowing requests found.";
            return;
        }

        message.textContent = "";

        requests.forEach(function (request) {
            const row = document.createElement("tr");

            [
                request.id,
                request.requester + " (ID " + request.studentId + ")",
                request.resource + " (ID " + request.resourceId + ")",
                request.status,
                request.date
            ].forEach(function (value) {
                const cell = document.createElement("td");
                cell.textContent = value;
                row.appendChild(cell);
            });

            const actionCell = document.createElement("td");
            const resource = resources.find(function (item) {
                return Number(item.resourceId) === request.resourceId;
            });

            if (request.status === "PENDING" && resource && String(resource.ownerId) === String(currentStudentId) && String(request.studentId) !== String(currentStudentId)) {
                const approveButton = document.createElement("button");
                approveButton.textContent = "Approve";
                approveButton.type = "button";
                approveButton.addEventListener("click", function () {
                    decideBorrowRequest(request.id, resource.ownerId, "approve");
                });

                const rejectButton = document.createElement("button");
                rejectButton.textContent = "Reject";
                rejectButton.type = "button";
                rejectButton.className = "secondary";
                rejectButton.addEventListener("click", function () {
                    decideBorrowRequest(request.id, resource.ownerId, "reject");
                });

                actionCell.appendChild(approveButton);
                actionCell.appendChild(rejectButton);
            } else {
                actionCell.textContent = "—";
            }

            row.appendChild(actionCell);
            table.appendChild(row);
        });

    } catch (error) {
        message.textContent = error.message;
    }
}


async function decideBorrowRequest(requestId, ownerId, decision) {
    if (!ownerId) {
        alert("The resource owner ID could not be found.");
        return;
    }

    const action = decision === "approve" ? "approve" : "reject";

    if (!confirm("Are you sure you want to " + action
            + " request " + requestId + "?")) {
        return;
    }

    const data = new URLSearchParams();
    data.append("ownerId", ownerId);

    try {
        const response = await fetch(
            "/api/borrow-requests/" + requestId + "/" + action, {
                method: "POST",
                body: data
            });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.error || result.message
                || "Could not update request.");
        }

        alert(result.message || "Request " + action + "d.");
        await loadBorrowingPage();

    } catch (error) {
        alert(error.message);
    }
}


// The transaction endpoint currently returns formatted strings.
function parseBorrowTransaction(line) {
    const match = line.match(
        /^Transaction ID: (\d+) \| Request ID: (\d+) \| Borrower: (.*?) \| Resource: (.*?) \| Borrow Date: (.*?) \| Due Date: (.*?) \| Return Date: (.*?) \| Status: (.*)$/
    );

    if (!match) return null;

    return {
        id: Number(match[1]),
        requestId: Number(match[2]),
        borrower: match[3],
        resource: match[4],
        borrowDate: match[5],
        dueDate: match[6],
        returnDate: match[7],
        status: match[8]
    };
}


async function loadBorrowingTransactions() {
    const table = document.getElementById("transactionsTable");
    const message = document.getElementById("transactionsMessage");

    table.innerHTML = "";
    message.textContent = "Loading transactions...";

    try {
        const [transactionResponse, requestResponse] = await Promise.all([
            fetch("/api/borrow-transactions"),
            fetch("/api/borrow-requests")
        ]);

        if (!transactionResponse.ok || !requestResponse.ok) {
            throw new Error("Could not load borrowing transactions.");
        }

        const transactionLines = await transactionResponse.json();
        const requestLines = await requestResponse.json();

        const transactions = transactionLines.map(parseBorrowTransaction)
            .filter(Boolean);

        const requests = requestLines.map(parseBorrowRequest)
            .filter(Boolean);

        if (transactions.length === 0) {
            message.textContent = "No borrowing transactions found.";
            return;
        }

        message.textContent = "";

        transactions.forEach(function (transaction) {
            const row = document.createElement("tr");

            [
                transaction.id,
                transaction.borrower,
                transaction.resource,
                transaction.borrowDate,
                transaction.dueDate,
                transaction.returnDate === "null"
                    || transaction.returnDate === ""
                    ? "Not returned"
                    : transaction.returnDate,
                transaction.status
            ].forEach(function (value) {
                const cell = document.createElement("td");
                cell.textContent = value;
                row.appendChild(cell);
            });

            const actionCell = document.createElement("td");

const request = requests.find(function (item) {
    return item.id === transaction.requestId;
});

if (
    transaction.status === "Borrowed"
    && request
    && String(request.studentId) === String(currentStudentId)
) {
    const returnButton = document.createElement("button");
    returnButton.type = "button";
    returnButton.textContent = "Mark Returned";

    returnButton.addEventListener("click", function () {
        returnBorrowedResource(
            transaction.id, currentStudentId
        );
    });

    actionCell.appendChild(returnButton);
} else {
    actionCell.textContent = "—";
}

row.appendChild(actionCell);
            table.appendChild(row);
        });

    } catch (error) {
        message.textContent = error.message;
    }
}


async function returnBorrowedResource(transactionId, studentId) {
    if (!confirm("Mark transaction " + transactionId + " as returned?")) {
        return;
    }

    const data = new URLSearchParams();
    data.append("studentId", studentId);

    try {
        const response = await fetch(
            "/api/borrow-transactions/" + transactionId + "/return", {
                method: "POST",
                body: data
            });

        const result = await response.json();

        if (!response.ok) {
            throw new Error(result.error || result.message
                || "Could not return resource.");
        }

        alert(result.message || "Resource returned successfully.");
        await loadBorrowingPage();
        await loadResources();

    } catch (error) {
        alert(error.message);
    }
}


document.getElementById("refreshBorrowingButton").addEventListener(
    "click", loadBorrowingPage);


    async function loadResourcesByCategory() {
    const reportTable =
        document.getElementById("categoryReportTable");

    const reportMessage =
        document.getElementById("categoryReportMessage");

    reportTable.innerHTML = "";
    reportMessage.textContent = "Loading report...";

    try {
        const response = await fetch(
            "/api/reports/resources-by-category"
        );

        if (!response.ok) {
            throw new Error("Could not load category report.");
        }

        const categories = await response.json();

        if (categories.length === 0) {
            reportMessage.textContent = "No resources found.";
            return;
        }

        categories.forEach(function (item) {
            const row = document.createElement("tr");

            const categoryCell = document.createElement("td");
            categoryCell.textContent = item.category ?? "";

            const countCell = document.createElement("td");
            countCell.textContent = item.totalResources;

            row.appendChild(categoryCell);
            row.appendChild(countCell);

            reportTable.appendChild(row);
        });

        reportMessage.textContent = "";

    } catch (error) {
        reportMessage.textContent = error.message;
    }
}

document
    .getElementById("loadCategoryReport")
    .addEventListener("click", loadResourcesByCategory);
