<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Employee CRUD via Browser</title>
    <script>
        function getEmployeeById() {
            const id = document.getElementById('getId').value;
            fetch(`employee?id=${id}`)
                .then(res => res.json())
                .then(data => {
                    document.getElementById('result').textContent = JSON.stringify(data, null, 2);
                });
        }

        function getAllEmployees() {
            fetch(`employee`)
                .then(res => res.json())
                .then(data => {
                    document.getElementById('result').textContent = JSON.stringify(data, null, 2);
                });
        }

        function updateEmployee() {
            const id = document.getElementById('updateId').value;
            const name = document.getElementById('updateName').value;
            const email = document.getElementById('updateEmail').value;

            fetch('employee', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: parseInt(id), name: name, email: email })
            })
                .then(res => res.json())
                .then(data => {
                    document.getElementById('result').textContent = JSON.stringify(data, null, 2);
                });
        }

        function deleteEmployee() {
            const id = document.getElementById('deleteId').value;
            fetch(`employee?id=${id}`, {
                method: 'DELETE'
            })
                .then(res => res.json())
                .then(data => {
                    document.getElementById('result').textContent = JSON.stringify(data, null, 2);
                });
        }
    </script>
</head>
<body>
<h1>Employee CRUD Operations</h1>

<h2>Create Employee</h2>
<form method="post" action="employee">
    Name: <input type="text" name="name" required>
    Email: <input type="email" name="email" required>
    <button type="submit">Create</button>
</form>

<hr>

<h2>Read Employees</h2>
<button onclick="getAllEmployees()">Get All</button><br><br>
ID: <input type="number" id="getId">
<button onclick="getEmployeeById()">Get By ID</button>

<hr>

<h2>Update Employee</h2>
ID: <input type="number" id="updateId" required><br>
Name: <input type="text" id="updateName" required><br>
Email: <input type="email" id="updateEmail" required><br>
<button onclick="updateEmployee()">Update</button>

<hr>

<h2>Delete Employee</h2>
ID: <input type="number" id="deleteId" required>
<button onclick="deleteEmployee()">Delete</button>

<hr>

<h2>Result:</h2>
<pre id="result" style="background:#f0f0f0;padding:10px;border:1px solid #ccc;"></pre>
</body>
</html>
