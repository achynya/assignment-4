import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Hospital Management System ---");
            System.out.println("1. Add Hospital");
            System.out.println("2. Add Doctor");
            System.out.println("3. Add Patient");
            System.out.println("4. View Hospitals");
            System.out.println("5. View Doctors");
            System.out.println("6. View Patients");
            System.out.println("7. Edit Hospital");
            System.out.println("8. Edit Doctor");
            System.out.println("9. Edit Patient");
            System.out.println("10. Sort Hospital");
            System.out.println("11. Sort Doctor");
            System.out.println("12. Sort Patient");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> Hospital.addHospital();
                case 2 -> Doctor.addDoctor();
                case 3 -> Patient.addPatient();
                case 4 -> Hospital.getAllHospitals().forEach(System.out::println);
                case 5 -> Doctor.getAllDoctors().forEach(System.out::println);
                case 6 -> Patient.getAllPatients().forEach(System.out::println);
                case 7 -> Hospital.editHospital();
                case 8 -> Doctor.editDoctor();
                case 9 -> Patient.editPatient();
                case 10 -> Hospital.getSortedHospitals();
                case 11 -> Doctor.getSortedDoctors();
                case 12 -> Patient.getSortedPatients();
                case 0 -> { System.out.println("Exiting..."); return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }
}
