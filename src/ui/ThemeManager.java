package ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import database.DatabaseConnection;

public class ThemeManager {
    private static ThemeManager instance;
    private boolean isDarkTheme = true;
    private Color backgroundColor;
    private Color textColor;
    private Color panelColor;
    private Color buttonColor;
    private Color buttonTextColor;

    private ThemeManager() {
        loadTheme();
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    private void loadTheme() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT theme FROM users WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, getCurrentUserId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                isDarkTheme = "dark".equalsIgnoreCase(rs.getString("theme"));
            }
            updateColors();
        } catch (SQLException ex) {
            System.err.println("Error loading theme: " + ex.getMessage());
        }
    }

    private String getCurrentUserId() {
        // This should be implemented to get the current user's ID
        // For now, we'll use a default value
        return "current_user";
    }

    private void updateColors() {
        if (isDarkTheme) {
            backgroundColor = new Color(10, 10, 30);
            textColor = Color.WHITE;
            panelColor = new Color(20, 20, 40);
            buttonColor = new Color(30, 20, 60);
            buttonTextColor = Color.WHITE;
        } else {
            backgroundColor = new Color(240, 240, 240);
            textColor = new Color(44, 62, 80);
            panelColor = Color.WHITE;
            buttonColor = new Color(41, 128, 185);
            buttonTextColor = Color.WHITE;
        }
    }

    public void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        updateColors();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE users SET theme = ? WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, isDarkTheme ? "dark" : "light");
            pstmt.setString(2, getCurrentUserId());
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error saving theme: " + ex.getMessage());
        }
    }

    public void applyTheme(Component component) {
        if (component instanceof JPanel) {
            JPanel panel = (JPanel) component;
            panel.setBackground(panelColor);
            for (Component child : panel.getComponents()) {
                applyTheme(child);
            }
        } else if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            label.setForeground(textColor);
        } else if (component instanceof JButton) {
            JButton button = (JButton) component;
            button.setBackground(buttonColor);
            button.setForeground(buttonTextColor);
        } else if (component instanceof JTextField) {
            JTextField textField = (JTextField) component;
            textField.setBackground(panelColor);
            textField.setForeground(textColor);
        } else if (component instanceof JPasswordField) {
            JPasswordField passwordField = (JPasswordField) component;
            passwordField.setBackground(panelColor);
            passwordField.setForeground(textColor);
        } else if (component instanceof JComboBox) {
            JComboBox<?> comboBox = (JComboBox<?>) component;
            comboBox.setBackground(panelColor);
            comboBox.setForeground(textColor);
        } else if (component instanceof JTable) {
            JTable table = (JTable) component;
            table.setBackground(panelColor);
            table.setForeground(textColor);
            table.setGridColor(textColor);
        }
    }

    public boolean isDarkTheme() {
        return isDarkTheme;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public Color getPanelColor() {
        return panelColor;
    }

    public Color getButtonColor() {
        return buttonColor;
    }

    public Color getButtonTextColor() {
        return buttonTextColor;
    }
}