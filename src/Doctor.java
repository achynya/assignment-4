import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Doctor extends Person {
    private String specialty;
    private int hospitalId;

    public Doctor(int id, String name, int age, String gender, String specialty, int hospitalId) {
        super(id, name, age, gender);
        this.specialty = specialty;
        this.hospitalId = hospitalId;
    }

    public static void addDoctor (){
        String sql = "INSERT INTO doctors (name, age, gender, specialty, hospital_id) VALUES (?, ?, ?, ?, ?)";
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter doctor's name: ");
        String name = scanner.nextLine();
        System.out.print("Enter doctor's age: ");
        int age = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter doctor's gender: ");
        String gender = scanner.nextLine();
        System.out.print("Enter doctor's specialty: ");
        String specialty = scanner.nextLine();
        System.out.print("Enter hospital ID: ");
        int hospitalId = scanner.nextInt();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String formattedName = "Dr. " + name;
            stmt.setString(1, formattedName);
            stmt.setInt(2, age);
            stmt.setString(3, gender);
            stmt.setString(4, specialty);
            stmt.setInt(5, hospitalId);
            stmt.executeUpdate();
            System.out.println("Doctor added successfully!");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                doctors.add(new Doctor(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("specialty"),
                        rs.getInt("hospital_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }


    public static void editDoctor() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the ID of the doctor you want to edit: ");
        int doctorId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Do you want to edit the doctor's name(E/D): ");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("E")) {
            String fetchSql = "SELECT * FROM doctors WHERE id = ?";
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement fetchStmt = conn.prepareStatement(fetchSql)) {

                fetchStmt.setInt(1, doctorId);
                ResultSet rs = fetchStmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("No doctor found with ID: " + doctorId);
                    return;
                }

                System.out.println("Current details for Doctor ID " + doctorId + ":");
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Age: " + rs.getInt("age"));
                System.out.println("Gender: " + rs.getString("gender"));
                System.out.println("Specialty: " + rs.getString("specialty"));
                System.out.println("Hospital ID: " + rs.getInt("hospital_id"));

                System.out.print("Enter new name (leave blank to keep current): ");
                String newName = scanner.nextLine();
                if (newName.isEmpty()) {
                    newName = rs.getString("name"); // Keep the current value if no input
                }

                System.out.print("Enter new age (leave blank to keep current): ");
                String ageInput = scanner.nextLine();
                int newAge = ageInput.isEmpty() ? rs.getInt("age") : Integer.parseInt(ageInput);

                System.out.print("Enter new gender (leave blank to keep current): ");
                String newGender = scanner.nextLine();
                if (newGender.isEmpty()) {
                    newGender = rs.getString("gender");
                }

                System.out.print("Enter new specialty (leave blank to keep current): ");
                String newSpecialty = scanner.nextLine();
                if (newSpecialty.isEmpty()) {
                    newSpecialty = rs.getString("specialty");
                }

                System.out.print("Enter new hospital ID (leave blank to keep current): ");
                String hospitalIdInput = scanner.nextLine();
                int newHospitalId = hospitalIdInput.isEmpty() ? rs.getInt("hospital_id") : Integer.parseInt(hospitalIdInput);

                String updateSql = "UPDATE doctors SET name = ?, age = ?, gender = ?, specialty = ?, hospital_id = ? WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, newName);
                    updateStmt.setInt(2, newAge);
                    updateStmt.setString(3, newGender);
                    updateStmt.setString(4, newSpecialty);
                    updateStmt.setInt(5, newHospitalId);
                    updateStmt.setInt(6, doctorId);

                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Doctor updated successfully!");
                    } else {
                        System.out.println("Failed to update the doctor.");
                    }
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (choice.equalsIgnoreCase("D")) {
            String deleteSql = "DELETE FROM doctors WHERE id = ?";
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, doctorId);
                int rowsAffected = deleteStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Doctor with ID " + doctorId + " deleted successfully!");
                } else {
                    System.out.println("No doctor found with ID: " + doctorId);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("You entered wrong choice! Try again later");
        }
    }

    public static void getSortedDoctors() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Sort doctors by: (name, age, specialty, id): ");
        String sortBy = scanner.nextLine();

        System.out.print("Order: (ASC or DESC): ");
        String order = scanner.nextLine().toUpperCase();

        String sql = "SELECT * FROM doctors ORDER BY " + sortBy + " " + order;

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println("Doctor ID: " + rs.getInt("id") +
                        ", Name: " + rs.getString("name") +
                        ", Age: " + rs.getInt("age") +
                        ", Gender: " + rs.getString("gender") +
                        ", Specialty: " + rs.getString("specialty") +
                        ", Hospital ID: " + rs.getInt("hospital_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Doctor ID: " + getId()+ ", Doctor: " + getName() + ", Age: " + getAge() + ", Gender: " + getGender()+ ", Specialty: " + specialty + ", Hospital ID: " + hospitalId;
    }
}
