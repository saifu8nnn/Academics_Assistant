package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.sql.*;
import database.DatabaseConnection;
import javax.swing.table.DefaultTableModel;
import javax.swing.ImageIcon;
public class TeacherDashboard extends JFrame {
    private String userId;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel menuPanel;
    private boolean isMenuVisible = true;
    private JButton selectedButton = null;
    private JComboBox<String> semesterCombo;
    private DefaultTableModel gradesTableModel;
    private JTable gradesTable;

    public TeacherDashboard(String userId) {
        this.userId = userId;
        setTitle("Ilmly-Teacher Dashboard");
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
        contentPanel.add(createSyllabusPanel(), "ADD SYLLABUS");
        contentPanel.add(createGradesPanel(), "ADD GRADES");
        contentPanel.add(createAttendancePanel(), "ADD ATTENDANCE");

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
            SettingsDialog settingsDialog = new SettingsDialog(this, userId, "Teacher");
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

        String[] menuItems = {"Profile", "Add Syllabus", "Add Grades", "Add Attendance"};
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
        JLabel welcomeLabel = new JLabel("Hi " + getTeacherName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(welcomeLabel);
        panel.add(Box.createVerticalStrut(30));

        // Profile image placeholder
        JLabel imageLabel = new JLabel("👨‍🏫");
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

        JLabel nameLabel = new JLabel("Name: " + getTeacherName());
        nameLabel.setFont(Theme.NORMAL_FONT);
        nameLabel.setForeground(Color.WHITE);
        detailsPanel.add(nameLabel);

        JLabel idLabel = new JLabel("Teacher ID: " + userId);
        idLabel.setFont(Theme.NORMAL_FONT);
        idLabel.setForeground(Color.WHITE);
        detailsPanel.add(idLabel);

        panel.add(detailsPanel);

        return panel;
    }

    private String getTeacherName() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT name FROM teachers WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "Teacher";
    }

    private JPanel createSyllabusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JLabel headerLabel = new JLabel("Add Syllabus");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(new Color(138, 43, 226));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 16, 0));
        panel.add(headerLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Theme.BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        // Controls panel for alignment
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setBackground(Theme.BACKGROUND_COLOR);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 16, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 16);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;
        gbc.gridx = 0;

        JLabel semesterLabel = new JLabel("Select Semester:");
        semesterLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        semesterLabel.setForeground(new Color(180, 180, 255));
        controlsPanel.add(semesterLabel, gbc);
        gbc.gridx++;
        JComboBox<String> syllabusSemesterCombo = new JComboBox<>(new String[]{"Semester 1", "Semester 2", "Semester 3", "Semester 4"});
        Theme.styleComboBox(syllabusSemesterCombo);
        syllabusSemesterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        controlsPanel.add(syllabusSemesterCombo, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        JLabel subjectLabel = new JLabel("Select Subject:");
        subjectLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        subjectLabel.setForeground(new Color(180, 180, 255));
        controlsPanel.add(subjectLabel, gbc);
        gbc.gridx++;
        JComboBox<String> subjectCombo = new JComboBox<>();
        Theme.styleComboBox(subjectCombo);
        subjectCombo.setMaximumRowCount(8);
        subjectCombo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        controlsPanel.add(subjectCombo, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        JLabel unitNumberLabel = new JLabel("Unit Number:");
        unitNumberLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        unitNumberLabel.setForeground(new Color(180, 180, 255));
        controlsPanel.add(unitNumberLabel, gbc);
        gbc.gridx++;
        JTextField unitNumberField = new JTextField(5);
        unitNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        controlsPanel.add(unitNumberField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        JLabel unitTitleLabel = new JLabel("Unit Title:");
        unitTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        unitTitleLabel.setForeground(new Color(180, 180, 255));
        controlsPanel.add(unitTitleLabel, gbc);
        gbc.gridx++;
        JTextField unitTitleField = new JTextField(20);
        unitTitleField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        controlsPanel.add(unitTitleField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        JLabel unitContentLabel = new JLabel("Unit Content:");
        unitContentLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        unitContentLabel.setForeground(new Color(180, 180, 255));
        controlsPanel.add(unitContentLabel, gbc);
        gbc.gridx++;
        JTextArea unitContentArea = new JTextArea(4, 30);
        unitContentArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        unitContentArea.setLineWrap(true);
        unitContentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(unitContentArea);
        controlsPanel.add(contentScroll, gbc);

        contentPanel.add(controlsPanel, BorderLayout.NORTH);
        contentPanel.add(Box.createVerticalStrut(16));

        JButton saveButton = new JButton("Save Syllabus");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        saveButton.setBackground(Color.WHITE);
        saveButton.setForeground(new Color(138, 43, 226));
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(10, 32, 10, 32));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                saveButton.setBackground(new Color(220, 220, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                saveButton.setBackground(Color.WHITE);
            }
        });
        saveButton.addActionListener(e -> {
            int semester = syllabusSemesterCombo.getSelectedIndex() + 1;
            String subjectCode = (String) subjectCombo.getSelectedItem();
            String unitNumber = unitNumberField.getText().trim();
            String unitTitle = unitTitleField.getText().trim();
            String unitContent = unitContentArea.getText().trim();
            if (subjectCode == null || unitNumber.isEmpty() || unitTitle.isEmpty() || unitContent.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement subStmt = conn.prepareStatement("SELECT subject_id FROM subjects WHERE semester = ? AND subject_code = ?");
                subStmt.setInt(1, semester);
                subStmt.setString(2, subjectCode);
                ResultSet subRs = subStmt.executeQuery();
                int subjectId = -1;
                if (subRs.next()) subjectId = subRs.getInt("subject_id");
                subRs.close();
                subStmt.close();
                if (subjectId == -1) {
                    JOptionPane.showMessageDialog(this, "Subject not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO syllabus (subject_id, unit_number, unit_title, unit_content) VALUES (?, ?, ?, ?)");
                insertStmt.setInt(1, subjectId);
                insertStmt.setInt(2, Integer.parseInt(unitNumber));
                insertStmt.setString(3, unitTitle);
                insertStmt.setString(4, unitContent);
                insertStmt.executeUpdate();
                insertStmt.close();
                JOptionPane.showMessageDialog(this, "Syllabus unit saved successfully!");
                unitNumberField.setText("");
                unitTitleField.setText("");
                unitContentArea.setText("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error saving syllabus: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Theme.BACKGROUND_COLOR);
        buttonPanel.add(saveButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Listeners
        syllabusSemesterCombo.addActionListener(e -> updateSyllabusSubjectCombo(syllabusSemesterCombo, subjectCombo));
        // Initial load
        updateSyllabusSubjectCombo(syllabusSemesterCombo, subjectCombo);

        return panel;
    }

    private void updateSyllabusSubjectCombo(JComboBox<String> semesterCombo, JComboBox<String> subjectCombo) {
        subjectCombo.removeAllItems();
        int semester = semesterCombo.getSelectedIndex() + 1;
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement subStmt = conn.prepareStatement("SELECT subject_code FROM subjects WHERE semester = ?");
            subStmt.setInt(1, semester);
            ResultSet subRs = subStmt.executeQuery();
            while (subRs.next()) {
                subjectCombo.addItem(subRs.getString("subject_code"));
            }
            subRs.close();
            subStmt.close();
        } catch (SQLException ex) {
            // Error loading subjects
        }
        if (subjectCombo.getItemCount() > 0) {
            subjectCombo.setSelectedIndex(0);
        }
    }

    private JPanel createGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JLabel headerLabel = new JLabel("Add Grades");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(new Color(138, 43, 226));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 16, 0));
        panel.add(headerLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Theme.BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        // Controls panel for alignment
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setBackground(Theme.BACKGROUND_COLOR);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 16, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 16);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;
        gbc.gridx = 0;

        JLabel semesterLabel = new JLabel("Select Semester:");
        semesterLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        semesterLabel.setForeground(new Color(180, 180, 255));
        controlsPanel.add(semesterLabel, gbc);
        gbc.gridx++;
        JComboBox<String> gradesSemesterCombo = new JComboBox<>(new String[]{"Semester 1", "Semester 2", "Semester 3", "Semester 4"});
        Theme.styleComboBox(gradesSemesterCombo);
        gradesSemesterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        controlsPanel.add(gradesSemesterCombo, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        JLabel subjectLabel = new JLabel("Select Subject:");
        subjectLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        subjectLabel.setForeground(new Color(180, 180, 255));
        controlsPanel.add(subjectLabel, gbc);
        gbc.gridx++;
        JComboBox<String> subjectCombo = new JComboBox<>();
        Theme.styleComboBox(subjectCombo);
        subjectCombo.setMaximumRowCount(8);
        subjectCombo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        controlsPanel.add(subjectCombo, gbc);

        contentPanel.add(controlsPanel);
        contentPanel.add(Box.createVerticalStrut(16));

        // Card-like panel for the table
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(new Color(30, 20, 60));
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(138, 43, 226), 2, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        gradesTableModel = new DefaultTableModel();
        gradesTable = new JTable(gradesTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2;
            }
        };
        gradesTable.setFillsViewportHeight(true);
        gradesTable.setRowHeight(32);
        gradesTable.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gradesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
        gradesTable.getTableHeader().setBackground(new Color(70, 130, 180));
        gradesTable.getTableHeader().setForeground(Color.BLACK);
        gradesTable.setSelectionBackground(new Color(138, 43, 226));
        gradesTable.setSelectionForeground(Color.WHITE);
        gradesTable.setGridColor(new Color(90, 90, 120));
        gradesTable.setShowHorizontalLines(true);
        gradesTable.setShowVerticalLines(false);
        gradesTable.setIntercellSpacing(new Dimension(0, 2));
        gradesTable.setBorder(BorderFactory.createEmptyBorder());
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(30, 20, 60));
        tableCard.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(tableCard);
        contentPanel.add(Box.createVerticalStrut(16));

        JButton saveButton = new JButton("Save Grades");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        saveButton.setBackground(Color.WHITE);
        saveButton.setForeground(new Color(138, 43, 226));
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(10, 32, 10, 32));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                saveButton.setBackground(new Color(160, 60, 240));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                saveButton.setBackground(new Color(138, 43, 226));
            }
        });
        saveButton.addActionListener(e -> saveGradesToDatabase(gradesSemesterCombo, subjectCombo));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Theme.BACKGROUND_COLOR);
        buttonPanel.add(saveButton);
        contentPanel.add(buttonPanel);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Listeners with recursion guard
        final boolean[] ignoreSemesterListener = {false};
        final boolean[] ignoreSubjectListener = {false};

        gradesSemesterCombo.addActionListener(e -> {
            if (ignoreSemesterListener[0]) return;
            ignoreSemesterListener[0] = true;
            updateSubjectCombo(gradesSemesterCombo, subjectCombo);
            ignoreSemesterListener[0] = false;
        });
        subjectCombo.addActionListener(e -> {
            if (ignoreSubjectListener[0]) return;
            ignoreSubjectListener[0] = true;
            loadGradesTableForSemesterAndSubject(gradesSemesterCombo, subjectCombo);
            ignoreSubjectListener[0] = false;
        });

        // Initial load
        updateSubjectCombo(gradesSemesterCombo, subjectCombo);

        return panel;
    }

    private void updateSubjectCombo(JComboBox<String> gradesSemesterCombo, JComboBox<String> subjectCombo) {
        subjectCombo.removeAllItems();
        int semester = gradesSemesterCombo.getSelectedIndex() + 1;
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement subStmt = conn.prepareStatement("SELECT subject_code FROM subjects WHERE semester = ?");
            subStmt.setInt(1, semester);
            ResultSet subRs = subStmt.executeQuery();
            boolean found = false;
            while (subRs.next()) {
                subjectCombo.addItem(subRs.getString("subject_code"));
                found = true;
            }
            subRs.close();
            subStmt.close();
            if (!found) {
                gradesTableModel.setDataVector(new Object[][]{{"No subjects found for this semester."}}, new Object[]{"Info"});
            }
        } catch (SQLException ex) {
            gradesTableModel.setDataVector(new Object[][]{{"Error loading subjects: " + ex.getMessage()}}, new Object[]{"Error"});
        }
        // Trigger table load for the first subject
        if (subjectCombo.getItemCount() > 0) {
            subjectCombo.setSelectedIndex(0);
            loadGradesTableForSemesterAndSubject(gradesSemesterCombo, subjectCombo);
        }
    }

    private void loadGradesTableForSemesterAndSubject(JComboBox<String> gradesSemesterCombo, JComboBox<String> subjectCombo) {
        int semester = gradesSemesterCombo.getSelectedIndex() + 1;
        String subjectCode = (String) subjectCombo.getSelectedItem();
        if (subjectCode == null) {
            gradesTableModel.setDataVector(new Object[][]{{"No subject selected."}}, new Object[]{"Info"});
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get subject_id
            PreparedStatement subStmt = conn.prepareStatement("SELECT subject_id FROM subjects WHERE semester = ? AND subject_code = ?");
            subStmt.setInt(1, semester);
            subStmt.setString(2, subjectCode);
            ResultSet subRs = subStmt.executeQuery();
            int subjectId = -1;
            if (subRs.next()) subjectId = subRs.getInt("subject_id");
            subRs.close();
            subStmt.close();
            if (subjectId == -1) {
                gradesTableModel.setDataVector(new Object[][]{{"Subject not found."}}, new Object[]{"Info"});
                return;
            }
            // Get students
            PreparedStatement stuStmt = conn.prepareStatement("SELECT user_id, name, enrollment FROM students WHERE semester = ?");
            stuStmt.setInt(1, semester);
            ResultSet stuRs = stuStmt.executeQuery();
            gradesTableModel.setDataVector(new Object[0][0], new String[]{
                    "Enrollment", "Name", "MST 1", "MST 2", "Unit Test 1", "Unit Test 2", "Unit Test 3", "Unit Test 4", "Unit Test 5", "Main Exam"
            });
            boolean found = false;
            while (stuRs.next()) {
                found = true;
                Object[] row = new Object[10];
                row[0] = stuRs.getString("enrollment");
                row[1] = stuRs.getString("name");
                String userId = stuRs.getString("user_id");
                PreparedStatement gradeStmt = conn.prepareStatement(
                        "SELECT mst1, mst2, unit_test1, unit_test2, unit_test3, unit_test4, unit_test5, main_exam FROM grades WHERE user_id = ? AND subject_id = ?");
                gradeStmt.setString(1, userId);
                gradeStmt.setInt(2, subjectId);
                ResultSet gradeRs = gradeStmt.executeQuery();
                if (gradeRs.next()) {
                    row[2] = gradeRs.getString("mst1");
                    row[3] = gradeRs.getString("mst2");
                    row[4] = gradeRs.getString("unit_test1");
                    row[5] = gradeRs.getString("unit_test2");
                    row[6] = gradeRs.getString("unit_test3");
                    row[7] = gradeRs.getString("unit_test4");
                    row[8] = gradeRs.getString("unit_test5");
                    row[9] = gradeRs.getString("main_exam");
                } else {
                    for (int i = 2; i < 10; i++) row[i] = "";
                }
                gradeRs.close();
                gradeStmt.close();
                gradesTableModel.addRow(row);
            }
            stuRs.close();
            stuStmt.close();
            if (!found) {
                gradesTableModel.addRow(new Object[]{"No students found for this semester.", "", "", "", "", "", "", "", "", ""});
            }
        } catch (SQLException ex) {
            gradesTableModel.setDataVector(new Object[][]{{"Error loading data: " + ex.getMessage()}}, new Object[]{"Error"});
        }
    }

    private void saveGradesToDatabase(JComboBox<String> gradesSemesterCombo, JComboBox<String> subjectCombo) {
        int semester = gradesSemesterCombo.getSelectedIndex() + 1;
        String subjectCode = (String) subjectCombo.getSelectedItem();
        if (subjectCode == null) return;
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get subject_id
            PreparedStatement subStmt = conn.prepareStatement("SELECT subject_id FROM subjects WHERE semester = ? AND subject_code = ?");
            subStmt.setInt(1, semester);
            subStmt.setString(2, subjectCode);
            ResultSet subRs = subStmt.executeQuery();
            int subjectId = -1;
            if (subRs.next()) subjectId = subRs.getInt("subject_id");
            subRs.close();
            subStmt.close();
            if (subjectId == -1) return;
            for (int row = 0; row < gradesTableModel.getRowCount(); row++) {
                String enrollment = (String) gradesTableModel.getValueAt(row, 0);
                PreparedStatement stuStmt = conn.prepareStatement("SELECT user_id FROM students WHERE enrollment = ?");
                stuStmt.setString(1, enrollment);
                ResultSet stuRs = stuStmt.executeQuery();
                String userId = null;
                if (stuRs.next()) userId = stuRs.getString("user_id");
                stuRs.close();
                stuStmt.close();
                if (userId == null) continue;
                String mst1 = (String) gradesTableModel.getValueAt(row, 2);
                String mst2 = (String) gradesTableModel.getValueAt(row, 3);
                String ut1 = (String) gradesTableModel.getValueAt(row, 4);
                String ut2 = (String) gradesTableModel.getValueAt(row, 5);
                String ut3 = (String) gradesTableModel.getValueAt(row, 6);
                String ut4 = (String) gradesTableModel.getValueAt(row, 7);
                String ut5 = (String) gradesTableModel.getValueAt(row, 8);
                String mainExam = (String) gradesTableModel.getValueAt(row, 9);
                PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM grades WHERE user_id = ? AND subject_id = ?");
                checkStmt.setString(1, userId);
                checkStmt.setInt(2, subjectId);
                ResultSet checkRs = checkStmt.executeQuery();
                if (checkRs.next()) {
                    PreparedStatement updateStmt = conn.prepareStatement(
                            "UPDATE grades SET mst1 = ?, mst2 = ?, unit_test1 = ?, unit_test2 = ?, unit_test3 = ?, unit_test4 = ?, unit_test5 = ?, main_exam = ? WHERE id = ?");
                    updateStmt.setString(1, mst1);
                    updateStmt.setString(2, mst2);
                    updateStmt.setString(3, ut1);
                    updateStmt.setString(4, ut2);
                    updateStmt.setString(5, ut3);
                    updateStmt.setString(6, ut4);
                    updateStmt.setString(7, ut5);
                    updateStmt.setString(8, mainExam);
                    updateStmt.setInt(9, checkRs.getInt("id"));
                    updateStmt.executeUpdate();
                    updateStmt.close();
                } else {
                    PreparedStatement insertStmt = conn.prepareStatement(
                            "INSERT INTO grades (user_id, subject_id, semester, mst1, mst2, unit_test1, unit_test2, unit_test3, unit_test4, unit_test5, main_exam) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    insertStmt.setString(1, userId);
                    insertStmt.setInt(2, subjectId);
                    insertStmt.setInt(3, semester);
                    insertStmt.setString(4, mst1);
                    insertStmt.setString(5, mst2);
                    insertStmt.setString(6, ut1);
                    insertStmt.setString(7, ut2);
                    insertStmt.setString(8, ut3);
                    insertStmt.setString(9, ut4);
                    insertStmt.setString(10, ut5);
                    insertStmt.setString(11, mainExam);
                    insertStmt.executeUpdate();
                    insertStmt.close();
                }
                checkRs.close();
                checkStmt.close();
            }
            JOptionPane.showMessageDialog(this, "Grades saved successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving grades: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JLabel headerLabel = new JLabel("Add Attendance");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(new Color(138, 43, 226));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 16, 0));
        panel.add(headerLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Theme.BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        // Controls panel for alignment
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setBackground(Theme.BACKGROUND_COLOR);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 16, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 16);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;
        gbc.gridx = 0;

        JLabel semesterLabel = new JLabel("Select Semester:");
        semesterLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        semesterLabel.setForeground(new Color(180, 180, 255));
        controlsPanel.add(semesterLabel, gbc);
        gbc.gridx++;
        JComboBox<String> attendanceSemesterCombo = new JComboBox<>(new String[]{"Semester 1", "Semester 2", "Semester 3", "Semester 4"});
        Theme.styleComboBox(attendanceSemesterCombo);
        attendanceSemesterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        controlsPanel.add(attendanceSemesterCombo, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        JLabel subjectLabel = new JLabel("Select Subject:");
        subjectLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        subjectLabel.setForeground(new Color(180, 180, 255));
        controlsPanel.add(subjectLabel, gbc);
        gbc.gridx++;
        JComboBox<String> subjectCombo = new JComboBox<>();
        Theme.styleComboBox(subjectCombo);
        subjectCombo.setMaximumRowCount(8);
        subjectCombo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        controlsPanel.add(subjectCombo, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        JLabel dateLabel = new JLabel("Select Date:");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dateLabel.setForeground(new Color(180, 180, 255));
        controlsPanel.add(dateLabel, gbc);
        gbc.gridx++;
        JTextField dateField = new JTextField(10);
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        dateField.setText(java.time.LocalDate.now().toString());
        controlsPanel.add(dateField, gbc);

        contentPanel.add(controlsPanel);
        contentPanel.add(Box.createVerticalStrut(16));

        // Card-like panel for the table
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(new Color(30, 20, 60));
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(138, 43, 226), 2, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        DefaultTableModel attendanceTableModel = new DefaultTableModel();
        JTable attendanceTable = new JTable(attendanceTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only allow editing of the Status column
                return column == 2;
            }
        };
        attendanceTable.setFillsViewportHeight(true);
        attendanceTable.setRowHeight(32);
        attendanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        attendanceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
        attendanceTable.getTableHeader().setBackground(new Color(70, 130, 180));
        attendanceTable.getTableHeader().setForeground(Color.BLACK);
        attendanceTable.setSelectionBackground(new Color(138, 43, 226));
        attendanceTable.setSelectionForeground(Color.WHITE);
        attendanceTable.setGridColor(new Color(90, 90, 120));
        attendanceTable.setShowHorizontalLines(true);
        attendanceTable.setShowVerticalLines(false);
        attendanceTable.setIntercellSpacing(new Dimension(0, 2));
        attendanceTable.setBorder(BorderFactory.createEmptyBorder());
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(30, 20, 60));
        tableCard.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(tableCard);
        contentPanel.add(Box.createVerticalStrut(16));

        JButton saveButton = new JButton("Save Attendance");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        saveButton.setBackground(Color.WHITE);
        saveButton.setForeground(new Color(138, 43, 226));
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(10, 32, 10, 32));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                saveButton.setBackground(new Color(220, 220, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                saveButton.setBackground(Color.WHITE);
            }
        });
        saveButton.addActionListener(e -> saveAttendanceToDatabase(attendanceSemesterCombo, subjectCombo, dateField, attendanceTableModel));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Theme.BACKGROUND_COLOR);
        buttonPanel.add(saveButton);
        contentPanel.add(buttonPanel);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Listeners with recursion guard
        final boolean[] ignoreSemesterListener = {false};
        final boolean[] ignoreSubjectListener = {false};

        attendanceSemesterCombo.addActionListener(e -> {
            if (ignoreSemesterListener[0]) return;
            ignoreSemesterListener[0] = true;
            updateAttendanceSubjectCombo(attendanceSemesterCombo, subjectCombo);
            ignoreSemesterListener[0] = false;
        });
        subjectCombo.addActionListener(e -> {
            if (ignoreSubjectListener[0]) return;
            ignoreSubjectListener[0] = true;
            loadAttendanceTableForSemesterAndSubject(attendanceSemesterCombo, subjectCombo, attendanceTableModel);
            ignoreSubjectListener[0] = false;
        });
        dateField.addActionListener(e -> loadAttendanceTableForSemesterAndSubject(attendanceSemesterCombo, subjectCombo, attendanceTableModel));

        // Initial load
        updateAttendanceSubjectCombo(attendanceSemesterCombo, subjectCombo);

        return panel;
    }

    private void updateAttendanceSubjectCombo(JComboBox<String> semesterCombo, JComboBox<String> subjectCombo) {
        subjectCombo.removeAllItems();
        int semester = semesterCombo.getSelectedIndex() + 1;
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement subStmt = conn.prepareStatement("SELECT subject_code FROM subjects WHERE semester = ?");
            subStmt.setInt(1, semester);
            ResultSet subRs = subStmt.executeQuery();
            boolean found = false;
            while (subRs.next()) {
                subjectCombo.addItem(subRs.getString("subject_code"));
                found = true;
            }
            subRs.close();
            subStmt.close();
            if (!found) {
                // No subjects found
            }
        } catch (SQLException ex) {
            // Error loading subjects
        }
        // Trigger table load for the first subject
        if (subjectCombo.getItemCount() > 0) {
            subjectCombo.setSelectedIndex(0);
            // Table will load via subjectCombo listener
        }
    }

    private void loadAttendanceTableForSemesterAndSubject(JComboBox<String> semesterCombo, JComboBox<String> subjectCombo, DefaultTableModel attendanceTableModel) {
        int semester = semesterCombo.getSelectedIndex() + 1;
        String subjectCode = (String) subjectCombo.getSelectedItem();
        if (subjectCode == null) {
            attendanceTableModel.setDataVector(new Object[][]{{"No subject selected."}}, new Object[]{"Info"});
            return;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get subject_id
            PreparedStatement subStmt = conn.prepareStatement("SELECT subject_id FROM subjects WHERE semester = ? AND subject_code = ?");
            subStmt.setInt(1, semester);
            subStmt.setString(2, subjectCode);
            ResultSet subRs = subStmt.executeQuery();
            int subjectId = -1;
            if (subRs.next()) subjectId = subRs.getInt("subject_id");
            subRs.close();
            subStmt.close();
            if (subjectId == -1) {
                attendanceTableModel.setDataVector(new Object[][]{{"Subject not found."}}, new Object[]{"Info"});
                return;
            }
            // Get students
            PreparedStatement stuStmt = conn.prepareStatement("SELECT user_id, name, enrollment FROM students WHERE semester = ?");
            stuStmt.setInt(1, semester);
            ResultSet stuRs = stuStmt.executeQuery();
            attendanceTableModel.setDataVector(new Object[0][0], new String[]{"Enrollment", "Name", "Status"});
            boolean found = false;
            while (stuRs.next()) {
                found = true;
                Object[] row = new Object[3];
                row[0] = stuRs.getString("enrollment");
                row[1] = stuRs.getString("name");
                row[2] = "Present"; // Default to Present
                attendanceTableModel.addRow(row);
            }
            stuRs.close();
            stuStmt.close();
            if (!found) {
                attendanceTableModel.addRow(new Object[]{"No students found for this semester.", "", ""});
            }
        } catch (SQLException ex) {
            attendanceTableModel.setDataVector(new Object[][]{{"Error loading data: " + ex.getMessage()}}, new Object[]{"Error"});
        }
    }

    private void saveAttendanceToDatabase(JComboBox<String> semesterCombo, JComboBox<String> subjectCombo, JTextField dateField, DefaultTableModel attendanceTableModel) {
        int semester = semesterCombo.getSelectedIndex() + 1;
        String subjectCode = (String) subjectCombo.getSelectedItem();
        String date = dateField.getText();
        if (subjectCode == null || date == null || date.isEmpty()) return;
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get subject_id
            PreparedStatement subStmt = conn.prepareStatement("SELECT subject_id FROM subjects WHERE semester = ? AND subject_code = ?");
            subStmt.setInt(1, semester);
            subStmt.setString(2, subjectCode);
            ResultSet subRs = subStmt.executeQuery();
            int subjectId = -1;
            if (subRs.next()) subjectId = subRs.getInt("subject_id");
            subRs.close();
            subStmt.close();
            if (subjectId == -1) return;
            for (int row = 0; row < attendanceTableModel.getRowCount(); row++) {
                String enrollment = (String) attendanceTableModel.getValueAt(row, 0);
                PreparedStatement stuStmt = conn.prepareStatement("SELECT user_id FROM students WHERE enrollment = ?");
                stuStmt.setString(1, enrollment);
                ResultSet stuRs = stuStmt.executeQuery();
                String userId = null;
                if (stuRs.next()) userId = stuRs.getString("user_id");
                stuRs.close();
                stuStmt.close();
                if (userId == null) continue;
                String status = (String) attendanceTableModel.getValueAt(row, 2);
                // Upsert attendance for this date, subject, student
                PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM attendance WHERE user_id = ? AND subject_id = ? AND date = ?");
                checkStmt.setString(1, userId);
                checkStmt.setInt(2, subjectId);
                checkStmt.setString(3, date);
                ResultSet checkRs = checkStmt.executeQuery();
                if (checkRs.next()) {
                    PreparedStatement updateStmt = conn.prepareStatement("UPDATE attendance SET status = ? WHERE id = ?");
                    updateStmt.setString(1, status);
                    updateStmt.setInt(2, checkRs.getInt("id"));
                    updateStmt.executeUpdate();
                    updateStmt.close();
                } else {
                    PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO attendance (user_id, subject_id, semester, date, status) VALUES (?, ?, ?, ?, ?)");
                    insertStmt.setString(1, userId);
                    insertStmt.setInt(2, subjectId);
                    insertStmt.setInt(3, semester);
                    insertStmt.setString(4, date);
                    insertStmt.setString(5, status);
                    insertStmt.executeUpdate();
                    insertStmt.close();
                }
                checkRs.close();
                checkStmt.close();
            }
            JOptionPane.showMessageDialog(this, "Attendance saved successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving attendance: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
}