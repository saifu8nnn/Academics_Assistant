package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.sql.*;
import database.DatabaseConnection;

public class RegistrationFrame extends JFrame {
    private JComboBox<String> userTypeCombo;
    private JTextField enrollmentField, nameField, userIdField, emailField, phoneField;
    private JPasswordField passwordField;
    private JComboBox<String> semesterCombo;
    private JLabel enrollmentLabel, semesterLabel;

    public RegistrationFrame() {
        setTitle("Academic Assistant - Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(400, 600));
        setContentPane(layeredPane);

        SpaceBackgroundPanel backgroundPanel = new SpaceBackgroundPanel();
        backgroundPanel.setBounds(0, 0, 400, 600);
        layeredPane.add(backgroundPanel, Integer.valueOf(0));

        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setBounds(0, 0, 400, 600);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Register Account");
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Theme.TEXT_QW);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(30));

        // User Type
        JLabel userTypeLabel = new JLabel("Register as");
        userTypeLabel.setFont(Theme.NORMAL_FONT);
        userTypeLabel.setForeground(Theme.TEXT_QW);
        userTypeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(userTypeLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        String[] userTypes = {"Student", "Teacher"};
        userTypeCombo = new JComboBox<>(userTypes);
        Theme.styleComboBox(userTypeCombo);
        userTypeCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(userTypeCombo);
        mainPanel.add(Box.createVerticalStrut(20));

        // Enrollment (Student only)
        enrollmentLabel = new JLabel("Enrollment Number");
        enrollmentLabel.setFont(Theme.NORMAL_FONT);
        enrollmentLabel.setForeground(Theme.TEXT_QW);
        enrollmentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(enrollmentLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        enrollmentField = new JTextField();
        Theme.styleTextField(enrollmentField);
        enrollmentField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(enrollmentField);
        mainPanel.add(Box.createVerticalStrut(20));

        // Name
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setFont(Theme.NORMAL_FONT);
        nameLabel.setForeground(Theme.TEXT_QW);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(nameLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        nameField = new JTextField();
        Theme.styleTextField(nameField);
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(nameField);
        mainPanel.add(Box.createVerticalStrut(20));

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

        // Semester (Student only)
        semesterLabel = new JLabel("Semester");
        semesterLabel.setFont(Theme.NORMAL_FONT);
        semesterLabel.setForeground(Theme.TEXT_QW);
        semesterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(semesterLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        String[] semesters = {"1", "2", "3", "4", "5", "6", "7", "8"};
        semesterCombo = new JComboBox<>(semesters);
        Theme.styleComboBox(semesterCombo);
        semesterCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(semesterCombo);
        mainPanel.add(Box.createVerticalStrut(20));

        // Email
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(Theme.NORMAL_FONT);
        emailLabel.setForeground(Theme.TEXT_QW);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(emailLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        emailField = new JTextField();
        Theme.styleTextField(emailField);
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(emailField);
        mainPanel.add(Box.createVerticalStrut(20));

        // Phone
        JLabel phoneLabel = new JLabel("Phone");
        phoneLabel.setFont(Theme.NORMAL_FONT);
        phoneLabel.setForeground(Theme.TEXT_QW);
        phoneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(phoneLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        phoneField = new JTextField();
        Theme.styleTextField(phoneField);
        phoneField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(phoneField);
        mainPanel.add(Box.createVerticalStrut(30));

        // Register Button
        JButton registerButton = new JButton("Register");
        Theme.styleButton(registerButton);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setBackground(new Color(138, 43, 226));
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(e -> handleRegister());
        mainPanel.add(registerButton);
        mainPanel.add(Box.createVerticalStrut(10));

        // Back to Login Button
        JButton backButton = new JButton("Back to Login");
        Theme.styleButton(backButton);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setBackground(new Color(70, 130, 180));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> {
            this.dispose();
            new LoginFrame().setVisible(true);
        });
        mainPanel.add(backButton);

        // Instead of adding mainPanel directly, wrap it in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBounds(0, 0, 400, 600);
        layeredPane.add(scrollPane, Integer.valueOf(1));

        // Show/hide fields based on user type
        userTypeCombo.addActionListener(e -> updateFieldsForUserType());
        updateFieldsForUserType();
    }

    private void updateFieldsForUserType() {
        boolean isStudent = userTypeCombo.getSelectedItem().equals("Student");
        enrollmentLabel.setVisible(isStudent);
        enrollmentField.setVisible(isStudent);
        semesterLabel.setVisible(isStudent);
        semesterCombo.setVisible(isStudent);
    }

    private void handleRegister() {
        String userType = (String) userTypeCombo.getSelectedItem();
        String enrollment = enrollmentField.getText().trim();
        String name = nameField.getText().trim();
        String userId = userIdField.getText().trim();
        String password = new String(passwordField.getPassword());
        String semester = (String) semesterCombo.getSelectedItem();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || userId.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty() || (userType.equals("Student") && (enrollment.isEmpty() || semester.isEmpty()))) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Insert into users table
            PreparedStatement userStmt = conn.prepareStatement("INSERT INTO users (user_id, password, user_type) VALUES (?, ?, ?)");
            userStmt.setString(1, userId);
            userStmt.setString(2, password);
            userStmt.setString(3, userType);
            userStmt.executeUpdate();
            userStmt.close();

            if (userType.equals("Student")) {
                PreparedStatement stuStmt = conn.prepareStatement("INSERT INTO students (user_id, enrollment, name, semester, email, phone) VALUES (?, ?, ?, ?, ?, ?)");
                stuStmt.setString(1, userId);
                stuStmt.setString(2, enrollment);
                stuStmt.setString(3, name);
                stuStmt.setInt(4, Integer.parseInt(semester));
                stuStmt.setString(5, email);
                stuStmt.setString(6, phone);
                stuStmt.executeUpdate();
                stuStmt.close();
            } else {
                PreparedStatement tchStmt = conn.prepareStatement("INSERT INTO teachers (user_id, name, email, phone) VALUES (?, ?, ?, ?)");
                tchStmt.setString(1, userId);
                tchStmt.setString(2, name);
                tchStmt.setString(3, email);
                tchStmt.setString(4, phone);
                tchStmt.executeUpdate();
                tchStmt.close();
            }

            JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            new LoginFrame().setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error during registration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 🌌 Custom Space Background Panel (same as LoginFrame)
    class SpaceBackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gradient = new GradientPaint(0, 0, new Color(10, 10, 30),
                    getWidth(), getHeight(), new Color(30, 20, 60));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(Color.WHITE);
            for (int i = 0; i < 1000; i++) {
                int x = (int) (Math.random() * getWidth());
                int y = (int) (Math.random() * getHeight());
                int size = (int) (Math.random() * 2) + 1;
                g2d.fillOval(x, y, size, size);
            }

            int planetSize = 300;
            int planetX = getWidth() - planetSize / 2;
            int planetY = -planetSize / 4;

            GradientPaint planetGradient = new GradientPaint(planetX, planetY, new Color(138, 43, 226),
                    planetX + planetSize, planetY + planetSize, new Color(75, 0, 130));
            g2d.setPaint(planetGradient);
            g2d.fill(new Ellipse2D.Double(planetX, planetY, planetSize, planetSize));

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