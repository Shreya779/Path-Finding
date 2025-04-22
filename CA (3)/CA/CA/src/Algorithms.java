import java.util.*;
import java.util.concurrent.TimeUnit;

public class Algorithms {

    public static int aStar(Node[][] grid, Node startNode, Node endNode, boolean euclidean) {
        clearPath(grid);
        updateAllNeighbors(grid);

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(a -> a.fScore));
        Set<Node> closedSet = new HashSet<>();
        Map<Node, Node> cameFrom = new HashMap<>();

        for (Node[] row : grid) {
            for (Node node : row) {
                node.gScore = Double.POSITIVE_INFINITY;
                node.fScore = Double.POSITIVE_INFINITY;
            }
        }

        startNode.gScore = 0;
        startNode.fScore = heuristic(startNode, endNode, euclidean);
        openSet.add(startNode);

        int nodesExplored = 0;

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            nodesExplored++;

            if (current == endNode) {
                reconstructPath(cameFrom, current, grid);
                return nodesExplored;
            }

            closedSet.add(current);
            current.isVisited = true;
            repaintGrid(grid);
            sleep(5);

            for (Node neighbor : current.neighbors) {
                if (closedSet.contains(neighbor)) continue;

                double tentativeGScore = current.gScore + 1;

                if (tentativeGScore < neighbor.gScore) {
                    cameFrom.put(neighbor, current);
                    neighbor.gScore = tentativeGScore;
                    neighbor.fScore = neighbor.gScore + heuristic(neighbor, endNode, euclidean);

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return nodesExplored;
    }

    public static int bfs(Node[][] grid, Node startNode, Node endNode) {
        clearPath(grid);
        updateAllNeighbors(grid);

        Queue<Node> queue = new LinkedList<>();
        Map<Node, Node> cameFrom = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        int nodesExplored = 0;

        queue.add(startNode);
        visited.add(startNode);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            nodesExplored++;

            if (current == endNode) {
                reconstructPath(cameFrom, current, grid);
                return nodesExplored;
            }

            current.isVisited = true;
            repaintGrid(grid);
            sleep(5);

            for (Node neighbor : current.neighbors) {
                if (!visited.contains(neighbor)) {
                    cameFrom.put(neighbor, current);
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return nodesExplored;
    }

    public static int dijkstra(Node[][] grid, Node startNode, Node endNode) {
        clearPath(grid);
        updateAllNeighbors(grid);

        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(a -> a.distance));
        Map<Node, Node> cameFrom = new HashMap<>();
        int nodesExplored = 0;

        for (Node[] row : grid) {
            for (Node node : row) {
                node.distance = Double.POSITIVE_INFINITY;
            }
        }

        startNode.distance = 0;
        queue.add(startNode);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            nodesExplored++;

            if (current.isVisited) continue;
            current.isVisited = true;

            if (current == endNode) {
                reconstructPath(cameFrom, current, grid);
                return nodesExplored;
            }

            repaintGrid(grid);
            sleep(5);

            for (Node neighbor : current.neighbors) {
                if (!neighbor.isVisited) {
                    double newDist = current.distance + 1;
                    if (newDist < neighbor.distance) {
                        neighbor.distance = newDist;
                        cameFrom.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }
        }

        return nodesExplored;
    }

    public static int dfs(Node[][] grid, Node startNode, Node endNode) {
        clearPath(grid);
        updateAllNeighbors(grid);

        Stack<Node> stack = new Stack<>();
        Map<Node, Node> cameFrom = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        int nodesExplored = 0;

        stack.push(startNode);
        visited.add(startNode);

        while (!stack.isEmpty()) {
            Node current = stack.pop();
            nodesExplored++;

            if (current == endNode) {
                reconstructPath(cameFrom, current, grid);
                return nodesExplored;
            }

            current.isVisited = true;
            repaintGrid(grid);
            sleep(5);

            for (Node neighbor : current.neighbors) {
                if (!visited.contains(neighbor)) {
                    cameFrom.put(neighbor, current);
                    visited.add(neighbor);
                    stack.push(neighbor);
                }
            }
        }

        return nodesExplored;
    }

    public static int greedyBFS(Node[][] grid, Node startNode, Node endNode) {
        clearPath(grid);
        updateAllNeighbors(grid);

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(a -> a.fScore));
        Set<Node> closedSet = new HashSet<>();
        Map<Node, Node> cameFrom = new HashMap<>();
        int nodesExplored = 0;

        startNode.fScore = heuristic(startNode, endNode, false);
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            nodesExplored++;

            if (current == endNode) {
                reconstructPath(cameFrom, current, grid);
                return nodesExplored;
            }

            closedSet.add(current);
            current.isVisited = true;
            repaintGrid(grid);
            sleep(5);

            for (Node neighbor : current.neighbors) {
                if (!closedSet.contains(neighbor)) {
                    cameFrom.put(neighbor, current);
                    neighbor.fScore = heuristic(neighbor, endNode, false);
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return nodesExplored;
    }

    private static double heuristic(Node a, Node b, boolean euclidean) {
        if (euclidean) {
            return Math.sqrt(Math.pow(a.row - b.row, 2) + Math.pow(a.col - b.col, 2));
        } else {
            return Math.abs(a.row - b.row) + Math.abs(a.col - b.col);
        }
    }

    private static void reconstructPath(Map<Node, Node> cameFrom, Node current, Node[][] grid) {
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            if (!current.isStart && !current.isEnd) {
                current.isPath = true;
            }
            repaintGrid(grid);
            sleep(20);
        }
    }

    private static void clearPath(Node[][] grid) {
        for (Node[] row : grid) {
            for (Node node : row) {
                if (!node.isStart && !node.isEnd && !node.isWall) {
                    node.isVisited = false;
                    node.isPath = false;
                }
            }
        }
    }

    private static void updateAllNeighbors(Node[][] grid) {
        for (Node[] row : grid) {
            for (Node node : row) {
                node.updateNeighbors(grid);
            }
        }
    }

    private static void repaintGrid(Node[][] grid) {
        // This would need to be implemented to trigger a repaint in the UI
        // In a real implementation, you would call a method on your main panel to repaint
    }

    private static void sleep(int millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}