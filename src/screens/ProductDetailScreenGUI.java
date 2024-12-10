package screens;

import database.ProductDAO;
import database.ProductViewDAO;
import models.Product;
import utils.Session;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ProductDetailScreenGUI extends JFrame {

    @SuppressWarnings("unused")
    private JFrame parentFrame;
    @SuppressWarnings("unused")
    private int productId;

    @SuppressWarnings("unused")
    public ProductDetailScreenGUI(JFrame parent, int productId) {
        this.parentFrame = parent;
        this.productId = productId;

        setTitle("รายละเอียดสินค้า");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel navBar = new JPanel();
        navBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
        navBar.setBackground(new Color(52, 73, 94));
        navBar.setPreferredSize(new Dimension(1200, 60));

        Font kanitNormal = new Font("Kanit", Font.PLAIN, 14);
        Font kanitBold = new Font("Kanit", Font.BOLD, 16);
        Font kanitHeader = new Font("Kanit", Font.BOLD, 24);
        Font kanitPrice = new Font("Kanit", Font.BOLD, 20);

        JButton backButton = createNavButton("กลับไปหน้าสินค้า", kanitNormal);
        navBar.add(backButton);
        add(navBar, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(245, 245, 250));
        mainPanel.setLayout(new BorderLayout(30, 30));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        ProductDAO productDAO = new ProductDAO();
        Product product = productDAO.getProductById(productId);

        if (product == null) {
            JOptionPane.showMessageDialog(this, "No products found. Returning to the main page.", "mistake",
                    JOptionPane.ERROR_MESSAGE);
            new MainScreenGUI(this).setVisible(true);
            dispose();
            return;
        }

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BorderLayout(20, 20));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)));

        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imagePanel.setBackground(Color.WHITE);
        JLabel imageLabel = new JLabel();
        ImageIcon imageIcon = getImageIconFromURL(product.getImageUrl());
        imageLabel.setIcon(imageIcon);
        imagePanel.add(imageLabel);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(kanitHeader);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel(String.format("฿%.2f บาท", product.getPrice()));
        priceLabel.setFont(kanitPrice);
        priceLabel.setForeground(new Color(52, 152, 219));
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        categoryPanel.setBackground(new Color(52, 73, 94));
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel categoryLabel = new JLabel(product.getCategoryName());
        categoryLabel.setFont(kanitNormal);
        categoryLabel.setForeground(Color.WHITE);
        categoryPanel.add(categoryLabel);
        categoryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descriptionArea = new JTextArea(product.getDescription());
        descriptionArea.setFont(kanitNormal);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(new Color(250, 250, 250));
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        infoPanel.add(priceLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        infoPanel.add(categoryPanel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        infoPanel.add(descriptionArea);

        detailsPanel.add(imagePanel, BorderLayout.NORTH);
        detailsPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        add(mainPanel);

        backButton.addActionListener(e -> {
            new MainScreenGUI(ProductDetailScreenGUI.this).setVisible(true);
            dispose();
        });

        ProductViewDAO productViewDAO = new ProductViewDAO();
        productViewDAO.saveVisitHistory(Session.getCurrentUser().getUserId(), productId);

        setVisible(true);
    }

    private JButton createNavButton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 73, 94));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(44, 62, 80));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 73, 94));
            }
        });

        return button;
    }

    private ImageIcon getImageIconFromURL(String imageUrl) {
        try {
            @SuppressWarnings("deprecation")
            BufferedImage img = ImageIO.read(new URL(imageUrl));
            Image scaledImg = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (IOException e) {
            BufferedImage placeholder = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            Graphics g = placeholder.getGraphics();
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, 200, 200);
            g.setColor(Color.BLACK);
            g.drawString("No Image", 80, 100);
            g.dispose();
            return new ImageIcon(placeholder);
        }
    }
}
