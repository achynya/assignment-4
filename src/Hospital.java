import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Hospital {
    private int id;
    private String name;
    private String location;

    public Hospital(int id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public static void addHospital() {
        String sql = "INSERT INTO hospitals (name, location) VALUES (?, ?)";
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter hospital name: ");
        String name = sc.nextLine();
        System.out.print("Enter hospital location: ");
        String location = sc.nextLine();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, location);
            stmt.executeUpdate();
            System.out.println("Hospital added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Hospital> getAllHospitals() {
        List<Hospital> hospitals = new ArrayList<>();
        String sql = "SELECT * FROM hospitals";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                hospitals.add(new Hospital(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("location")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hospitals;
    }

    public static void editHospital() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the ID of the hospital you want to edit: ");
        int hospitalId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Do you want to edit or delete the hospital?(E/D): ");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("E")) {
            String fetchSql = "SELECT * FROM hospitals WHERE id = ?";
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement fetchStmt = conn.prepareStatement(fetchSql)) {
                fetchStmt.setInt(1, hospitalId);
                ResultSet rs = fetchStmt.executeQuery();
                if (!rs.next()) {
                    System.out.println("No hospital found with ID: " + hospitalId);
                    return;
                }
                System.out.println("Current details for Hospital ID " + hospitalId + ":");
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Location: " + rs.getString("location"));
                System.out.print("Enter new name (leave blank to keep current): ");
                String newName = scanner.nextLine();
                if (newName.isEmpty()) {
                    newName = rs.getString("name");
                }
                System.out.print("Enter new location (leave blank to keep current): ");
                String newLocation = scanner.nextLine();
                if (newLocation.isEmpty()) {
                    newLocation = rs.getString("location");
                }
                String updateSql = "UPDATE hospitals SET name = ?, location = ? WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, newName);
                    updateStmt.setString(2, newLocation);
                    updateStmt.setInt(3, hospitalId);
                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Hospital updated successfully!");
                    } else {
                        System.out.println("Failed to update the hospital.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (choice.equalsIgnoreCase("D")) {
            String deleteSql = "DELETE FROM hospitals WHERE id = ?";
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, hospitalId);
                int rowsAffected = deleteStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Hospital with ID " + hospitalId + " deleted successfully!");
                } else {
                    System.out.println("No hospital found with ID: " + hospitalId);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void getSortedHospitals() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Sort hospitals by: (name, location, id): ");
        String sortBy = scanner.nextLine();

        System.out.print("Order: (ASC or DESC): ");
        String order = scanner.nextLine().toUpperCase();

        String sql = "SELECT * FROM hospitals ORDER BY " + sortBy + " " + order;

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println("Hospital ID: " + rs.getInt("id") +
                        ", Name: " + rs.getString("name") +
                        ", Location: " + rs.getString("location"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Hospital ID: " + id + ", Name: " + name + ", Location: " + location;
    }
}
