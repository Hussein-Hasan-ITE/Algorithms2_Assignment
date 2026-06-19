import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphPanel extends JPanel {

    private Network network;

    public void setNetwork(Network network) {
        this.network = network;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (network == null) {
            g.setColor(Color.BLACK);
            g.drawString("No network loaded.", 20, 20);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        HashMap<String, ArrayList<Pair<String, Integer>>> data = network.toExport();
        List<String> stations = new ArrayList<>(data.keySet());

        int width = getWidth();
        int height = getHeight();

        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 3;

        HashMap<String, Point> positions = new HashMap<>();

        // place stations in a circle
        for (int i = 0; i < stations.size(); i++) {
            String station = stations.get(i);
            double angle = 2 * Math.PI * i / stations.size();

            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));

            positions.put(station, new Point(x, y));
        }

        int nodeRadius = 20;

        // draw edges with arrows
        g2.setColor(Color.GRAY);
        for (String from : stations) {
            Point p1 = positions.get(from);

            for (Pair<String, Integer> edge : data.get(from)) {
                Point p2 = positions.get(edge.first);

                if (p1 == null || p2 == null) continue;

                double dx = p2.x - p1.x;
                double dy = p2.y - p1.y;
                double length = Math.sqrt(dx * dx + dy * dy);

                if (length == 0) continue;

                int startX = (int) (p1.x + nodeRadius * dx / length);
                int startY = (int) (p1.y + nodeRadius * dy / length);
                int endX = (int) (p2.x - nodeRadius * dx / length);
                int endY = (int) (p2.y - nodeRadius * dy / length);

                // line
                g2.drawLine(startX, startY, endX, endY);

                // arrow head
                drawArrowHead(g2, startX, startY, endX, endY);

                // weight label
                int midX = (startX + endX) / 2;
                int midY = (startY + endY) / 2;

                g2.setColor(Color.RED);
                g2.drawString(String.valueOf(edge.second), midX, midY);
                g2.setColor(Color.GRAY);
            }
        }

        // draw nodes
        for (String station : stations) {
            Point p = positions.get(station);

            g2.setColor(Color.CYAN);
            g2.fillOval(p.x - nodeRadius, p.y - nodeRadius, nodeRadius * 2, nodeRadius * 2);

            g2.setColor(Color.BLACK);
            g2.drawOval(p.x - nodeRadius, p.y - nodeRadius, nodeRadius * 2, nodeRadius * 2);

            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(station);

            g2.drawString(station, p.x - textWidth / 2, p.y + nodeRadius + 15);
        }
    }

    private void drawArrowHead(Graphics2D g2, int x1, int y1, int x2, int y2) {
        int arrowSize = 10;
        double angle = Math.atan2(y2 - y1, x2 - x1);

        int xArrow1 = (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6));
        int yArrow1 = (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6));

        int xArrow2 = (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6));
        int yArrow2 = (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6));

        int[] xPoints = {x2, xArrow1, xArrow2};
        int[] yPoints = {y2, yArrow1, yArrow2};

        g2.fillPolygon(xPoints, yPoints, 3);
    }
}