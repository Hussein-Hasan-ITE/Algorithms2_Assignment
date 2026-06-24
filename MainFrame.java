import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainFrame extends JFrame {

    private static final Color BG = new Color(20, 20, 20);
    private static final Color PANEL_BG = new Color(30, 30, 30);
    private static final Color BLUE = new Color(30, 90, 200);
    private static final Color DARK_BLUE = new Color(10, 40, 120);
    private static final Color TEXT = Color.WHITE;

    private Network network;
    private Network backupNetwork;
    private GraphPanel graphPanel;

    private JTextArea infoArea;
    private JComboBox<String> currentStationCombo;
    private JComboBox<String> destinationCombo;

    public MainFrame() {
        setTitle("Graph Simulation");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon("\\C:\\Users\\LOQ\\Downloads\\Telegram Desktop\\photo_2026-06-21_11-31-46.jpg\\").getImage());
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(PANEL_BG);

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

        styleButton(loadButton);
        styleButton(reloadButton);
        styleButton(addStationButton);
        styleButton(addRailwayButton);
        styleButton(deleteStationButton);
        styleButton(deleteRailwayButton);
        styleButton(clearButton);
        styleButton(shortestPathButton);
        styleButton(cycleButton);
        styleButton(sortButton);

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

        infoArea = new JTextArea(4, 30);
        infoArea.setEditable(false);
        infoArea.setBackground(Color.BLACK);
        infoArea.setForeground(TEXT);

        JScrollPane infoScroll = new JScrollPane(infoArea);

        JPanel traversalPanel = new JPanel(new GridBagLayout());
        traversalPanel.setBackground(PANEL_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel currentLabel = new JLabel("the station you're in");
        JLabel destinationLabel = new JLabel("available destinations");
        currentLabel.setForeground(TEXT);
        destinationLabel.setForeground(TEXT);

        currentStationCombo = new JComboBox<>();
        destinationCombo = new JComboBox<>();

        currentStationCombo.setPreferredSize(new Dimension(220, 28));
        destinationCombo.setPreferredSize(new Dimension(220, 28));

        currentStationCombo.setBackground(Color.BLACK);
        currentStationCombo.setForeground(TEXT);
        destinationCombo.setBackground(Color.BLACK);
        destinationCombo.setForeground(TEXT);

        JButton traverseButton = new JButton("Traverse");
        styleButton(traverseButton);

        gbc.gridx = 0; gbc.gridy = 0;
        traversalPanel.add(currentLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        traversalPanel.add(destinationLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        traversalPanel.add(currentStationCombo, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        traversalPanel.add(destinationCombo, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        traversalPanel.add(traverseButton, gbc);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(PANEL_BG);
        bottomPanel.add(traversalPanel, BorderLayout.NORTH);
        bottomPanel.add(infoScroll, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        currentStationCombo.addActionListener(e -> updateDestinationCombo());

        loadButton.addActionListener(e -> {
            try {
                HashMap<String, ArrayList<Pair<String, Integer>>> data = W_RFiles.importFromFile();
                network = new Network(data);
                backupNetwork = new Network(data);
                graphPanel.setNetwork(network);
                refreshTraversalCombos();
                infoArea.setText("Network loaded from file.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage());
            }
        });

        reloadButton.addActionListener(e -> {
            if (backupNetwork == null) {
                JOptionPane.showMessageDialog(this, "No saved graph state to reload.");
                return;
            }
            network = new Network(backupNetwork.toExport());
            graphPanel.setNetwork(network);
            refreshTraversalCombos();
            infoArea.setText("Graph reloaded.");
        });

        addStationButton.addActionListener(e -> {
            if (network == null) {
                JOptionPane.showMessageDialog(this, "Load a network first.");
                return;
            }

            String name = JOptionPane.showInputDialog(this, "Enter station name:");
            if (name == null || name.trim().isEmpty()) return;

            network.addStation(name.trim());
            saveCurrentState("Station added: " + name.trim());
        });

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
                saveCurrentState("Railway added: " + from.trim() + " -> " + to.trim() + " (" + distance + ")");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Distance must be a number.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

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

            saveCurrentState("Station deleted: " + name.trim());
        });

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

            saveCurrentState("Railway deleted: " + from.trim() + " -> " + to.trim());
        });

        clearButton.addActionListener(e -> {
            graphPanel.setNetwork(null);
            infoArea.setText("Drawing cleared.");
        });

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
                result.append(p.first).append(" -> ").append(p.second).append("\n");
            }

            JOptionPane.showMessageDialog(this, result.toString());
        });

        traverseButton.addActionListener(e -> {
            if (network == null) {
                JOptionPane.showMessageDialog(this, "Load a network first.");
                return;
            }

            String current = (String) currentStationCombo.getSelectedItem();
            String destination = (String) destinationCombo.getSelectedItem();

            if (current == null) {
                JOptionPane.showMessageDialog(this, "Select a station.");
                return;
            }

            if (destination == null) {
                JOptionPane.showMessageDialog(this, "Select a destination.");
                return;
            }

            int cutIndex = destination.lastIndexOf(" (");
            String nextStation = (cutIndex == -1) ? destination.trim() : destination.substring(0, cutIndex).trim();

            currentStationCombo.setSelectedItem(nextStation);
            updateDestinationCombo();
            infoArea.setText("Moved from " + current + " to " + nextStation);
        });

        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setBackground(BLUE);
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(DARK_BLUE, 1));
    }

    private void saveCurrentState(String message) {
        try {
            network.exportNetworkToFile();
            backupNetwork = new Network(network.toExport());
            graphPanel.setNetwork(network);
            refreshTraversalCombos();
            infoArea.setText(message);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving network: " + ex.getMessage());
        }
    }

    private void refreshTraversalCombos() {
        currentStationCombo.removeAllItems();
        if (network == null) return;

        ArrayList<String> names = network.getStationNames();
        for (String name : names) {
            currentStationCombo.addItem(name);
        }

        if (currentStationCombo.getItemCount() > 0) {
            currentStationCombo.setSelectedIndex(0);
        }
        updateDestinationCombo();
    }

    private void updateDestinationCombo() {
        destinationCombo.removeAllItems();

        if (network == null) return;

        String current = (String) currentStationCombo.getSelectedItem();
        if (current == null) return;

        ArrayList<String> neighbors = network.getNeighbors(current);
        for (String n : neighbors) {
            destinationCombo.addItem(n);
        }
    }
}