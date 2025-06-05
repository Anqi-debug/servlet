package org.example.servletdemo.httpServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

// Map the servlet to URL "/employee"
@WebServlet("/employee")
public class EmployeeHttpServlet extends HttpServlet {
    // Utility method to get JDBC Connection
    private Connection getConnection() throws SQLException {
        String jdbcURL = "jdbc:mysql://localhost:3306/servlet?useSSL=false&serverTimezone=UTC";
        String jdbcUsername = "root";
        String jdbcPassword = "PW";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }

        Connection conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        System.out.println("✅ Database connection successful.");
        return conn;
    }

    // Employee POJO used for JSON serialization/deserialization.
    public static class Employee {
        public int id;
        public String name;
        public String email;
    }

    // JSON converter
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String idParam = request.getParameter("id");

        try (Connection conn = getConnection()) {
            if (idParam != null) {
                // Retrieve specific employee record
                int id = Integer.parseInt(idParam);
                String sql = "SELECT * FROM employee WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        Employee emp = new Employee();
                        emp.id = rs.getInt("id");
                        emp.name = rs.getString("name");
                        emp.email = rs.getString("email");
                        out.print(gson.toJson(emp));
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print("{\"message\":\"Employee not found\"}");
                    }
                }
            } else {
                // Retrieve all employee records
                String sql = "SELECT * FROM employee";
                try (Statement stmt = conn.createStatement()) {
                    ResultSet rs = stmt.executeQuery(sql);
                    List<Employee> empList = new ArrayList<>();
                    while (rs.next()) {
                        Employee emp = new Employee();
                        emp.id = rs.getInt("id");
                        emp.name = rs.getString("name");
                        emp.email = rs.getString("email");
                        empList.add(emp);
                    }
                    out.print(gson.toJson(empList));
                }
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    // POST for CREATE operation
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        // Read parameters (assuming application/x-www-form-urlencoded or JSON)
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO employee (name, email) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, email);
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    ResultSet generatedKeys = ps.getGeneratedKeys();
                    int newId = -1;
                    if (generatedKeys.next()) {
                        newId = generatedKeys.getInt(1);
                    }
                    response.getWriter().print("{\"message\":\"Employee created\", \"id\":" + newId + "}");
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().print("{\"message\":\"Failed to create employee\"}");
                }
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    // PUT for UPDATE operation
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // To support PUT, we’ll read parameters from the request body (assumed JSON)
        response.setContentType("application/json");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        // Parse JSON to Employee object
        Employee emp = gson.fromJson(sb.toString(), Employee.class);

        try (Connection conn = getConnection()) {
            String sql = "UPDATE employee SET name = ?, email = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, emp.name);
                ps.setString(2, emp.email);
                ps.setInt(3, emp.id);
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    response.getWriter().print("{\"message\":\"Employee updated\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().print("{\"message\":\"Employee not found\"}");
                }
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    // DELETE for DELETE operation
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        String idParam = request.getParameter("id");
        if(idParam == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"message\":\"Missing id parameter\"}");
            return;
        }
        int id = Integer.parseInt(idParam);
        try (Connection conn = getConnection()) {
            String sql = "DELETE FROM employee WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    response.getWriter().print("{\"message\":\"Employee deleted\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().print("{\"message\":\"Employee not found\"}");
                }
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }
}
