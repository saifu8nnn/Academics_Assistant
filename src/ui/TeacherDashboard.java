package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import database.DatabaseConnection;

public class TeacherDashboard extends JFrame {
    private String userId;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JComboBox<String> semesterCombo;

    public TeacherDashboard(String userId) {
        this.userId = userId;
        setTitle("Teacher Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 500));

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.BACKGROUND_COLOR);

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create hamburger menu
        JPanel menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, BorderLayout.WEST);

        // Create content panel with CardLayout
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // Add different panels for each menu item
        contentPanel.add(createProfilePanel(), "PROFILE");
        contentPanel.add(createSyllabusPanel(), "SYLLABUS");
        contentPanel.add(createGradesPanel(), "GRADES");
        contentPanel.add(createAttendancePanel(), "ATTENDANCE");
        contentPanel.add(createNotificationPanel(), "NOTIFICATION");

        // Wrap content panel in a scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Show profile panel by default
        cardLayout.show(contentPanel, "PROFILE");

        add(mainPanel);
        loadUserData();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel titleLabel = new JLabel("Academic Assistant");
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel("Teacher Portal");
        userLabel.setFont(Theme.HEADER_FONT);
        userLabel.setForeground(Color.WHITE);
        headerPanel.add(userLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        menuPanel.setPreferredSize(new Dimension(200, 0));

        String[] menuItems = {"Profile", "Add Syllabus", "Add Grades", "Add Attendance", "Push Notification"};
        for (String item : menuItems) {
            JButton button = new JButton(item);
            Theme.styleMenuButton(button);
            button.addActionListener(e -> cardLayout.show(contentPanel, item.toUpperCase()));
            menuPanel.add(button);
            menuPanel.add(Box.createVerticalStrut(5));
        }

        return menuPanel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Profile header
        JLabel headerLabel = new JLabel("Teacher Profile");
        headerLabel.setFont(Theme.HEADER_FONT);
        headerLabel.setForeground(Theme.TEXT_COLOR);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(headerLabel);
        panel.add(Box.createVerticalStrut(30));

        // Profile image placeholder
        JLabel imageLabel = new JLabel("👨‍🏫");
        imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 72));
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(imageLabel);
        panel.add(Box.createVerticalStrut(20));

        // Profile details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(0, 1, 10, 10));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setMaximumSize(new Dimension(400, 0));

        JLabel nameLabel = new JLabel("Name: ");
        nameLabel.setFont(Theme.NORMAL_FONT);
        detailsPanel.add(nameLabel);

        JLabel idLabel = new JLabel("Teacher ID: ");
        idLabel.setFont(Theme.NORMAL_FONT);
        detailsPanel.add(idLabel);

        panel.add(detailsPanel);

        return panel;
    }

    private JPanel createSyllabusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel("Add Syllabus");
        headerLabel.setFont(Theme.HEADER_FONT);
        headerLabel.setForeground(Theme.TEXT_COLOR);
        panel.add(headerLabel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Semester selection
        JPanel semesterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        semesterPanel.setBackground(Color.WHITE);
        semesterCombo = new JComboBox<>(new String[]{"Semester 1", "Semester 2", "Semester 3", "Semester 4"});
        Theme.styleComboBox(semesterCombo);
        semesterPanel.add(new JLabel("Select Semester:"));
        semesterPanel.add(semesterCombo);
        contentPanel.add(semesterPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Syllabus content
        JTextArea syllabusArea = new JTextArea();
        syllabusArea.setFont(Theme.NORMAL_FONT);
        JScrollPane scrollPane = new JScrollPane(syllabusArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(20));

        // Save button
        JButton saveButton = new JButton("Save Syllabus");
        Theme.styleButton(saveButton);
        saveButton.addActionListener(e -> saveSyllabus(syllabusArea.getText()));
        contentPanel.add(saveButton);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel("Add Grades");
        headerLabel.setFont(Theme.HEADER_FONT);
        headerLabel.setForeground(Theme.TEXT_COLOR);
        panel.add(headerLabel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Semester selection
        JPanel semesterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        semesterPanel.setBackground(Color.WHITE);
        semesterCombo = new JComboBox<>(new String[]{"Semester 1", "Semester 2", "Semester 3", "Semester 4"});
        Theme.styleComboBox(semesterCombo);
        semesterPanel.add(new JLabel("Select Semester:"));
        semesterPanel.add(semesterCombo);
        contentPanel.add(semesterPanel, BorderLayout.NORTH);

        // Grades table
        JTable gradesTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Save button
        JButton saveButton = new JButton("Save Grades");
        Theme.styleButton(saveButton);
        saveButton.addActionListener(e -> saveGrades(gradesTable));
        contentPanel.add(saveButton, BorderLayout.SOUTH);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel("Add Attendance");
        headerLabel.setFont(Theme.HEADER_FONT);
        headerLabel.setForeground(Theme.TEXT_COLOR);
        panel.add(headerLabel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Semester selection
        JPanel semesterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        semesterPanel.setBackground(Color.WHITE);
        semesterCombo = new JComboBox<>(new String[]{"Semester 1", "Semester 2", "Semester 3", "Semester 4"});
        Theme.styleComboBox(semesterCombo);
        semesterPanel.add(new JLabel("Select Semester:"));
        semesterPanel.add(semesterCombo);
        contentPanel.add(semesterPanel, BorderLayout.NORTH);

        // Attendance table
        JTable attendanceTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Save button
        JButton saveButton = new JButton("Save Attendance");
        Theme.styleButton(saveButton);
        saveButton.addActionListener(e -> saveAttendance(attendanceTable));
        contentPanel.add(saveButton, BorderLayout.SOUTH);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createNotificationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel("Send Notification");
        headerLabel.setFont(Theme.HEADER_FONT);
        headerLabel.setForeground(Theme.TEXT_COLOR);
        panel.add(headerLabel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Semester selection
        JPanel semesterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        semesterPanel.setBackground(Color.WHITE);
        semesterCombo = new JComboBox<>(new String[]{"Semester 1", "Semester 2", "Semester 3", "Semester 4"});
        Theme.styleComboBox(semesterCombo);
        semesterPanel.add(new JLabel("Select Semester:"));
        semesterPanel.add(semesterCombo);
        contentPanel.add(semesterPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Notification content
        JTextArea notificationArea = new JTextArea();
        notificationArea.setFont(Theme.NORMAL_FONT);
        JScrollPane scrollPane = new JScrollPane(notificationArea);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalStrut(20));

        // Send button
        JButton sendButton = new JButton("Send Notification");
        Theme.styleButton(sendButton);
        sendButton.addActionListener(e -> sendNotification(notificationArea.getText()));
        contentPanel.add(sendButton);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private void loadUserData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM teachers WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Update profile panel with user data
                // This will be implemented when we have the database schema
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading user data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveSyllabus(String syllabus) {
        // Implement syllabus saving logic
    }

    private void saveGrades(JTable gradesTable) {
        // Implement grades saving logic
    }

    private void saveAttendance(JTable attendanceTable) {
        // Implement attendance saving logic
    }

    private void sendNotification(String notification) {
        // Implement notification sending logic
    }
}