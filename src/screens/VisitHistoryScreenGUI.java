package screens;

import database.ProductDAO;
import database.ProductViewDAO;
import models.Product;
import models.VisitHistory;
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

public class VisitHistoryScreenGUI extends JFrame {

    @SuppressWarnings("unused")
    private JFrame parentFrame;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private ProductViewDAO productViewDAO;
    private ProductDAO productDAO;

    @SuppressWarnings({ "unused", "static-access" })
    public VisitHistoryScreenGUI(JFrame parent) {
        this.parentFrame = parent;
        this.productViewDAO = new ProductViewDAO();
        this.productDAO = new ProductDAO();

        setTitle("ประวัติการเข้าชมสินค้า");
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

        String[] columnNames = { "รหัสการเข้าชม", "รหัสสินค้า", "ชื่อสินค้า", "เวลาที่เข้าชม", "รูปภาพ" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        styleTable(historyTable, kanitNormal);
        historyTable.setRowHeight(120);
        historyTable.getColumn("รูปภาพ").setCellRenderer(new ImageRenderer());

        JScrollPane scrollPane = new JScrollPane(historyTable);
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

        viewDetailsButton.addActionListener(e -> {
            int selectedRow = historyTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select the product you want to see details for.",
                        "Product not selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int productId = (int) tableModel.getValueAt(selectedRow, 1);
            new ProductDetailScreenGUI(this, productId).setVisible(true);
            setVisible(false);
        });

        loadVisitHistory();
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

    private void loadVisitHistory() {
        List<VisitHistory> visitHistories = productViewDAO.getVisitHistories(Session.getCurrentUser().getUserId());
        tableModel.setRowCount(0);

        if (visitHistories.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No visit history available.", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (VisitHistory visit : visitHistories) {
                Product product = productDAO.getProductById(visit.getProductId());
                String productName = (product != null) ? product.getName() : "Unknown";

                ImageIcon imageIcon = (product != null) ? getImageIconFromURL(product.getImageUrl())
                        : getPlaceholderImage();

                Object[] rowData = {
                        visit.getViewId(),
                        visit.getProductId(),
                        productName,
                        visit.getViewedAt(),
                        imageIcon
                };
                tableModel.addRow(rowData);
            }
        }
    }

    private ImageIcon getImageIconFromURL(String imageUrl) {
        try {
            @SuppressWarnings("deprecation")
            BufferedImage img = ImageIO.read(new URL(imageUrl));
            Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } catch (IOException e) {

            return getPlaceholderImage();
        }
    }

    private ImageIcon getPlaceholderImage() {
        BufferedImage placeholder = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics g = placeholder.getGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, 100, 100);
        g.setColor(Color.BLACK);
        g.drawString("No Image", 25, 50);
        g.dispose();
        return new ImageIcon(placeholder);
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
