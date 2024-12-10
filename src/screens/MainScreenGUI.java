package screens;

import database.ProductDAO;
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

public class MainScreenGUI extends JFrame {

    @SuppressWarnings("unused")
    private JFrame parentFrame;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private ProductDAO productDAO;

    @SuppressWarnings({ "static-access", "unused" })
    public MainScreenGUI(JFrame parent) {
        this.parentFrame = parent;
        this.productDAO = new ProductDAO();

        setTitle("Product Recommendation System");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel navBar = new JPanel();
        navBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
        navBar.setBackground(new Color(52, 73, 94));
        navBar.setPreferredSize(new Dimension(1200, 60));
        Font kanitNormal = new Font("Kanit", Font.PLAIN, 14);
        Font kanitBold = new Font("Kanit", Font.BOLD, 14);
        JButton recommendButton = createNavButton("แนะนำสินค้า", kanitNormal);
        JButton historyButton = createNavButton("ประวัติการเข้าชม", kanitNormal);
        JButton logoutButton = createNavButton("ออกจากระบบ", kanitNormal);
        navBar.add(recommendButton);
        navBar.add(historyButton);
        navBar.add(logoutButton);
        add(navBar, BorderLayout.NORTH);
        setupProductTable();

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);
        JButton viewDetailsButton = new JButton("ดูรายละเอียดสินค้า");
        styleButton(viewDetailsButton, new Dimension(250, 45), kanitBold,
                new Color(52, 152, 219), new Color(41, 128, 185));
        bottomPanel.add(viewDetailsButton);
        add(bottomPanel, BorderLayout.SOUTH);

        recommendButton.addActionListener(e -> {
            new RecommendationScreenGUI(MainScreenGUI.this).setVisible(true);
            setVisible(false);
        });

        historyButton.addActionListener(e -> {
            new VisitHistoryScreenGUI(MainScreenGUI.this).setVisible(true);
            setVisible(false);
        });

        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(MainScreenGUI.this,
                    "Do you want to log out ?", "Confirm logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Session.clear();
                new App().main(new String[] {});
                dispose();
            }
        });

        viewDetailsButton.addActionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(MainScreenGUI.this,
                        "Please select the product you want to see details for.",
                        "Product not selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int productId = (int) tableModel.getValueAt(selectedRow, 0);
            new ProductDetailScreenGUI(MainScreenGUI.this, productId).setVisible(true);
            setVisible(false);
        });

        loadAllProducts();
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

    private void setupProductTable() {
        String[] columnNames = { "รหัสสินค้า", "ชื่อสินค้า", "รายละเอียด", "ราคา (บาท)", "หมวดหมู่", "รูปภาพ" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel);
        styleTable(productTable);

        productTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        productTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        DefaultTableCellRenderer wrapRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                return label;
            }
        };

        productTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        productTable.getColumnModel().getColumn(1).setCellRenderer(wrapRenderer);
        productTable.getColumnModel().getColumn(2).setCellRenderer(wrapRenderer);
        productTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        productTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        productTable.getColumnModel().getColumn(5).setCellRenderer(new ImageRenderer());

        productTable.setRowHeight(120);
    }

    private void styleTable(JTable table) {
        Font kanitNormal = new Font("Kanit", Font.PLAIN, 14);
        Font kanitBold = new Font("Kanit", Font.BOLD, 14);

        table.setFont(kanitNormal);
        table.getTableHeader().setFont(kanitBold);

        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(235, 245, 251));
        table.setSelectionForeground(Color.BLACK);

        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        ((DefaultTableCellRenderer) table.getDefaultRenderer(Object.class)).setOpaque(true);
        ((DefaultTableCellRenderer) table.getDefaultRenderer(Object.class))
                .setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private void styleButton(JButton button, Dimension size, Font font, Color backgroundColor, Color hoverColor) {
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

    private void loadAllProducts() {
        List<Product> products = productDAO.getAllProducts();
        tableModel.setRowCount(0);

        for (Product product : products) {
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

    @SuppressWarnings("deprecation")
    private ImageIcon getImageIconFromURL(String imageUrl) {
        try {
            Image img = ImageIO.read(new URL(imageUrl));
            Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (IOException e) {
            Image placeholder = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
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
