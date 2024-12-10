package screens;

import database.DatabaseConnector;
import database.UserDAO;
import models.User;
import utils.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class LoginScreenGUI extends JFrame {

    private JFrame parentFrame;

    public LoginScreenGUI(JFrame parent) {
        this.parentFrame = parent;

        setTitle("เข้าสู่ระบบ");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 250));
        panel.setLayout(new GridBagLayout());
        add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font kanitNormal = new Font("Kanit", Font.PLAIN, 14);
        Font kanitBold = new Font("Kanit", Font.BOLD, 14);

        JLabel userLabel = new JLabel("ชื่อผู้ใช้ :");
        userLabel.setFont(kanitNormal);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);
        JTextField userText = new JTextField(20);
        userText.setFont(kanitNormal);
        userText.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(userText, gbc);
        JLabel passwordLabel = new JLabel("รหัสผ่าน :");
        passwordLabel.setFont(kanitNormal);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);
        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setFont(kanitNormal);
        passwordText.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passwordText, gbc);
        JButton loginButton = new JButton("เข้าสู่ระบบ");
        styleButton(loginButton, new Dimension(200, 40), kanitBold,
                new Color(79, 134, 247), new Color(63, 118, 231));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 15, 15, 15);
        panel.add(loginButton, gbc);
        JButton backButton = new JButton("ย้อนกลับ");
        styleButton(backButton, new Dimension(200, 40), kanitBold,
                new Color(231, 76, 60), new Color(192, 57, 43));
        gbc.gridy = 3;
        gbc.insets = new Insets(15, 15, 15, 15);
        panel.add(backButton, gbc);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText().trim();
                String password = new String(passwordText.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginScreenGUI.this, "Please enter both username and password.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (Connection connection = DatabaseConnector.getConnection()) {
                    UserDAO userDAO = new UserDAO();
                    User user = userDAO.loginUser(connection, username, password);

                    if (user != null) {
                        JOptionPane.showMessageDialog(LoginScreenGUI.this,
                                "Login Successful! Welcome, " + user.getUsername() + ".", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        Session.setCurrentUser(user);
                        new MainScreenGUI(LoginScreenGUI.this).setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginScreenGUI.this,
                                "Invalid username or password. Please try again.", "Login Failed",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginScreenGUI.this, "Error during login: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parentFrame.setVisible(true);
                dispose();
            }
        });

        setVisible(true);
    }

    private void styleButton(JButton button, Dimension size, Font font,
            Color backgroundColor, Color hoverColor) {
        button.setPreferredSize(size);
        button.setFont(font);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
    }
}
