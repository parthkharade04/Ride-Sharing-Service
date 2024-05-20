import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RideSharingAppGUI extends JFrame {

    private RideSharingService.Graph graph;
    private DriverManager driverManager;
    private JTextArea outputTextArea;

    public RideSharingAppGUI() {
        super("Ride Sharing Service");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to the Ride Sharing Service!");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1));
        JButton bookRideButton = new JButton("Book a Ride");
        JButton viewDriversButton = new JButton("View Available Drivers");
        JButton exitButton = new JButton("Exit");
        buttonPanel.add(bookRideButton);
        buttonPanel.add(viewDriversButton);
        buttonPanel.add(exitButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        graph = RideSharingService.createGraph();
        driverManager = new DriverManager();
        initializeDrivers();

        bookRideButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userLocation = JOptionPane.showInputDialog("Enter your location:");
                String destination = JOptionPane.showInputDialog("Enter destination location:");
                char source = userLocation.charAt(0);
                char dest = destination.charAt(0);

                RideSharingService.DijkstraResult result = RideSharingService.dijkstra(graph, source, dest);

                if (result.getDistance() == -1) {
                    outputTextArea.setText("Destination node is unreachable.");
                } else {
                    outputTextArea.setText("Minimum distance from node " + source + " to node " + dest + ": "
                            + result.getDistance() + "\n");
                    outputTextArea.append("Shortest path: " + result.getShortestPath() + "\n");

                    int fare = calculateFare(result.getShortestPath());
                    outputTextArea.append("Estimated fare: $" + fare + "\n");

                    Driver closestDriver = driverManager.findClosestAvailableDriver(userLocation, graph);
                    if (closestDriver != null) {
                        outputTextArea.append("Booking driver: " + closestDriver.getName() + "\n");
                        closestDriver.setAvailable(false);
                    } else {
                        outputTextArea.append("No available drivers nearby.");
                    }
                }
            }
        });

        viewDriversButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userLocation = JOptionPane.showInputDialog("Enter your location:");
                Driver closestDriver = driverManager.findClosestAvailableDriver(userLocation, graph);
                if (closestDriver != null) {
                    outputTextArea.setText("Available drivers:\n");
                    outputTextArea.append("Driver: " + closestDriver.getName() + ", Current Location: "
                            + closestDriver.getCurrentLocation() + "\n");

                    RideSharingService.DijkstraResult result = RideSharingService.dijkstra(graph,
                            userLocation.charAt(0), closestDriver.getCurrentLocation().charAt(0));
                    if (result.getDistance() != -1) {
                        outputTextArea.append("Shortest path to driver's location: " + result.getShortestPath() + "\n");
                        outputTextArea.append("Estimated time to reach: " + result.getDistance() * 2.5 + " minutes\n");
                    } else {
                        outputTextArea.append("Driver's location is unreachable.");
                    }
                } else {
                    outputTextArea.setText("No available drivers nearby.");
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void initializeDrivers() {
        driverManager.addDriver(new Driver("John", true, "B"));
        driverManager.addDriver(new Driver("Alice", true, "C"));
        driverManager.addDriver(new Driver("Bob", true, "B"));
        driverManager.addDriver(new Driver("Chotu", true, "D"));
    }

    private int calculateFare(List<Character> shortestPath) {
        return shortestPath.size() * 15; // Simple fare calculation: $15 per unit distance
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RideSharingAppGUI().setVisible(true);
            }
        });
    }
}
