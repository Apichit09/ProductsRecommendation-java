package screens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class App {
    public static void main(String[] args) {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Kanit-Regular.ttf")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Product Recommendation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 250));
        panel.setLayout(new GridBagLayout());
        frame.add(panel);
        Font kanitFont = new Font("Kanit", Font.PLAIN, 18);
        Dimension buttonSize = new Dimension(280, 50);
        JButton loginButton = new JButton("เข้าสู่ระบบ");
        styleButton(loginButton, buttonSize, kanitFont, new Color(79, 134, 247), new Color(63, 118, 231));
        JButton registerButton = new JButton("สมัครสมาชิก");
        styleButton(registerButton, buttonSize, kanitFont, new Color(46, 204, 113), new Color(39, 174, 96));
        JButton exitButton = new JButton("ออกจากโปรแกรม");
        styleButton(exitButton, buttonSize, kanitFont, new Color(231, 76, 60), new Color(192, 57, 43));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(25, 0, 25, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(loginButton, gbc);

        gbc.gridy = 1;
        panel.add(registerButton, gbc);

        gbc.gridy = 2;
        panel.add(exitButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginScreenGUI(frame);
                frame.setVisible(false);
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterScreenGUI(frame);
                frame.setVisible(false);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit?",
                        "Exit Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        frame.setVisible(true);
    }

    private static void styleButton(JButton button, Dimension size, Font font, Color backgroundColor,
            Color hoverColor) {
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
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
    }
}
