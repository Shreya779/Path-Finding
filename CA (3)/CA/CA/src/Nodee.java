import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.TimeUnit;

public class Nodee extends JPanel {
    private int rows = 30;
    private int cols = 50;
    private int cellSize = 20;
    private Node[][] grid;
    private Node startNode;
    private Node endNode;

    private final Color START_COLOR = Color.GREEN;
    private final Color END_COLOR = Color.RED;
    private final Color WALL_COLOR = Color.BLACK;
    private final Color VISITED_COLOR = new Color(100, 149, 237);
    private final Color PATH_COLOR = new Color(34, 139, 34);

    private JComboBox<String> algorithmCombo;
    private JComboBox<String> mazeCombo;
    private JButton runButton, resetButton, clearPathButton;
    private JButton randomMazeButton, recursiveDivButton;
    private JButton increaseSizeButton, decreaseSizeButton;
    private JLabel statsLabel;
    private JLabel timeLabel;
    private JLabel nodesLabel;

    public Nodee() {
        initializeGrid();
        setupUI();
        setupEventHandlers();
    }

    private void initializeGrid() {
        grid = new Node[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = new Node(i, j);
            }
        }
        startNode = null;
        endNode = null;
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel(new FlowLayout());

        algorithmCombo = new JComboBox<>(new String[]{
                "A* (Manhattan)", "A* (Euclidean)", "BFS", "Dijkstra's", "DFS", "Greedy BFS"
        });

        mazeCombo = new JComboBox<>(new String[]{
                "Random Maze", "Recursive Division",
                "Recursive Division (Horizontal)", "Recursive Division (Vertical)", "Simple Maze"
        });

        runButton = new JButton("Run Algorithm");
        resetButton = new JButton("Reset All");
        clearPathButton = new JButton("Clear Path");
        randomMazeButton = new JButton("Random Maze");
        recursiveDivButton = new JButton("Recursive Division");
        increaseSizeButton = new JButton("+");
        decreaseSizeButton = new JButton("-");
        statsLabel = new JLabel(" ");
        timeLabel = new JLabel("Time: -");
        nodesLabel = new JLabel("Nodes: -");

        controlPanel.add(new JLabel("Algorithm:"));
        controlPanel.add(algorithmCombo);
        controlPanel.add(runButton);
        controlPanel.add(clearPathButton);
        controlPanel.add(resetButton);
        controlPanel.add(new JLabel("Maze:"));
        controlPanel.add(mazeCombo);
        controlPanel.add(new JLabel("Cell Size:"));
        controlPanel.add(decreaseSizeButton);
        controlPanel.add(increaseSizeButton);
        controlPanel.add(timeLabel);
        controlPanel.add(nodesLabel);
        controlPanel.add(statsLabel);

        add(controlPanel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGrid(g);
            }

            public Dimension getPreferredSize() {
                return new Dimension(cols * cellSize, rows * cellSize);
            }
        };

        gridPanel.setBackground(Color.WHITE);
        gridPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                handleGridClick(evt);
            }
        });

        add(gridPanel, BorderLayout.CENTER);

        JPanel legendPanel = new JPanel(new GridLayout(1, 5));
        addLegendItem(legendPanel, START_COLOR, "Start");
        addLegendItem(legendPanel, END_COLOR, "End");
        addLegendItem(legendPanel, WALL_COLOR, "Wall");
        addLegendItem(legendPanel, VISITED_COLOR, "Visited");
        addLegendItem(legendPanel, PATH_COLOR, "Path");
        add(legendPanel, BorderLayout.SOUTH);
    }

    private void addLegendItem(JPanel panel, Color color, String text) {
        JLabel label = new JLabel(text);
        label.setOpaque(true);
        label.setBackground(color);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.add(label);
    }

    private void setupEventHandlers() {
        runButton.addActionListener(this::runAlgorithm);
        resetButton.addActionListener(e -> resetAll());
        clearPathButton.addActionListener(e -> clearPath());
        increaseSizeButton.addActionListener(e -> adjustCellSize(2));
        decreaseSizeButton.addActionListener(e -> adjustCellSize(-2));

        mazeCombo.addActionListener(e -> {
            String selected = (String) mazeCombo.getSelectedItem();
            switch (selected) {
                case "Random Maze" -> generateRandomMaze();
                case "Recursive Division" -> generateRecursiveDivisionMaze();
                case "Recursive Division (Horizontal)" -> generateRecursiveDivisionVerticalMaze();
                case "Recursive Division (Vertical)" -> generateRecursiveDivisionHorizontalMaze();
                case "Simple Maze" -> generateSimpleMaze();
            }
        });
    }

    private void runAlgorithm(ActionEvent e) {
        if (startNode == null || endNode == null) {
            statsLabel.setText("Please set both start and end nodes");
            return;
        }

        String selectedAlgo = (String) algorithmCombo.getSelectedItem();
        new Thread(() -> {
            long startTime = System.nanoTime();
            int nodesExplored = 0;

            switch (selectedAlgo) {
                case "A* (Manhattan)" -> nodesExplored = Algorithms.aStar(grid, startNode, endNode, false);
                case "A* (Euclidean)" -> nodesExplored = Algorithms.aStar(grid, startNode, endNode, true);
                case "BFS" -> nodesExplored = Algorithms.bfs(grid, startNode, endNode);
                case "Dijkstra's" -> nodesExplored = Algorithms.dijkstra(grid, startNode, endNode);
                case "DFS" -> nodesExplored = Algorithms.dfs(grid, startNode, endNode);
                case "Greedy BFS" -> nodesExplored = Algorithms.greedyBFS(grid, startNode, endNode);
            }

            long duration = System.nanoTime() - startTime;
            double seconds = duration / 1_000_000_000.0;
            timeLabel.setText(String.format("Time: %.3f s", seconds));
            nodesLabel.setText(String.format("Nodes: %d", nodesExplored));
            statsLabel.setText(String.format("%s completed", selectedAlgo));
            repaint();
        }).start();
    }

    private void drawGrid(Graphics g) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Node node = grid[i][j];
                Color color = Color.WHITE;

                if (node.isStart) color = START_COLOR;
                else if (node.isEnd) color = END_COLOR;
                else if (node.isWall) color = WALL_COLOR;
                else if (node.isPath) color = PATH_COLOR;
                else if (node.isVisited) color = VISITED_COLOR;

                g.setColor(color);
                g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                g.setColor(Color.LIGHT_GRAY);
                g.drawRect(j * cellSize, i * cellSize, cellSize, cellSize);
            }
        }
    }

    private void handleGridClick(java.awt.event.MouseEvent evt) {
        int col = evt.getX() / cellSize;
        int row = evt.getY() / cellSize;

        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            Node clickedNode = grid[row][col];
            if (SwingUtilities.isLeftMouseButton(evt)) {
                if (startNode == null) {
                    startNode = clickedNode;
                    clickedNode.isStart = true;
                } else if (endNode == null && !clickedNode.isStart) {
                    endNode = clickedNode;
                    clickedNode.isEnd = true;
                } else if (!clickedNode.isStart && !clickedNode.isEnd) {
                    clickedNode.isWall = !clickedNode.isWall;
                }
            }
            repaint();
        }
    }

    private void resetAll() {
        for (Node[] row : grid) {
            for (Node node : row) {
                node.reset();
            }
        }
        startNode = null;
        endNode = null;
        statsLabel.setText(" ");
        timeLabel.setText("Time: -");
        nodesLabel.setText("Nodes: -");
        repaint();
    }

    private void clearPath() {
        for (Node[] row : grid) {
            for (Node node : row) {
                if (!node.isStart && !node.isEnd && !node.isWall) {
                    node.isVisited = false;
                    node.isPath = false;
                }
            }
        }
        repaint();
    }

    private void adjustCellSize(int delta) {
        cellSize = Math.max(10, Math.min(50, cellSize + delta));
        revalidate();
        repaint();
    }

    private void generateRandomMaze() {
        new Thread(() -> {
            clearWalls();
            int wallCount = (int) (rows * cols * 0.25);
            for (int i = 0; i < wallCount; i++) {
                int r = (int) (Math.random() * rows);
                int c = (int) (Math.random() * cols);
                if (grid[r][c] != startNode && grid[r][c] != endNode) {
                    grid[r][c].isWall = true;
                }
                repaintSleep(10);
            }
        }).start();
    }

    private void generateRecursiveDivisionMaze() {
        new Thread(() -> {
            clearWalls();
            addBorders();
            recursiveDivisionEnhanced(0, rows - 1, 0, cols - 1);
        }).start();
    }

    private void recursiveDivisionEnhanced(int minRow, int maxRow, int minCol, int maxCol) {
        if (maxRow - minRow <= 5 || maxCol - minCol <= 5) return;

        boolean horizontal = (maxRow - minRow) >= (maxCol - minCol);

        if (horizontal) {
            int randRow = minRow + 2 + (int) (Math.random() * ((maxRow - 2) - (minRow + 2)));
            for (int i = minCol + 1; i < maxCol; i++) {
                grid[randRow][i].isWall = true;
                repaintSleep(1);
            }

            for (int i = 0; i < 3; i++) {
                int gap = minCol + 1 + (int) (Math.random() * (maxCol - minCol - 2));
                grid[randRow][gap].isWall = false;
            }

            recursiveDivisionEnhanced(minRow, randRow, minCol, maxCol);
            recursiveDivisionEnhanced(randRow, maxRow, minCol, maxCol);
        } else {
            int randCol = minCol + 2 + (int) (Math.random() * ((maxCol - 2) - (minCol + 2)));
            for (int i = minRow + 1; i < maxRow; i++) {
                grid[i][randCol].isWall = true;
                repaintSleep(1);
            }

            for (int i = 0; i < 3; i++) {
                int gap = minRow + 1 + (int) (Math.random() * (maxRow - minRow - 2));
                grid[gap][randCol].isWall = false;
            }

            recursiveDivisionEnhanced(minRow, maxRow, minCol, randCol);
            recursiveDivisionEnhanced(minRow, maxRow, randCol, maxCol);
        }
    }

    private void generateRecursiveDivisionVerticalMaze() {
        new Thread(() -> {
            clearWalls();
            addBorders();
            recursiveDivVertical(0, rows - 1);
        }).start();
    }

    private void recursiveDivVertical(int top, int bottom) {
        if (Math.abs(top - bottom) <= 3) return;

        int mid = (top + bottom) / 2;
        for (int col = 0; col < cols; col++) {
            grid[mid][col].isWall = true;
            repaintSleep(1);
        }
        for (int i = 0; i < 4; i++) {
            int randCol = (int) (Math.random() * cols);
            grid[mid][randCol].isWall = false;
        }
        recursiveDivVertical(top, mid - 1);
        recursiveDivVertical(mid + 1, bottom);
    }

    private void generateRecursiveDivisionHorizontalMaze() {
        new Thread(() -> {
            clearWalls();
            addBorders();
            recursiveDivHorizontal(0, cols - 1);
        }).start();
    }

    private void recursiveDivHorizontal(int left, int right) {
        if (Math.abs(left - right) <= 3) return;

        int mid = (left + right) / 2;
        for (int row = 0; row < rows; row++) {
            grid[row][mid].isWall = true;
            repaintSleep(1);
        }
        for (int i = 0; i < 4; i++) {
            int randRow = (int) (Math.random() * rows);
            grid[randRow][mid].isWall = false;
        }
        recursiveDivHorizontal(left, mid - 1);
        recursiveDivHorizontal(mid + 1, right);
    }

    private void generateSimpleMaze() {
        new Thread(() -> {
            clearWalls();
            for (int i = 0; i < 200; i++) {
                int r = (int) (Math.random() * rows);
                int c = (int) (Math.random() * cols);
                if (grid[r][c] != startNode && grid[r][c] != endNode) {
                    grid[r][c].isWall = true;
                }
                repaintSleep(11);
            }
        }).start();
    }

    private void repaintSleep(int millis) {
        repaint();
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void clearWalls() {
        for (Node[] row : grid) {
            for (Node node : row) {
                if (!node.isStart && !node.isEnd) {
                    node.isWall = false;
                }
            }
        }
    }

    private void addBorders() {
        for (int i = 0; i < rows; i++) {
            if (!grid[i][0].isStart && !grid[i][0].isEnd) grid[i][0].isWall = true;
            if (!grid[i][cols - 1].isStart && !grid[i][cols - 1].isEnd) grid[i][cols - 1].isWall = true;
        }
        for (int j = 0; j < cols; j++) {
            if (!grid[0][j].isStart && !grid[0][j].isEnd) grid[0][j].isWall = true;
            if (!grid[rows - 1][j].isStart && !grid[rows - 1][j].isEnd) grid[rows - 1][j].isWall = true;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pathfinding Visualizer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new Nodee());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}