import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphPanel extends JPanel {

    private static final Color BG = new Color(20, 20, 20);
    private static final Color EDGE = new Color(255, 255, 255);
    private static final Color NODE = new Color(20, 65, 180);
    private static final Color NODE_BORDER = new Color(20, 65, 180);
    private static final Color TEXT = Color.WHITE;

    private Network network;

    public GraphPanel() {
        setBackground(BG);
        setOpaque(true);
    }

    public void setNetwork(Network network) {
        this.network = network;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BG);
        g2.fillRect(0, 0, getWidth(), getHeight());

        if (network == null) {
            g2.setColor(TEXT);
            g2.drawString("No network loaded.", 20, 20);
            return;
        }

        HashMap<String, ArrayList<Pair<String, Integer>>> data = network.toExport();
        List<String> stations = new ArrayList<>(data.keySet());

        int width = getWidth();
        int height = getHeight();

        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 3;

        HashMap<String, Point> positions = new HashMap<>();

        for (int i = 0; i < stations.size(); i++) {
            String station = stations.get(i);
            double angle = 2 * Math.PI * i / stations.size();

            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));

            positions.put(station, new Point(x, y));
        }

        int nodeRadius = 20;

        g2.setColor(EDGE);
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

                g2.drawLine(startX, startY, endX, endY);
                drawArrowHead(g2, startX, startY, endX, endY);

                int labelX = startX + (endX - startX) / 3;
                int labelY = startY + (endY - startY) / 3;

                g2.setColor(TEXT);
                g2.drawString(String.valueOf(edge.second), labelX, labelY);
                g2.setColor(EDGE);
            }
        }

        for (String station : stations) {
            Point p = positions.get(station);

            g2.setColor(NODE);
            g2.fillOval(p.x - nodeRadius, p.y - nodeRadius, nodeRadius * 2, nodeRadius * 2);

            g2.setColor(NODE_BORDER);
            g2.drawOval(p.x - nodeRadius, p.y - nodeRadius, nodeRadius * 2, nodeRadius * 2);

            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(station);

            g2.setColor(TEXT);
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

        g2.setColor(EDGE);
        g2.fillPolygon(xPoints, yPoints, 3);
    }
}