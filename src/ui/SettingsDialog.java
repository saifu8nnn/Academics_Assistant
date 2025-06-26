package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.*;
import java.sql.*;
import database.DatabaseConnection;

public class SettingsDialog extends JDialog {
    private final JFrame parent;
    private final String userId;
    private final String userType;
    private JTabbedPane tabbedPane;
    private JPasswordField currentPasswordField, newPasswordField, confirmPasswordField;
    private JButton profilePhotoButton;
    private JLabel profilePhotoLabel;
    private boolean isDarkTheme;
    private String currentProfilePhotoPath;

    public SettingsDialog(JFrame parent, String userId, String userType) {
        super(parent, "Settings", true);
        this.parent = parent;
        this.userId = userId;
        this.userType = userType;

        setSize(500, 450);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(10, 10, 30));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(10, 10, 30));
        tabbedPane.setForeground(Color.BLACK);

        tabbedPane.addTab("Account", createAccountPanel());
        tabbedPane.addTab("About", createAboutPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);

        // Add a prominent Logout button at the bottom of mainPanel (inside the panel)
        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton);
        logoutButton.setBackground(new Color(138, 43, 226));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoutButton.setFocusPainted(false);
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.addActionListener(e -> logout());
        JPanel logoutPanel = new JPanel();
        logoutPanel.setBackground(new Color(10, 10, 30));
        logoutPanel.setLayout(new BoxLayout(logoutPanel, BoxLayout.Y_AXIS));
        logoutPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        logoutPanel.add(logoutButton);
        mainPanel.add(logoutPanel, BorderLayout.SOUTH);

        loadCurrentSettings();
    }

    private void loadCurrentSettings() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Theme
            PreparedStatement themeStmt = conn.prepareStatement("SELECT theme, profile_photo_path FROM users WHERE user_id = ?");
            themeStmt.setString(1, userId);
            ResultSet themeRs = themeStmt.executeQuery();
            if (themeRs.next()) {
                isDarkTheme = !"light".equalsIgnoreCase(themeRs.getString("theme"));
                currentProfilePhotoPath = themeRs.getString("profile_photo_path");
                updateProfilePhotoLabel();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading settings: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createAccountPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(10, 10, 30));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Profile Photo
        JPanel photoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        photoPanel.setBackground(new Color(10, 10, 30));
        profilePhotoLabel = new JLabel("👤");
        profilePhotoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        profilePhotoLabel.setForeground(Color.WHITE);
        profilePhotoButton = new JButton("Change Photo");
        styleButton(profilePhotoButton);
        profilePhotoButton.addActionListener(e -> changeProfilePhoto());
        photoPanel.add(profilePhotoLabel);
        photoPanel.add(profilePhotoButton);
        panel.add(photoPanel);
        panel.add(Box.createVerticalStrut(20));

        // Change Password
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.setBackground(new Color(10, 10, 30));

        JLabel passwordTitle = new JLabel("Change Password");
        passwordTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        passwordTitle.setForeground(Color.WHITE);
        passwordPanel.add(passwordTitle);
        passwordPanel.add(Box.createVerticalStrut(10));

        currentPasswordField = new JPasswordField(15);
        newPasswordField = new JPasswordField(15);
        confirmPasswordField = new JPasswordField(15);

        passwordPanel.add(createLabeledField("Current Password:", currentPasswordField));
        passwordPanel.add(Box.createVerticalStrut(10));
        passwordPanel.add(createLabeledField("New Password:", newPasswordField));
        passwordPanel.add(Box.createVerticalStrut(10));
        passwordPanel.add(createLabeledField("Confirm Password:", confirmPasswordField));
        passwordPanel.add(Box.createVerticalStrut(10));

        JButton changePasswordButton = new JButton("Change Password");
        styleButton(changePasswordButton);
        changePasswordButton.addActionListener(e -> changePassword());
        passwordPanel.add(changePasswordButton);

        panel.add(passwordPanel);
        panel.add(Box.createVerticalStrut(20));

        // Theme Toggle
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        themePanel.setBackground(new Color(10, 10, 30));
        JButton themeButton = new JButton("Toggle Light/Dark Theme");
        styleButton(themeButton);
        themeButton.addActionListener(e -> toggleTheme());
        themePanel.add(themeButton);
        panel.add(themePanel);
        panel.add(Box.createVerticalStrut(20));

        // Logout and Delete Account
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.setBackground(new Color(10, 10, 30));
        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton);
        logoutButton.addActionListener(e -> logout());
        JButton deleteAccountButton = new JButton("Delete Account");
        styleButton(deleteAccountButton);
        deleteAccountButton.addActionListener(e -> deleteAccount());
        actionPanel.add(logoutButton);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(deleteAccountButton);
        panel.add(actionPanel);

        return panel;
    }

    private void changeProfilePhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".jpg") ||
                        f.getName().toLowerCase().endsWith(".jpeg") ||
                        f.getName().toLowerCase().endsWith(".png");
            }
            public String getDescription() {
                return "Image files (*.jpg, *.jpeg, *.png)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Create profile_photos directory in the user's home directory
                Path profilePhotosDir = Paths.get(System.getProperty("user.home"), "academic_assistant", "profile_photos");
                if (!Files.exists(profilePhotosDir)) {
                    Files.createDirectories(profilePhotosDir);
                }

                // Generate unique filename
                String newFileName = userId + "_" + System.currentTimeMillis() +
                        selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                Path targetPath = profilePhotosDir.resolve(newFileName);

                // Copy the file
                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // Update database
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String query = "UPDATE users SET profile_photo_path = ? WHERE user_id = ?";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, targetPath.toString());
                    pstmt.setString(2, userId);
                    pstmt.executeUpdate();

                    currentProfilePhotoPath = targetPath.toString();
                    updateProfilePhotoLabel();
                    JOptionPane.showMessageDialog(this, "Profile photo updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating profile photo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateProfilePhotoLabel() {
        if (currentProfilePhotoPath != null && !currentProfilePhotoPath.isEmpty()) {
            try {
                File photoFile = new File(currentProfilePhotoPath);
                if (photoFile.exists()) {
                    ImageIcon icon = new ImageIcon(currentProfilePhotoPath);
                    Image image = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                    profilePhotoLabel.setIcon(new ImageIcon(image));
                    profilePhotoLabel.setText("");
                } else {
                    profilePhotoLabel.setIcon(null);
                    profilePhotoLabel.setText("👤");
                }
            } catch (Exception ex) {
                profilePhotoLabel.setIcon(null);
                profilePhotoLabel.setText("👤");
            }
        } else {
            profilePhotoLabel.setIcon(null);
            profilePhotoLabel.setText("👤");
        }
    }

    private void changePassword() {
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // First verify current password
            String verifyQuery = "SELECT password FROM users WHERE user_id = ? AND password = ?";
            PreparedStatement verifyStmt = conn.prepareStatement(verifyQuery);
            verifyStmt.setString(1, userId);
            verifyStmt.setString(2, currentPassword);
            ResultSet rs = verifyStmt.executeQuery();

            if (rs.next()) {
                // Update password
                String updateQuery = "UPDATE users SET password = ? WHERE user_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, newPassword);
                updateStmt.setString(2, userId);
                updateStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearPasswordFields();
            } else {
                JOptionPane.showMessageDialog(this, "Current password is incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error changing password: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleTheme() {
        ThemeManager themeManager = ThemeManager.getInstance();
        themeManager.toggleTheme();
        themeManager.applyTheme(this);
        themeManager.applyTheme(getContentPane());

        JOptionPane.showMessageDialog(this,
                "Theme changed to " + (themeManager.isDarkTheme() ? "Dark" : "Light") + ".",
                "Theme Changed", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            parent.dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void deleteAccount() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete your account? This action cannot be undone!",
                "Confirm Account Deletion", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement userStmt = conn.prepareStatement("DELETE FROM users WHERE user_id = ?");
                userStmt.setString(1, userId);
                userStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Account deleted successfully!", "Account Deleted", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                parent.dispose();
                new LoginFrame().setVisible(true);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting account: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createLabeledField(String label, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(10, 10, 30));
        JLabel jLabel = new JLabel(label);
        jLabel.setForeground(Color.WHITE);
        panel.add(jLabel);
        panel.add(field);
        return panel;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(30, 20, 60));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(200, 30));
    }

    private void clearPasswordFields() {
        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    private JPanel createAboutPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(10, 10, 30));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("About You");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        String name = "", semester = "", email = "", attendance = "";
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (userType.equalsIgnoreCase("Student")) {
                PreparedStatement stmt = conn.prepareStatement("SELECT name, semester, email FROM students WHERE user_id = ?");
                stmt.setString(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    name = rs.getString("name");
                    semester = rs.getString("semester");
                    email = rs.getString("email");
                }
                rs.close();
                stmt.close();
                // Calculate overall attendance percentage
                stmt = conn.prepareStatement("SELECT COUNT(*) AS total, SUM(status = 'Present') AS present FROM attendance WHERE user_id = ?");
                stmt.setString(1, userId);
                rs = stmt.executeQuery();
                if (rs.next() && rs.getInt("total") > 0) {
                    int total = rs.getInt("total");
                    int present = rs.getInt("present");
                    double percent = (present * 100.0) / total;
                    attendance = String.format("%.1f%%", percent);
                } else {
                    attendance = "N/A";
                }
                rs.close();
                stmt.close();
            } else {
                PreparedStatement stmt = conn.prepareStatement("SELECT name, email FROM teachers WHERE user_id = ?");
                stmt.setString(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    name = rs.getString("name");
                    email = rs.getString("email");
                }
                rs.close();
                stmt.close();
                attendance = "N/A";
            }
        } catch (SQLException ex) {
            name = semester = email = attendance = "Error";
        }

        panel.add(createInfoLabel("Name: ", name));
        if (userType.equalsIgnoreCase("Student")) {
            panel.add(Box.createVerticalStrut(10));
            panel.add(createInfoLabel("Semester: ", semester));
            panel.add(Box.createVerticalStrut(10));
            panel.add(createInfoLabel("Overall Attendance: ", attendance));
        }
        panel.add(Box.createVerticalStrut(10));
        panel.add(createInfoLabel("Email: ", email));

        return panel;
    }

    private JPanel createInfoLabel(String label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(10, 10, 30));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(new Color(138, 43, 226));
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        v.setForeground(Color.WHITE);
        panel.add(l);
        panel.add(v);
        return panel;
    }
}