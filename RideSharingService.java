import java.util.*;

public class RideSharingService {
    public static class Edge {
        char destination;
        int weight;

        public Edge(char destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }

    public static class Graph {
        private Map<Character, List<Edge>> adjacencyList;

        public Graph() {
            adjacencyList = new HashMap<>();
        }

        public void addEdge(char source, char destination, int weight) {
            adjacencyList.computeIfAbsent(source, k -> new ArrayList<>()).add(new Edge(destination, weight));
            adjacencyList.computeIfAbsent(destination, k -> new ArrayList<>()).add(new Edge(source, weight));
        }

        public Map<Character, List<Edge>> getAdjacencyList() {
            return adjacencyList;
        }

    }

    public static Graph createGraph() {
        Graph graph = new Graph();
        graph.addEdge('A', 'B', 2);
        graph.addEdge('A', 'C', 5);
        graph.addEdge('B', 'C', 6);
        graph.addEdge('B', 'D', 1);
        graph.addEdge('B', 'E', 3);
        graph.addEdge('C', 'D', 2);
        graph.addEdge('D', 'E', 4);
        graph.addEdge('E', 'F', 7);
        return graph;
    }

    public static class DijkstraResult {
        private int distance;
        private List<Character> shortestPath;

        public DijkstraResult(int distance, List<Character> shortestPath) {
            this.distance = distance;
            this.shortestPath = shortestPath;
        }

        public int getDistance() {
            return distance;
        }

        public List<Character> getShortestPath() {
            return shortestPath;
        }
    }

    public static DijkstraResult dijkstra(Graph graph, char source, char destination) {
        Map<Character, List<Edge>> adjacencyList = graph.getAdjacencyList();
        Map<Character, Integer> distance = new HashMap<>();
        Map<Character, Character> previous = new HashMap<>();
        PriorityQueue<Character> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(distance::get));

        // Initialize distances
        for (char vertex : adjacencyList.keySet()) {
            distance.put(vertex, Integer.MAX_VALUE);
            previous.put(vertex, null);
            priorityQueue.add(vertex);
        }
        distance.put(source, 0); // Set distance of source to 0

        while (!priorityQueue.isEmpty()) {
            char u = priorityQueue.poll();
            int distU = distance.get(u);

            if (distU == Integer.MAX_VALUE) {
                // No need to break here, just continue to next node
                continue;
            }

            if (u == destination) {
                // Found the destination, reconstruct the shortest path
                List<Character> shortestPath = new ArrayList<>();
                while (previous.get(u) != null) {
                    shortestPath.add(0, u);
                    u = previous.get(u);
                }
                return new DijkstraResult(distU, shortestPath);
            }

            for (Edge neighbor : adjacencyList.getOrDefault(u, new ArrayList<>())) {
                char v = neighbor.destination;
                int weight = neighbor.weight;
                int alt = distU + weight;
                if (alt < distance.get(v)) {
                    distance.put(v, alt);
                    previous.put(v, u);
                    priorityQueue.add(v);
                }
            }
        }

        // Destination is unreachable
        return new DijkstraResult(-1, null);
    }

    private static int calculateFare(int totalDistance) {
        int Fare = totalDistance * 10;
        return Fare;
    }

    public static void main(String[] args) {
        Graph graph = createGraph(); // Create the graph
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter source node: ");
        char source = scanner.next().charAt(0);
        System.out.print("Enter destination node: ");
        char destination = scanner.next().charAt(0);

        DijkstraResult result = dijkstra(graph, source, destination);

        // Test output
        if (result.getDistance() == -1) {
            System.out.println("Destination node is unreachable.");
        } else {
            List<Character> shortestPath = result.getShortestPath();
            System.out.print("Shortest path from node " + source + " to node " + destination + ": ");
            for (int i = 0; i < shortestPath.size(); i++) {
                System.out.print(shortestPath.get(i));
                if (i < shortestPath.size() - 1) {
                    System.out.print(" -> ");
                }
            }
            System.out.println();
            int totalDistance = result.getDistance();
            int fare = calculateFare(totalDistance);
            System.out.println("Fare: $" + fare);
        }
        scanner.close();
    }
}
