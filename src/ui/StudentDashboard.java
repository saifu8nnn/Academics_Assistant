package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.sql.*;
import database.DatabaseConnection;

public class StudentDashboard extends JFrame {
    private String userId;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel menuPanel;
    private boolean isMenuVisible = true;

    public StudentDashboard(String userId) {
        this.userId = userId;
        setTitle("Student Dashboard");
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
        menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, BorderLayout.WEST);

        // Create content panel with CardLayout
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // Add different panels for each menu item
        contentPanel.add(createProfilePanel(), "PROFILE");
        contentPanel.add(createAttendancePanel(), "ATTENDANCE");
        contentPanel.add(createGradesPanel(), "GRADES");
        contentPanel.add(createTimetablePanel(), "TIMETABLE");
        contentPanel.add(createSyllabusPanel(), "SYLLABUS");
        contentPanel.add(createNotesPanel(), "NOTES");

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
        headerPanel.setBackground(new Color(10, 10, 30));
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Three dots menu button
        JButton menuButton = new JButton("⋮");
        menuButton.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        menuButton.setForeground(Color.WHITE);
        menuButton.setBackground(new Color(10, 10, 30));
        menuButton.setBorderPainted(false);
        menuButton.setFocusPainted(false);
        menuButton.addActionListener(e -> toggleMenu());
        headerPanel.add(menuButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("Academic Assistant");
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel userLabel = new JLabel("Student Portal");
        userLabel.setFont(Theme.HEADER_FONT);
        userLabel.setForeground(Color.WHITE);
        headerPanel.add(userLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private void toggleMenu() {
        isMenuVisible = !isMenuVisible;
        menuPanel.setVisible(isMenuVisible);
        menuPanel.setPreferredSize(new Dimension(isMenuVisible ? 200 : 0, 0));
        menuPanel.revalidate();
        menuPanel.repaint();
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(30, 20, 60));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        menuPanel.setPreferredSize(new Dimension(200, 0));

        String[] menuItems = {"Profile", "Attendance", "Grades", "Timetable", "Syllabus", "Notes"};
        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setFont(Theme.NORMAL_FONT);
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(30, 20, 60));
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 40));
            button.addActionListener(e -> cardLayout.show(contentPanel, item.toUpperCase()));
            menuPanel.add(button);
            menuPanel.add(Box.createVerticalStrut(5));
        }

        return menuPanel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(10, 10, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Hi " + getStudentName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(welcomeLabel);
        panel.add(Box.createVerticalStrut(30));

        // Profile image placeholder
        JLabel imageLabel = new JLabel("👤");
        imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 72));
        imageLabel.setForeground(Color.WHITE);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(imageLabel);
        panel.add(Box.createVerticalStrut(20));

        // Profile details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(0, 1, 10, 10));
        detailsPanel.setBackground(new Color(10, 10, 30));
        detailsPanel.setMaximumSize(new Dimension(400, 0));

        JLabel nameLabel = new JLabel("Name: " + getStudentName());
        nameLabel.setFont(Theme.NORMAL_FONT);
        nameLabel.setForeground(Color.WHITE);
        detailsPanel.add(nameLabel);

        JLabel enrollmentLabel = new JLabel("Enrollment: " + userId);
        enrollmentLabel.setFont(Theme.NORMAL_FONT);
        enrollmentLabel.setForeground(Color.WHITE);
        detailsPanel.add(enrollmentLabel);

        panel.add(detailsPanel);

        return panel;
    }

    private String getStudentName() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT name FROM students WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "Student";
    }

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Attendance Records");
        headerLabel.setFont(Theme.HEADER_FONT);
        headerLabel.setForeground(Theme.TEXT_COLOR);
        panel.add(headerLabel, BorderLayout.NORTH);

        JTable attendanceTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Academic Performance");
        headerLabel.setFont(Theme.HEADER_FONT);
        headerLabel.setForeground(Theme.TEXT_COLOR);
        panel.add(headerLabel, BorderLayout.NORTH);

        JTable gradesTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTimetablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Class Schedule");
        headerLabel.setFont(Theme.HEADER_FONT);
        headerLabel.setForeground(Theme.TEXT_COLOR);
        panel.add(headerLabel, BorderLayout.NORTH);

        JTable timetableTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(timetableTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSyllabusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Course Syllabus");
        headerLabel.setFont(Theme.HEADER_FONT);
        headerLabel.setForeground(Theme.TEXT_COLOR);
        panel.add(headerLabel, BorderLayout.NORTH);

        JTextArea syllabusArea = new JTextArea();
        syllabusArea.setFont(Theme.NORMAL_FONT);
        syllabusArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(syllabusArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createNotesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Study Notes");
        headerLabel.setFont(Theme.HEADER_FONT);
        headerLabel.setForeground(Theme.TEXT_COLOR);
        panel.add(headerLabel, BorderLayout.NORTH);

        JTextArea notesArea = new JTextArea();
        notesArea.setFont(Theme.NORMAL_FONT);
        notesArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(notesArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadUserData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM students WHERE user_id = ?";
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
}