package screens;

import database.ProductRecommendationDAO;
import models.Product;
import utils.Session;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class RecommendationScreenGUI extends JFrame {

    @SuppressWarnings("unused")
    private JFrame parentFrame;
    private JTable recommendationTable;
    private DefaultTableModel tableModel;
    private ProductRecommendationDAO productRecommendationDAO;

    @SuppressWarnings({ "unused", "static-access" })
    public RecommendationScreenGUI(JFrame parent) {
        this.parentFrame = parent;
        this.productRecommendationDAO = new ProductRecommendationDAO();

        setTitle("สินค้าแนะนำ");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel navBar = new JPanel();
        navBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
        navBar.setBackground(new Color(52, 73, 94));
        navBar.setPreferredSize(new Dimension(1200, 60));

        Font kanitNormal = new Font("Kanit", Font.PLAIN, 14);
        Font kanitBold = new Font("Kanit", Font.BOLD, 14);

        JButton backButton = createNavButton("กลับไปหน้าหลัก", kanitNormal);
        JButton logoutButton = createNavButton("ออกจากระบบ", kanitNormal);

        navBar.add(backButton);
        navBar.add(logoutButton);
        add(navBar, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 245, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        JButton refreshButton = new JButton("รีเฟรชรายการแนะนำ");
        styleButton(refreshButton, new Dimension(200, 40), kanitNormal,
                new Color(46, 204, 113), new Color(39, 174, 96));
        topPanel.add(refreshButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = { "รหัสสินค้า", "ชื่อสินค้า", "รายละเอียด", "ราคา", "หมวดหมู่", "รูปภาพ" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        recommendationTable = new JTable(tableModel);
        styleTable(recommendationTable, kanitNormal);
        recommendationTable.setRowHeight(120);
        recommendationTable.getColumn("รูปภาพ").setCellRenderer(new ImageRenderer());

        JScrollPane scrollPane = new JScrollPane(recommendationTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        JButton viewDetailsButton = new JButton("ดูรายละเอียดสินค้า");
        styleButton(viewDetailsButton, new Dimension(250, 45), kanitBold,
                new Color(52, 152, 219), new Color(41, 128, 185));
        bottomPanel.add(viewDetailsButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            new MainScreenGUI(this).setVisible(true);
            dispose();
        });

        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Do you want to log out ?", "Confirm logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Session.clear();
                new App().main(new String[] {});
                dispose();
            }
        });

        refreshButton.addActionListener(e -> loadRecommendedProducts());

        viewDetailsButton.addActionListener(e -> {
            int selectedRow = recommendationTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select the product you want to see details for.",
                        "Product not selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int productId = (int) tableModel.getValueAt(selectedRow, 0);
            new ProductDetailScreenGUI(this, productId).setVisible(true);
            setVisible(false);
        });

        loadRecommendedProducts();
        setVisible(true);
    }

    private void styleTable(JTable table, Font font) {
        table.setFont(font);
        table.getTableHeader().setFont(font);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(235, 245, 251));
        table.setSelectionForeground(Color.BLACK);

        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
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

    private void loadRecommendedProducts() {
        List<Product> recommendedProducts = productRecommendationDAO
                .getRecommendedProducts(Session.getCurrentUser().getUserId());

        tableModel.setRowCount(0);

        if (recommendedProducts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "There are no current product recommendations.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Product product : recommendedProducts) {
                ImageIcon imageIcon = getImageIconFromURL(product.getImageUrl());

                Object[] rowData = {
                        product.getProductId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getCategoryName(),
                        imageIcon
                };
                tableModel.addRow(rowData);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private ImageIcon getImageIconFromURL(String imageUrl) {
        try {
            BufferedImage img = ImageIO.read(new URL(imageUrl));
            Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (IOException e) {
            BufferedImage placeholder = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
            Graphics g = placeholder.getGraphics();
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, 100, 100);
            g.setColor(Color.BLACK);
            g.drawString("No Image", 25, 50);
            g.dispose();
            return new ImageIcon(placeholder);
        }
    }

    class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (value instanceof ImageIcon) {
                JLabel label = new JLabel();
                label.setIcon((ImageIcon) value);
                label.setHorizontalAlignment(JLabel.CENTER);
                return label;
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
