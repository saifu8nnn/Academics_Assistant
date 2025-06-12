package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.sql.*;
import database.DatabaseConnection;

public class LoginFrame extends JFrame {
    private JTextField userIdField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeCombo;

    public LoginFrame() {
        setTitle("Academic Assistant - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // LayeredPane to hold background and mainPanel
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(400, 500));
        setContentPane(layeredPane);

        // Space-themed background
        SpaceBackgroundPanel backgroundPanel = new SpaceBackgroundPanel();
        backgroundPanel.setBounds(0, 0, 400, 500);
        layeredPane.add(backgroundPanel, Integer.valueOf(0));

        // Semi-transparent mainPanel
        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setBounds(0, 0, 400, 500);
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Title
        JLabel titleLabel = new JLabel("Academic Assistant");
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.TEXT_QW);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(40));

        // User ID
        JLabel userIdLabel = new JLabel("User ID");
        userIdLabel.setFont(Theme.NORMAL_FONT);
        userIdLabel.setForeground(Theme.TEXT_QW);
        userIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(userIdLabel);
        mainPanel.add(Box.createVerticalStrut(5));

        userIdField = new JTextField();
        Theme.styleTextField(userIdField);
        userIdField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(userIdField);
        mainPanel.add(Box.createVerticalStrut(20));

        // Password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(Theme.NORMAL_FONT);
        passwordLabel.setForeground(Theme.TEXT_QW);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(passwordLabel);
        mainPanel.add(Box.createVerticalStrut(5));

        passwordField = new JPasswordField();
        Theme.styleTextField(passwordField);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(20));

        // User Type
        JLabel userTypeLabel = new JLabel("User Type");
        userTypeLabel.setFont(Theme.NORMAL_FONT);
        userTypeLabel.setForeground(Theme.TEXT_COLOR);
        userTypeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(userTypeLabel);
        mainPanel.add(Box.createVerticalStrut(5));

        String[] userTypes = {"Student", "Teacher"};
        userTypeCombo = new JComboBox<>(userTypes);
        Theme.styleComboBox(userTypeCombo);
        userTypeCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(userTypeCombo);
        mainPanel.add(Box.createVerticalStrut(40));

        // Login Button
        JButton loginButton = new JButton("Login");
        Theme.styleButton(loginButton);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> handleLogin());
        mainPanel.add(loginButton);

        layeredPane.add(mainPanel, Integer.valueOf(1));
    }

    private void handleLogin() {
        String userId = userIdField.getText();
        String password = new String(passwordField.getPassword());
        String userType = (String) userTypeCombo.getSelectedItem();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE user_id = ? AND password = ? AND user_type = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userId);
            pstmt.setString(2, password);
            pstmt.setString(3, userType);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                this.dispose();

                if (userType.equals("Student")) {
                    new StudentDashboard(userId).setVisible(true);
                } else {
                    new TeacherDashboard(userId).setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }

    // 🌌 Custom Space Background Panel
    class SpaceBackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Space gradient
            GradientPaint gradient = new GradientPaint(0, 0, new Color(10, 10, 30),
                    getWidth(), getHeight(), new Color(30, 20, 60));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Stars
            g2d.setColor(Color.WHITE);
            for (int i = 0; i < 1000; i++) {
                int x = (int) (Math.random() * getWidth());
                int y = (int) (Math.random() * getHeight());
                int size = (int) (Math.random() * 2) + 1;
                g2d.fillOval(x, y, size, size);
            }

            // Planet
            int planetSize = 300;
            int planetX = getWidth() - planetSize / 2;
            int planetY = -planetSize / 4;

            GradientPaint planetGradient = new GradientPaint(planetX, planetY, new Color(138, 43, 226),
                    planetX + planetSize, planetY + planetSize, new Color(75, 0, 130));
            g2d.setPaint(planetGradient);
            g2d.fill(new Ellipse2D.Double(planetX, planetY, planetSize, planetSize));

            // Planet glow
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            Point2D center = new Point2D.Double(planetX + planetSize / 2.0, planetY + planetSize / 2.0);
            float radius = planetSize / 2 + 20;
            float[] dist = {0f, 1f};
            Color[] colors = {new Color(138, 43, 226, 100), new Color(138, 43, 226, 0)};
            RadialGradientPaint glow = new RadialGradientPaint(center, radius, dist, colors);
            g2d.setPaint(glow);
            g2d.fill(new Ellipse2D.Double(planetX - 20, planetY - 20, planetSize + 40, planetSize + 40));

            g2d.dispose();
        }
    }
}
