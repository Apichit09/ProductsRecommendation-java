package screens;

import database.DatabaseConnector;
import database.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class RegisterScreenGUI extends JFrame {

    private JFrame parentFrame;

    public RegisterScreenGUI(JFrame parent) {
        this.parentFrame = parent;

        setTitle("สมัครสมาชิก");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 250));
        panel.setLayout(new GridBagLayout());
        add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font kanitNormal = new Font("Kanit", Font.PLAIN, 14);
        Font kanitBold = new Font("Kanit", Font.BOLD, 14);

        JLabel userLabel = new JLabel("ชื่อผู้ใช้ :");
        userLabel.setFont(kanitNormal);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);

        JTextField userText = new JTextField(20);
        styleTextField(userText, kanitNormal);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(userText, gbc);

        JLabel emailLabel = new JLabel("อีเมล :");
        emailLabel.setFont(kanitNormal);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(emailLabel, gbc);

        JTextField emailText = new JTextField(20);
        styleTextField(emailText, kanitNormal);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(emailText, gbc);

        JLabel passwordLabel = new JLabel("รหัสผ่าน :");
        passwordLabel.setFont(kanitNormal);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);

        JPasswordField passwordText = new JPasswordField(20);
        styleTextField(passwordText, kanitNormal);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(passwordText, gbc);

        JLabel confirmPasswordLabel = new JLabel("ยืนยันรหัสผ่าน :");
        confirmPasswordLabel.setFont(kanitNormal);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(confirmPasswordLabel, gbc);

        JPasswordField confirmPasswordText = new JPasswordField(20);
        styleTextField(confirmPasswordText, kanitNormal);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(confirmPasswordText, gbc);

        JButton registerButton = new JButton("สมัครสมาชิก");
        styleButton(registerButton, new Dimension(200, 40), kanitBold,
                new Color(46, 204, 113), new Color(39, 174, 96));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 15, 15, 15);
        panel.add(registerButton, gbc);

        JButton backButton = new JButton("ย้อนกลับ");
        styleButton(backButton, new Dimension(200, 40), kanitBold,
                new Color(231, 76, 60), new Color(192, 57, 43));
        gbc.gridy = 5;
        gbc.insets = new Insets(15, 15, 15, 15);
        panel.add(backButton, gbc);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText().trim();
                String email = emailText.getText().trim();
                String password = new String(passwordText.getPassword()).trim();
                String confirmPassword = new String(confirmPasswordText.getPassword()).trim();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterScreenGUI.this, "Please fill in all fields.", "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(RegisterScreenGUI.this, "Passwords do not match!", "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (Connection connection = DatabaseConnector.getConnection()) {
                    UserDAO userDAO = new UserDAO();
                    boolean isRegistered = userDAO.registerUser(connection, username, email, password);

                    if (isRegistered) {
                        JOptionPane.showMessageDialog(RegisterScreenGUI.this,
                                "Registration Successful! You can now login.", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        new LoginScreenGUI(RegisterScreenGUI.this).setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(RegisterScreenGUI.this, "Registration failed. Please try again.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RegisterScreenGUI.this,
                            "Error during registration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void styleTextField(JTextField textField, Font font) {
        textField.setFont(font);
        textField.setPreferredSize(new Dimension(200, 35));
        textField.setMargin(new Insets(5, 10, 5, 10));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)));
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
