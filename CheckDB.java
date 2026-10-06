import java.sql.*;
public class CheckDB {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=shopbangiay;encrypt=false;trustServerCertificate=true";
        try (Connection conn = DriverManager.getConnection(url, "sa", "123456789");
             Statement stmt = conn.createStatement()) {
            
            ResultSet rsRole = stmt.executeQuery("SELECT * FROM roles");
            System.out.println("--- ROLES ---");
            while(rsRole.next()) {
                System.out.println("ID: " + rsRole.getInt("id") + " | Name: " + rsRole.getString("name"));
            }
            
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            System.out.println("--- USERS ---");
            while(rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + " | Username: " + rs.getString("username") + 
                " | Password: " + rs.getString("password") + " | RoleID: " + rs.getInt("role_id") + 
                " | Status: " + rs.getBoolean("status"));
            }
        }
    }
}
