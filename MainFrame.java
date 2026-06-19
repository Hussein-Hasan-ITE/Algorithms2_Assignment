import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainFrame extends JFrame {

    private Network network;
    private GraphPanel graphPanel;

    public MainFrame() {
        setTitle("Graph Network");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton loadButton = new JButton("Load");
        JButton addStationButton = new JButton("Add Station");
        JButton addRailwayButton = new JButton("Add Railway");
        JButton deleteStationButton = new JButton("Delete Station");
        JButton deleteRailwayButton = new JButton("Delete Railway");
        JButton clearButton = new JButton("Clear");

        buttonPanel.add(loadButton);
        buttonPanel.add(addStationButton);
        buttonPanel.add(addRailwayButton);
        buttonPanel.add(deleteStationButton);
        buttonPanel.add(deleteRailwayButton);
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.NORTH);

        // Graph panel
        graphPanel = new GraphPanel();
        add(graphPanel, BorderLayout.CENTER);

        // LOAD
        loadButton.addActionListener(e -> {
            try {
                HashMap<String, ArrayList<Pair<String, Integer>>> data = W_RFiles.importFromFile();
                network = new Network(data);
                graphPanel.setNetwork(network);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage());
            }
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

            try {
                network.exportNetworkToFile();
                graphPanel.setNetwork(network);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting file: " + ex.getMessage());
            }
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

                network.exportNetworkToFile();
                graphPanel.setNetwork(network);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Distance must be a number.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
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

            try {
                network.exportNetworkToFile();
                graphPanel.setNetwork(network);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting file: " + ex.getMessage());
            }
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
                JOptionPane.showMessageDialog(this, "Could not delete railway. Check station names.");
                return;
            }

            try {
                network.exportNetworkToFile();
                graphPanel.setNetwork(network);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting file: " + ex.getMessage());
            }
        });

        // CLEAR
        clearButton.addActionListener(e -> {
            network = null;
            graphPanel.setNetwork(null);
        });

        setVisible(true);
    }
}