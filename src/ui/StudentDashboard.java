package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.sql.*;
import database.DatabaseConnection;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class StudentDashboard extends JFrame {
    private String userId;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel menuPanel;
    private boolean isMenuVisible = true;
    private JButton selectedButton = null;
    private DefaultTableModel GradesTableModel;
    private JTable GradesTable;
    private DefaultTableModel attendanceTableModel;
    private JTable attendanceTable;
    private JTable timetableTable;

    public StudentDashboard(String userId) {
        this.userId = userId;
        setTitle("Ilmly-Student Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 500));
        setIconImage(new ImageIcon(getClass().getResource("/ilmly.png")).getImage());
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

        mainPanel.add(contentPanel, BorderLayout.CENTER);

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
        menuButton.setFont(new Font("Segoe UI SYMBOL", Font.BOLD, 20));
        menuButton.setForeground(new Color(255, 255, 255));
        menuButton.setBackground(new Color(10, 10, 30));
        menuButton.setBorderPainted(false);
        menuButton.setFocusPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setOpaque(false);
        menuButton.addActionListener(e -> toggleMenu());
        headerPanel.add(menuButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel("ilmly");
        titleLabel.setFont(Theme.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Settings button
        JButton settingsButton = new JButton("⚙");
        settingsButton.setFont(new Font("Segoe UI SYMBOL", Font.PLAIN, 24));
        settingsButton.setForeground(Color.WHITE);
        settingsButton.setBackground(new Color(10, 10, 30));
        settingsButton.setBorderPainted(false);
        settingsButton.setFocusPainted(false);
        settingsButton.setContentAreaFilled(false);
        settingsButton.setOpaque(false);
        settingsButton.addActionListener(e -> {
            SettingsDialog settingsDialog = new SettingsDialog(this, userId, "Student");
            settingsDialog.setVisible(true);
        });
        headerPanel.add(settingsButton, BorderLayout.EAST);

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

        String[] menuItems = {"Profile", "Attendance", "Grades", "Timetable", "Syllabus"};
        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setFont(Theme.NORMAL_FONT);
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(30, 20, 60));
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 40));

            // Add hover effect
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (button != selectedButton) {
                        button.setBackground(new Color(50, 40, 80));
                    }
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (button != selectedButton) {
                        button.setBackground(new Color(30, 20, 60));
                    }
                }
            });

            button.addActionListener(e -> {
                // Reset previous selected button
                if (selectedButton != null) {
                    selectedButton.setBackground(new Color(30, 20, 60));
                    selectedButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                }

                // Set new selected button
                selectedButton = button;
                button.setBackground(new Color(70, 60, 100));
                button.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(138, 43, 226)));

                cardLayout.show(contentPanel, item.toUpperCase());
            });

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
        welcomeLabel.setForeground(new Color(138, 43, 226));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(welcomeLabel);
        panel.add(Box.createVerticalStrut(30));

        // Profile image placeholder
        JLabel imageLabel = new JLabel("👤");
        imageLabel.setFont(new Font("Segoe UI IMAGE", Font.PLAIN, 72));
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

    private JPanel createGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Grades Records");
        headerLabel.setFont(Theme.HEADER_FONT);
        headerLabel.setForeground(new Color(138, 43, 226));
        panel.add(headerLabel, BorderLayout.NORTH);

        // Table setup
        GradesTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        GradesTable = new JTable(GradesTableModel);
        GradesTable.setSelectionBackground(new Color(138, 43, 226));
        GradesTable.setSelectionForeground(Color.WHITE);
        JScrollPane gradesScrollPane = new JScrollPane(GradesTable);
        gradesScrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        gradesScrollPane.getViewport().setBackground(Color.WHITE);
        gradesScrollPane.setBackground(Color.WHITE);
        panel.add(gradesScrollPane, BorderLayout.CENTER);

        // Refresh button
        JButton refreshButton = new JButton("Refresh Grades");
        refreshButton.setBackground(new Color(70, 130, 180));
        refreshButton.setForeground(new Color(138, 43, 226));
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadGradesTableData());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Initial load and setup
        loadGradesTableData();
        setupGradesTableAppearance();

        return panel;
    }

    private void loadGradesTableData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get student's semester
            PreparedStatement semStmt = conn.prepareStatement("SELECT semester FROM students WHERE user_id = ?");
            semStmt.setString(1, userId);
            ResultSet semRs = semStmt.executeQuery();
            int semester = 0;
            if (semRs.next()) semester = semRs.getInt("semester");
            semRs.close();
            semStmt.close();
            // Get subjects for semester
            PreparedStatement subStmt = conn.prepareStatement("SELECT subject_id, subject_code, subject_name FROM subjects WHERE semester = ?");
            subStmt.setInt(1, semester);
            ResultSet subRs = subStmt.executeQuery();
            java.util.List<String> subjectCodes = new java.util.ArrayList<>();
            java.util.List<Integer> subjectIds = new java.util.ArrayList<>();
            while (subRs.next()) {
                subjectCodes.add(subRs.getString("subject_code"));
                subjectIds.add(subRs.getInt("subject_id"));
            }
            subRs.close();
            subStmt.close();
            // Set up table columns: Subject, MST 1, MST 2, Unit Test 1-5, Main Exam
            GradesTableModel.setColumnIdentifiers(new String[]{
                    "Subject", "MST 1", "MST 2", "Unit Test 1", "Unit Test 2", "Unit Test 3", "Unit Test 4", "Unit Test 5", "Main Exam"
            });
            GradesTableModel.setRowCount(0);
            // Fetch grades for each subject
            for (int i = 0; i < subjectIds.size(); i++) {
                String subject = subjectCodes.get(i);
                PreparedStatement gradeStmt = conn.prepareStatement(
                        "SELECT mst1, mst2, unit_test1, unit_test2, unit_test3, unit_test4, unit_test5, main_exam FROM grades WHERE user_id = ? AND subject_id = ?");
                gradeStmt.setString(1, userId);
                gradeStmt.setInt(2, subjectIds.get(i));
                ResultSet gradeRs = gradeStmt.executeQuery();
                String mst1 = "", mst2 = "", ut1 = "", ut2 = "", ut3 = "", ut4 = "", ut5 = "", mainExam = "";
                if (gradeRs.next()) {
                    mst1 = gradeRs.getString("mst1");
                    mst2 = gradeRs.getString("mst2");
                    ut1 = gradeRs.getString("unit_test1");
                    ut2 = gradeRs.getString("unit_test2");
                    ut3 = gradeRs.getString("unit_test3");
                    ut4 = gradeRs.getString("unit_test4");
                    ut5 = gradeRs.getString("unit_test5");
                    mainExam = gradeRs.getString("main_exam");
                }
                GradesTableModel.addRow(new Object[]{subject, mst1, mst2, ut1, ut2, ut3, ut4, ut5, mainExam});
                gradeRs.close();
                gradeStmt.close();
            }
            setupGradesTableAppearance(); // Ensure color coding is applied after data is loaded
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading Grades data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Make the grades JTable colorful
    private void setupGradesTableAppearance() {
        GradesTable.setRowHeight(30);
        GradesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        GradesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        GradesTable.getTableHeader().setBackground(new Color(70, 130, 180));
        GradesTable.getTableHeader().setForeground(Color.BLACK);
        GradesTable.setSelectionBackground(new Color(138, 43, 226));
        GradesTable.setSelectionForeground(Color.WHITE);
        GradesTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String grade = value == null ? "" : value.toString();
                if (isSelected) {
                    c.setBackground(new Color(138, 43, 226));
                    c.setForeground(Color.WHITE);
                } else if (grade.equalsIgnoreCase("A") || grade.equalsIgnoreCase("A+")) {
                    c.setBackground(new Color(144, 238, 144)); // Light green
                    c.setForeground(Color.BLACK);
                } else if (grade.equalsIgnoreCase("B") || grade.equalsIgnoreCase("B+")) {
                    c.setBackground(new Color(173, 216, 230)); // Light blue
                    c.setForeground(Color.BLACK);
                } else if (grade.equalsIgnoreCase("C")) {
                    c.setBackground(new Color(255, 255, 153)); // Light yellow
                    c.setForeground(Color.BLACK);
                } else if (grade.equalsIgnoreCase("D")) {
                    c.setBackground(new Color(255, 204, 153)); // Light orange
                    c.setForeground(Color.BLACK);
                } else if (grade.equalsIgnoreCase("F") || grade.equalsIgnoreCase("E")) {
                    c.setBackground(new Color(255, 99, 71)); // Light red
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
    }

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Attendance");
        headerLabel.setFont(Theme.HEADER_FONT);
        headerLabel.setForeground(new Color(138, 43, 226));
        panel.add(headerLabel, BorderLayout.NORTH);

        // Subject dropdown
        JComboBox<String> subjectCombo = new JComboBox<>();
        subjectCombo.setMaximumRowCount(8);
        subjectCombo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JPanel subjectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        subjectPanel.setBackground(Color.WHITE);
        subjectPanel.add(new JLabel("Select Subject:"));
        subjectPanel.add(subjectCombo);
        panel.add(subjectPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Table setup
        DefaultTableModel attendanceTableModel = new DefaultTableModel(new String[]{"Date", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        attendanceTable = new JTable(attendanceTableModel);
        attendanceTable.setRowHeight(30);
        attendanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        attendanceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        attendanceTable.getTableHeader().setBackground(new Color(70, 130, 180));
        attendanceTable.getTableHeader().setForeground(Color.BLACK);
        attendanceTable.setSelectionBackground(new Color(138, 43, 226));
        attendanceTable.setSelectionForeground(Color.WHITE);
        attendanceTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (column == 1 && value != null) ? value.toString() : "";
                if (isSelected) {
                    c.setBackground(new Color(138, 43, 226));
                    c.setForeground(Color.WHITE);
                } else if (column == 1 && status.equalsIgnoreCase("Present")) {
                    c.setBackground(new Color(144, 238, 144)); // Light green
                    c.setForeground(Color.BLACK);
                } else if (column == 1 && status.equalsIgnoreCase("Absent")) {
                    c.setBackground(new Color(255, 99, 71)); // Light red
                    c.setForeground(Color.BLACK);
                } else if (row % 2 == 0) {
                    c.setBackground(new Color(245, 245, 255)); // Light alternate row
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
        JScrollPane attendanceScrollPane = new JScrollPane(attendanceTable);
        attendanceScrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        attendanceScrollPane.getViewport().setBackground(Color.WHITE);
        attendanceScrollPane.setBackground(Color.WHITE);
        panel.add(attendanceScrollPane, BorderLayout.CENTER);

        // Attendance summary
        JLabel summaryLabel = new JLabel();
        summaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        summaryLabel.setForeground(new Color(138, 43, 226));
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.add(summaryLabel);
        panel.add(summaryPanel, BorderLayout.SOUTH);

        // Load subjects for the student's semester
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement semStmt = conn.prepareStatement("SELECT semester FROM students WHERE user_id = ?");
            semStmt.setString(1, userId);
            ResultSet semRs = semStmt.executeQuery();
            int semester = 0;
            if (semRs.next()) semester = semRs.getInt("semester");
            semRs.close();
            semStmt.close();
            PreparedStatement subStmt = conn.prepareStatement("SELECT subject_code FROM subjects WHERE semester = ?");
            subStmt.setInt(1, semester);
            ResultSet subRs = subStmt.executeQuery();
            while (subRs.next()) {
                subjectCombo.addItem(subRs.getString("subject_code"));
            }
            subRs.close();
            subStmt.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading subjects: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Listener to load attendance for selected subject
        subjectCombo.addActionListener(e -> loadAttendanceTableDataForSubject(subjectCombo, attendanceTableModel, summaryLabel));
        // Initial load
        if (subjectCombo.getItemCount() > 0) {
            subjectCombo.setSelectedIndex(0);
        }

        return panel;
    }

    private void loadAttendanceTableDataForSubject(JComboBox<String> subjectCombo, DefaultTableModel attendanceTableModel, JLabel summaryLabel) {
        String subjectCode = (String) subjectCombo.getSelectedItem();
        if (subjectCode == null) {
            attendanceTableModel.setRowCount(0);
            summaryLabel.setText("");
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get subject_id
            PreparedStatement semStmt = conn.prepareStatement("SELECT semester FROM students WHERE user_id = ?");
            semStmt.setString(1, userId);
            ResultSet semRs = semStmt.executeQuery();
            int semester = 0;
            if (semRs.next()) semester = semRs.getInt("semester");
            semRs.close();
            semStmt.close();
            PreparedStatement subStmt = conn.prepareStatement("SELECT subject_id FROM subjects WHERE semester = ? AND subject_code = ?");
            subStmt.setInt(1, semester);
            subStmt.setString(2, subjectCode);
            ResultSet subRs = subStmt.executeQuery();
            int subjectId = -1;
            if (subRs.next()) subjectId = subRs.getInt("subject_id");
            subRs.close();
            subStmt.close();
            if (subjectId == -1) {
                attendanceTableModel.setRowCount(0);
                summaryLabel.setText("");
                return;
            }
            PreparedStatement attStmt = conn.prepareStatement("SELECT date, status FROM attendance WHERE user_id = ? AND subject_id = ? ORDER BY date DESC");
            attStmt.setString(1, userId);
            attStmt.setInt(2, subjectId);
            ResultSet attRs = attStmt.executeQuery();
            attendanceTableModel.setRowCount(0);
            int total = 0, present = 0;
            while (attRs.next()) {
                String date = attRs.getString("date");
                String status = attRs.getString("status");
                attendanceTableModel.addRow(new Object[]{date, status});
                total++;
                if ("Present".equalsIgnoreCase(status)) present++;
            }
            attRs.close();
            attStmt.close();
            if (total > 0) {
                double percent = (present * 100.0) / total;
                summaryLabel.setText("Total Days: " + total + " | Present: " + present + " | Attendance: " + String.format("%.2f", percent) + "%");
            } else {
                summaryLabel.setText("No attendance records.");
            }
        } catch (SQLException ex) {
            attendanceTableModel.setRowCount(0);
            summaryLabel.setText("");
            JOptionPane.showMessageDialog(this, "Error loading attendance: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createTimetablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Class Schedule");
        headerLabel.setFont(Theme.HEADER_FONT);
        headerLabel.setForeground(new Color(138, 43, 226));
        panel.add(headerLabel, BorderLayout.NORTH);

        JPanel timetableContainer = new JPanel(new BorderLayout());
        timetableContainer.setBackground(Color.WHITE);
        timetableContainer.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        String[] days = {"Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String[] timeSlots = {
                "9:00-10:00", "10:00-10:50", "10:50-11:40", "11:40-12:30",
                "12:30-1:00", "1:00-1:50", "1:50-2:40", "2:40-3:30"
        };

        String[][] timetableData = new String[timeSlots.length][days.length];
        for (int i = 0; i < timeSlots.length; i++) {
            timetableData[i][0] = timeSlots[i];
            for (int j = 1; j < days.length; j++) {
                timetableData[i][j] = "Free";
            }
        }

        DefaultTableModel timetableModel = new DefaultTableModel(timetableData, days) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        timetableTable = new JTable(timetableModel);
        setupTableAppearance();

        JScrollPane timetableScrollPane = new JScrollPane(timetableTable);
        timetableScrollPane.setPreferredSize(new Dimension(800, 400));
        timetableScrollPane.getViewport().setBackground(Color.WHITE);
        timetableScrollPane.setBackground(Color.WHITE);
        timetableContainer.add(timetableScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton refreshButton = new JButton("Refresh Timetable");
        refreshButton.setBackground(new Color(70, 130, 180));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadTimetableFromDatabase());
        buttonPanel.add(refreshButton);

        panel.add(timetableContainer, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadTimetableFromDatabase();

        return panel;
    }

    // Separate method to set up table appearance
    private void setupTableAppearance() {
        // Customize table appearance
        timetableTable.setRowHeight(50);
        timetableTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timetableTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        timetableTable.getTableHeader().setBackground(new Color(70, 130, 180));
        timetableTable.getTableHeader().setForeground(Color.BLACK);
        timetableTable.getTableHeader().setReorderingAllowed(false);
        timetableTable.setSelectionBackground(new Color(138, 43, 226));
        timetableTable.setSelectionForeground(Color.WHITE);
        timetableTable.setRowSelectionAllowed(true);
        timetableTable.setColumnSelectionAllowed(false);

        // Set column widths
        timetableTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Time column
        for (int i = 1; i < 7; i++) {
            timetableTable.getColumnModel().getColumn(i).setPreferredWidth(130);
        }

        // Simple and clean cell renderer
        timetableTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Handle selection first - override all other colors when selected
                if (isSelected) {
                    c.setBackground(new Color(138, 43, 226));
                    c.setForeground(Color.WHITE);
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    // Normal (unselected) cell styling
                    if (column == 0) { // Time column
                        c.setBackground(new Color(240, 248, 255));
                        c.setForeground(Color.BLACK);
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else if (value != null && value.toString().contains("LUNCH BREAK")) {
                        c.setBackground(new Color(255, 228, 196));
                        c.setForeground(Color.BLACK);
                        setFont(new Font("Segoe UI", Font.BOLD, 12));
                    } else if (value != null && (value.toString().equals("Free") || value.toString().contains("Free Period"))) {
                        c.setBackground(new Color(240, 255, 240));
                        c.setForeground(Color.BLACK);
                        setFont(new Font("Segoe UI", Font.ITALIC, 12));
                    } else if (value != null && (value.toString().contains("LAB") || value.toString().contains("Lab"))) {
                        c.setBackground(new Color(255, 240, 245));
                        c.setForeground(Color.BLACK);
                        setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                        setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    }
                }

                setHorizontalAlignment(SwingConstants.CENTER);
                setVerticalAlignment(SwingConstants.CENTER);

                return c;
            }
        });
    }

    // FIXED: Updated method to load timetable from database with correct time slots
    private void loadTimetableFromDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT day, time_slot, subject, room, teacher FROM timetable WHERE user_id = ? ORDER BY FIELD(day, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'), time_slot";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, "s"); // Use your actual userId here
            ResultSet rs = pstmt.executeQuery();

            // Create a map to organize data by time slots and days
            Map<String, Map<String, String>> timetableMap = new LinkedHashMap<>();

            // Process the result set
            while (rs.next()) {
                String timeSlot = rs.getString("time_slot");
                String day = rs.getString("day");
                String subject = rs.getString("subject");
                String room = rs.getString("room");
                String teacher = rs.getString("teacher");

                // Create simple display text with subject and room only
                String displayText;
                if (subject.equals("LUNCH BREAK")) {
                    displayText = "LUNCH BREAK";
                } else if (subject.contains("Free")) {
                    displayText = subject;
                } else {
                    displayText = subject;
                    if (room != null && !room.trim().isEmpty()) {
                        displayText += "\n" + room;
                    }
                }

                timetableMap.computeIfAbsent(timeSlot, k -> new HashMap<>()).put(day, displayText);
            }

            // FIXED: Added the missing 2:40-3:30 time slot
            String[] timeSlots = {"9:00-10:00", "10:00-10:50", "10:50-11:40", "11:40-12:30", "12:30-1:00", "1:00-1:50", "1:50-2:40", "2:40-3:30"};
            String[] columns = {"Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            // Populate the model
            for (String timeSlot : timeSlots) {
                Object[] rowData = new Object[7];
                rowData[0] = timeSlot;

                Map<String, String> dayData = timetableMap.get(timeSlot);
                for (int i = 1; i < columns.length; i++) {
                    String day = columns[i];
                    if (dayData != null && dayData.containsKey(day)) {
                        rowData[i] = dayData.get(day);
                    } else {
                        rowData[i] = "Free";
                    }
                }
                model.addRow(rowData);
            }

            // Update the existing table with new data
            if (timetableTable != null) {
                timetableTable.setModel(model);
                setupTableAppearance();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading timetable: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private JPanel createSyllabusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Course Syllabus");
        headerLabel.setFont(Theme.HEADER_FONT);
        headerLabel.setForeground(new Color(138, 43, 226));
        panel.add(headerLabel, BorderLayout.NORTH);

        // Table columns
        String[] columns = {"Subject Code", "Subject Name", "Unit Number", "Unit Title", "Unit Content"};
        DefaultTableModel syllabusTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable syllabusTable = new JTable(syllabusTableModel);
        syllabusTable.setRowHeight(60);
        syllabusTable.setFont(Theme.NORMAL_FONT);
        syllabusTable.getTableHeader().setFont(Theme.HEADER_FONT);
        syllabusTable.setSelectionBackground(new Color(138, 43, 226));
        syllabusTable.setSelectionForeground(Color.WHITE);
        JScrollPane syllabusScrollPane = new JScrollPane(syllabusTable);
        syllabusScrollPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        syllabusScrollPane.getViewport().setBackground(Color.WHITE);
        syllabusScrollPane.setBackground(Color.WHITE);
        panel.add(syllabusScrollPane, BorderLayout.CENTER);

        // Set preferred column widths
        syllabusTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Subject Code
        syllabusTable.getColumnModel().getColumn(1).setPreferredWidth(180); // Subject Name
        syllabusTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Unit Number
        syllabusTable.getColumnModel().getColumn(3).setPreferredWidth(220); // Unit Title
        syllabusTable.getColumnModel().getColumn(4).setPreferredWidth(500); // Unit Content

        // Custom cell renderer for coloring and merging subject code and name
        DefaultTableCellRenderer subjectMergeRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                // Find the first non-empty value above (for merging effect)
                Object displayValue = value;
                if (value == null || value.toString().isEmpty()) {
                    // Check if above rows have the same subject (simulate merged cell)
                    for (int r = row - 1; r >= 0; r--) {
                        Object above = table.getValueAt(r, column);
                        if (above != null && !above.toString().isEmpty()) {
                            displayValue = ""; // Don't show anything for merged effect
                            break;
                        }
                    }
                }
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, displayValue, isSelected, hasFocus, row, column);
                label.setBackground(new Color(220, 235, 255));
                label.setForeground(Color.BLACK);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setVerticalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setForeground(table.getSelectionForeground());
                }
                // Remove border for merged effect if cell is empty
                if (displayValue == null || displayValue.toString().isEmpty()) {
                    label.setBorder(BorderFactory.createEmptyBorder());
                }
                return label;
            }
        };
        syllabusTable.getColumnModel().getColumn(0).setCellRenderer(subjectMergeRenderer); // Subject Code
        syllabusTable.getColumnModel().getColumn(1).setCellRenderer(subjectMergeRenderer); // Subject Name

        // Custom cell renderer for wrapping text in Unit Title and Unit Content
        DefaultTableCellRenderer wrapRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JTextArea area = new JTextArea();
                area.setLineWrap(true);
                area.setWrapStyleWord(true);
                area.setText(value == null ? "" : value.toString());
                area.setFont(table.getFont());
                area.setOpaque(true);
                if (isSelected) {
                    area.setBackground(table.getSelectionBackground());
                    area.setForeground(table.getSelectionForeground());
                } else {
                    area.setBackground(table.getBackground());
                    area.setForeground(table.getForeground());
                }
                area.setBorder(null);
                return area;
            }
        };
        syllabusTable.getColumnModel().getColumn(3).setCellRenderer(wrapRenderer); // Unit Title
        syllabusTable.getColumnModel().getColumn(4).setCellRenderer(wrapRenderer); // Unit Content

        JButton refreshButton = new JButton("Refresh Syllabus");
        refreshButton.setBackground(new Color(70, 130, 180));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        Runnable loadSyllabus = () -> {
            syllabusTableModel.setRowCount(0);
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT s.subject_code, s.subject_name, y.unit_number, y.unit_title, y.unit_content\n" +
                        "FROM syllabus y\n" +
                        "JOIN subjects s ON y.subject_id = s.subject_id\n" +
                        "ORDER BY s.subject_code, y.unit_number";
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery();
                String lastSubjectCode = "";
                while (rs.next()) {
                    String subjectCode = rs.getString("subject_code");
                    String subjectName = rs.getString("subject_name");
                    int unitNumber = rs.getInt("unit_number");
                    String unitTitle = rs.getString("unit_title");
                    String unitContent = rs.getString("unit_content");
                    // Only show subject code and name for the first unit of each subject
                    String showSubjectCode = subjectCode.equals(lastSubjectCode) ? "" : subjectCode;
                    String showSubjectName = subjectCode.equals(lastSubjectCode) ? "" : subjectName;
                    syllabusTableModel.addRow(new Object[]{showSubjectCode, showSubjectName, unitNumber, unitTitle, unitContent});
                    lastSubjectCode = subjectCode;
                }
                if (syllabusTableModel.getRowCount() == 0) {
                    syllabusTableModel.addRow(new Object[]{"No data", "", "", "", ""});
                }
            } catch (SQLException ex) {
                syllabusTableModel.setRowCount(0);
                syllabusTableModel.addRow(new Object[]{"Error loading syllabus: " + ex.getMessage(), "", "", "", ""});
            }
        };
        loadSyllabus.run();
        refreshButton.addActionListener(e -> loadSyllabus.run());

        return panel;
    }

    private void loadUserData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM students WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();


        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading user data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
