import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Patient extends Person {
    private String diagnosis;
    private int hospitalId;

    public Patient(int id, String name, int age, String gender, String diagnosis, int hospitalId) {
        super(id, name, age, gender);
        this.diagnosis = diagnosis;
        this.hospitalId = hospitalId;
    }

    public static void addPatient() {
        String sql = "INSERT INTO patients (name, age, gender, diagnosis, hospital_id) VALUES (?, ?, ?, ?, ?)";
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter patient name: ");
        String name = sc.nextLine();
        System.out.print("Enter patient age: ");
        int age = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter patient gender: ");
        String gender = sc.nextLine();
        System.out.print("Enter patient diagnosis: ");
        String diagnosis = sc.nextLine();
        System.out.print("Enter patient hospital_id: ");
        int hospitalId = sc.nextInt();
        sc.nextLine();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, age);
            stmt.setString(3, gender);
            stmt.setString(4, diagnosis);
            stmt.setInt(5, hospitalId);
            stmt.executeUpdate();
            System.out.println("Patient added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                patients.add(new Patient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("diagnosis"),
                        rs.getInt("hospital_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patients;
    }

    public static void editPatient() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the ID of the patient you want to edit: ");
        int patientId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Do you want edit or delete the patient?(E/D): ");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("e")) {
            String fetchSql = "SELECT * FROM patients WHERE id = ?";
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement fetchStmt = conn.prepareStatement(fetchSql)) {

                fetchStmt.setInt(1, patientId);
                ResultSet rs = fetchStmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("No patient found with ID: " + patientId);
                    return;
                }

                System.out.println("Current details for Patient ID " + patientId + ":");
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Age: " + rs.getInt("age"));
                System.out.println("Gender: " + rs.getString("gender"));
                System.out.println("Diagnosis: " + rs.getString("diagnosis"));
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

                System.out.print("Enter new diagnosis (leave blank to keep current): ");
                String newDiagnosis = scanner.nextLine();
                if (newDiagnosis.isEmpty()) {
                    newDiagnosis = rs.getString("diagnosis");
                }

                System.out.print("Enter new hospital ID (leave blank to keep current): ");
                String hospitalIdInput = scanner.nextLine();
                int newHospitalId = hospitalIdInput.isEmpty() ? rs.getInt("hospital_id") : Integer.parseInt(hospitalIdInput);

                String updateSql = "UPDATE patients SET name = ?, age = ?, gender = ?, diagnosis = ?, hospital_id = ? WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, newName);
                    updateStmt.setInt(2, newAge);
                    updateStmt.setString(3, newGender);
                    updateStmt.setString(4, newDiagnosis);
                    updateStmt.setInt(5, newHospitalId);
                    updateStmt.setInt(6, patientId);

                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Patient updated successfully!");
                    } else {
                        System.out.println("Failed to update the patient.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (choice.equalsIgnoreCase("d")) {
            String deleteSql = "DELETE FROM patients WHERE id = ?";
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, patientId);
                int rowsAffected = deleteStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Patient with ID " + patientId + " deleted successfully!");
                } else {
                    System.out.println("No patient found with ID: " + patientId);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("You entered wrong choice! Try again later");
        }
    }

    public static void getSortedPatients() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Sort patients by: (name, age, id): ");
        String sortBy = scanner.nextLine();

        System.out.print("Order: (ASC or DESC): ");
        String order = scanner.nextLine().toUpperCase();

        String sql = "SELECT * FROM patients ORDER BY " + sortBy + " " + order;

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println("Patient ID: " + rs.getInt("id") +
                        ", Name: " + rs.getString("name") +
                        ", Age: " + rs.getInt("age") +
                        ", Gender: " + rs.getString("gender") +
                        ", Diagnosis: " + rs.getString("diagnosis") +
                        ", Hospital ID: " + rs.getInt("hospital_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Patient ID: " + getId() + ", Patient: " + getName() + ", Age: " + getAge() + ", Gender: " + getGender() +  ", Diagnosis: " + diagnosis + ", Hospital ID: " + hospitalId;
    }
}
