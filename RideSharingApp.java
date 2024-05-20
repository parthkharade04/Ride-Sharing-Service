import java.util.*;

public class RideSharingApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Ride Sharing Service!");
        RideSharingService.Graph graph = RideSharingService.createGraph();
        DriverManager driverManager = new DriverManager();
        initializeDrivers(driverManager);

        while (true) {
            System.out.println("What would you like to do?");
            System.out.println("1. Book a ride");
            System.out.println("2. View available drivers");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    bookRide(scanner, graph, driverManager);
                    break;
                case 2:
                    viewAvailableDrivers(scanner, graph, driverManager);
                    break;
                case 3:
                    System.out.println("Thank you for using Ride Sharing Service. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 3.");
            }
        }
    }

    private static void initializeDrivers(DriverManager driverManager) {
        driverManager.addDriver(new Driver("John", true, "B"));
        driverManager.addDriver(new Driver("Alice", true, "C"));
        driverManager.addDriver(new Driver("Bob", true, "B"));
        driverManager.addDriver(new Driver("Chotu", true, "D"));
    }

    private static void bookRide(Scanner scanner, RideSharingService.Graph graph, DriverManager driverManager) {
        System.out.print("Enter your location: ");
        String userLocation = scanner.next(); // Assuming user inputs single character location like 'Node A'
        char source = userLocation.charAt(0);

        System.out.print("Enter destination location: ");
        char destination = scanner.next().charAt(0);

        RideSharingService.DijkstraResult result = RideSharingService.dijkstra(graph, source, destination);

        if (result.getDistance() == -1) {
            System.out.println("Destination node is unreachable.");
        } else {
            System.out.println(
                    "Minimum distance from node " + source + " to node " + destination + ": " + result.getDistance());
            System.out.println("Shortest path: " + result.getShortestPath());

            // Fare Calculation
            int fare = calculateFare(result.getShortestPath());
            System.out.println("Estimated fare: $" + fare);

            // Find closest available driver
            Driver closestDriver = driverManager.findClosestAvailableDriver(userLocation, graph);
            if (closestDriver != null) {
                System.out.println("Booking driver: " + closestDriver.getName());
                closestDriver.setAvailable(false);
            } else {
                System.out.println("No available drivers nearby.");
            }
        }
    }

    private static int calculateFare(List<Character> shortestPath) {
        // Simple fare calculation: $1 per unit distance
        return shortestPath.size() * 15;
    }

    private static void viewAvailableDrivers(Scanner scanner, RideSharingService.Graph graph,
            DriverManager driverManager) {
        System.out.print("Enter your location: ");
        String userLocation = scanner.next(); // Assuming user inputs single character location like 'Node A'
        Driver closestDriver = driverManager.findClosestAvailableDriver(userLocation, graph);
        if (closestDriver != null) {
            System.out.println("Available drivers:");
            System.out.println(
                    "Driver: " + closestDriver.getName() + ", Current Location: " + closestDriver.getCurrentLocation());
        } else {
            System.out.println("No available drivers nearby.");
        }
    }
}
