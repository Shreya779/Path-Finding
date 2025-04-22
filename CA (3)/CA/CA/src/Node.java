import java.util.ArrayList;
import java.util.List;

public class Node {
    public int row, col;
    public boolean isStart = false;
    public boolean isEnd = false;
    public boolean isWall = false;
    public boolean isVisited = false;
    public boolean isPath = false;
    public double distance = Double.POSITIVE_INFINITY;
    public double gScore = Double.POSITIVE_INFINITY;
    public double fScore = Double.POSITIVE_INFINITY;
    public List<Node> neighbors = new ArrayList<>();

    public Node(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void reset() {
        isStart = false;
        isEnd = false;
        isWall = false;
        isVisited = false;
        isPath = false;
        distance = Double.POSITIVE_INFINITY;
        gScore = Double.POSITIVE_INFINITY;
        fScore = Double.POSITIVE_INFINITY;
        neighbors.clear();
    }

    public void updateNeighbors(Node[][] grid) {
        neighbors.clear();
        if (!isWall) {
            // Up
            if (row > 0 && !grid[row-1][col].isWall) neighbors.add(grid[row-1][col]);
            // Down
            if (row < grid.length-1 && !grid[row+1][col].isWall) neighbors.add(grid[row+1][col]);
            // Left
            if (col > 0 && !grid[row][col-1].isWall) neighbors.add(grid[row][col-1]);
            // Right
            if (col < grid[0].length-1 && !grid[row][col+1].isWall) neighbors.add(grid[row][col+1]);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return row == node.row && col == node.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }
}