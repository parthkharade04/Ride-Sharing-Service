import java.util.*;
import java.util.stream.Collectors;

class Driver {
    private String name;
    private boolean isAvailable;
    private String currentLocation;

    public Driver(String name, boolean isAvailable, String currentLocation) {
        this.name = name;
        this.isAvailable = isAvailable;
        this.currentLocation = currentLocation;
    }

    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }
}

class DriverManager {
    private List<Driver> drivers;

    public DriverManager() {
        this.drivers = new ArrayList<>();
    }

    public void addDriver(Driver driver) {
        drivers.add(driver);
    }

    public boolean isAnyDriverAvailable() {
        for (Driver driver : drivers) {
            if (driver.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    public Driver findClosestAvailableDriver(String userLocation, RideSharingService.Graph graph) {
        Driver closestDriver = null;
        int minDistance = Integer.MAX_VALUE;
        List<Character> shortestPath = null;
        double time = 0;

        char source = userLocation.charAt(0);

        List<Driver> availableDrivers = drivers.stream()
                .filter(Driver::isAvailable)
                .collect(Collectors.toList());

        for (Driver driver : availableDrivers) {
            String driverLocation = driver.getCurrentLocation();
            char destination = driverLocation.charAt(0);
            RideSharingService.DijkstraResult result = RideSharingService.dijkstra(graph, source, destination);
            if (result.getDistance() != -1 && result.getDistance() < minDistance) {
                minDistance = result.getDistance();
                time = minDistance * 2.5;
                closestDriver = driver;
                shortestPath = result.getShortestPath();
            }
        }

        if (shortestPath != null) {
            System.out.println("Shortest path to driver's location: " + shortestPath);
            System.out.println("Estimated time to reach: " + time + " minutes");
        }

        return closestDriver;
    }
}

public class Driveravailable {
    public static void main(String[] args) {
        DriverManager driverManager = new DriverManager();

        driverManager.addDriver(new Driver("John", true, "B"));
        driverManager.addDriver(new Driver("Alice", true, "C"));
        driverManager.addDriver(new Driver("Bob", true, "B"));
        driverManager.addDriver(new Driver("Chotu", true, "D"));

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter your location: ");
            String userLocation = scanner.next();

            RideSharingService.Graph graph = RideSharingService.createGraph();

            Driver closestDriver = driverManager.findClosestAvailableDriver(userLocation, graph);

            if (closestDriver != null) {
                System.out.println("Booking driver: " + closestDriver.getName());
                closestDriver.setAvailable(false);
            } else {
                System.out.println("No available drivers nearby.");
            }

            System.out.print("Do you want to book another driver? (yes/no): ");
            String choice = scanner.next();
            if (!choice.equalsIgnoreCase("yes")) {
                break;
            }
        }

        scanner.close();
    }
}
