import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainFrame extends JFrame {

    private static final Color BG = new Color(20, 20, 20);
    private static final Color PANEL_BG = new Color(30, 30, 30);
    private static final Color RED = new Color(180, 20, 20);
    private static final Color DARK_RED = new Color(120, 0, 0);
    private static final Color TEXT = Color.WHITE;

    private Network network;
    private Network backupNetwork;
    private GraphPanel graphPanel;

    public MainFrame() {
        setTitle("Graph Network");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        JPanel buttonPanel = new JPanel();

        JButton loadButton = new JButton("Start");
        JButton reloadButton = new JButton("Reload");
        JButton addStationButton = new JButton("Add Station");
        JButton addRailwayButton = new JButton("Add Railway");
        JButton deleteStationButton = new JButton("Delete Station");
        JButton deleteRailwayButton = new JButton("Delete Railway");
        JButton clearButton = new JButton("Clear");
        JButton shortestPathButton = new JButton("Shortest Path");
        JButton cycleButton = new JButton("Detect Cycle");
        JButton sortButton = new JButton("Sort Stations");



        buttonPanel.add(loadButton);
        buttonPanel.add(reloadButton);
        buttonPanel.add(addStationButton);
        buttonPanel.add(addRailwayButton);
        buttonPanel.add(deleteStationButton);
        buttonPanel.add(deleteRailwayButton);
        buttonPanel.add(cycleButton);
        buttonPanel.add(shortestPathButton);
        buttonPanel.add(sortButton);
        buttonPanel.add(clearButton);


        add(buttonPanel, BorderLayout.NORTH);

        graphPanel = new GraphPanel();
        add(graphPanel, BorderLayout.CENTER);

        // LOAD from file
        loadButton.addActionListener(e -> {
            try {
                HashMap<String, ArrayList<Pair<String, Integer>>> data = W_RFiles.importFromFile();
                network = new Network(data);
                backupNetwork = new Network(data); // keep a copy
                graphPanel.setNetwork(network);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage());
            }
        });

        // RELOAD last updated state
        reloadButton.addActionListener(e -> {
            if (backupNetwork == null) {
                JOptionPane.showMessageDialog(this, "No saved graph state to reload.");
                return;
            }
            network = new Network(backupNetwork.toExport());
            graphPanel.setNetwork(network);
        });

        // ADD STATION
        addStationButton.addActionListener(e -> {
            if (network == null) {
                JOptionPane.showMessageDialog(this, "Load a network first.");
                return;
            }

            String name = JOptionPane.showInputDialog(this, "Enter station name:");
            if (name == null || name.trim().isEmpty()) return;

            network.addStation(name.trim());
            saveCurrentState();
        });

        // ADD RAILWAY
        addRailwayButton.addActionListener(e -> {
            if (network == null) {
                JOptionPane.showMessageDialog(this, "Load a network first.");
                return;
            }

            String from = JOptionPane.showInputDialog(this, "From station:");
            if (from == null || from.trim().isEmpty()) return;

            String to = JOptionPane.showInputDialog(this, "To station:");
            if (to == null || to.trim().isEmpty()) return;

            String distanceStr = JOptionPane.showInputDialog(this, "Distance:");
            if (distanceStr == null || distanceStr.trim().isEmpty()) return;

            try {
                int distance = Integer.parseInt(distanceStr.trim());
                network.addRailWay(from.trim(), to.trim(), distance);
                saveCurrentState();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Distance must be a number.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        cycleButton.addActionListener(e -> {
            if (network == null) {
                JOptionPane.showMessageDialog(this, "Load a network first.");
                return;
            }

            boolean hasCycle = network.hasCycle();
            JOptionPane.showMessageDialog(this,
                    hasCycle ? "The graph has a cycle." : "The graph has no cycle.");
        });

        sortButton.addActionListener(e -> {
            if (network == null) {
                JOptionPane.showMessageDialog(this, "Load a network first.");
                return;
            }

            ArrayList<Pair<String, Integer>> sorted = network.returnSorted();

            StringBuilder result = new StringBuilder("Stations sorted by number of railways:\n\n");
            for (Pair<String, Integer> p : sorted) {
                result.append(p.first)
                        .append(" -> ")
                        .append(p.second)
                        .append("\n");
            }

            JOptionPane.showMessageDialog(this, result.toString());
        });

        // DELETE STATION
        deleteStationButton.addActionListener(e -> {
            if (network == null) {
                JOptionPane.showMessageDialog(this, "Load a network first.");
                return;
            }

            String name = JOptionPane.showInputDialog(this, "Enter station name to delete:");
            if (name == null || name.trim().isEmpty()) return;

            boolean removed = network.removeStation(name.trim());
            if (!removed) {
                JOptionPane.showMessageDialog(this, "Station not found.");
                return;
            }

            saveCurrentState();
        });

        // DELETE RAILWAY
        deleteRailwayButton.addActionListener(e -> {
            if (network == null) {
                JOptionPane.showMessageDialog(this, "Load a network first.");
                return;
            }

            String from = JOptionPane.showInputDialog(this, "From station:");
            if (from == null || from.trim().isEmpty()) return;

            String to = JOptionPane.showInputDialog(this, "To station:");
            if (to == null || to.trim().isEmpty()) return;

            boolean removed = network.removeRailWay(from.trim(), to.trim());
            if (!removed) {
                JOptionPane.showMessageDialog(this, "Could not delete railway.");
                return;
            }

            saveCurrentState();
        });

        // CLEAR only the drawing
        clearButton.addActionListener(e -> {
            graphPanel.setNetwork(null);
        });

        setVisible(true);
        shortestPathButton.addActionListener(e -> {
            if (network == null) {
                JOptionPane.showMessageDialog(this, "Load a network first.");
                return;
            }

            String from = JOptionPane.showInputDialog(this, "Enter source station:");
            if (from == null || from.trim().isEmpty()) return;

            String to = JOptionPane.showInputDialog(this, "Enter destination station:");
            if (to == null || to.trim().isEmpty()) return;

            ArrayList<String> path = network.getShortestPath(from.trim(), to.trim());
            int distance = network.getShortestDistance(from.trim(), to.trim());

            if (path.isEmpty() || distance == -1) {
                JOptionPane.showMessageDialog(this, "No path found between these stations.");
                return;
            }

            StringBuilder result = new StringBuilder();
            result.append("Shortest path:\n");
            result.append(String.join(" -> ", path));
            result.append("\n\nDistance: ").append(distance);

            JOptionPane.showMessageDialog(this, result.toString());
        });
    }


    private void saveCurrentState() {
        try {
            network.exportNetworkToFile(); // write to toWriteNetwork.txt
            backupNetwork = new Network(network.toExport()); // keep latest saved copy
            graphPanel.setNetwork(network);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving network: " + ex.getMessage());
        }
    }
}