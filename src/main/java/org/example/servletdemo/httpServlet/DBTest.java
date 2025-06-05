package org.example.servletdemo.httpServlet;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBTest {
    public static void main(String[] args) {
        String jdbcURL = "jdbc:mysql://localhost:3306/servlet?useSSL=false&serverTimezone=UTC";
        String jdbcUsername = "root";
        String jdbcPassword = "PW";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
            System.out.println("✅ Database connection successful.");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
