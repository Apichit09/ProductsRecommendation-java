package database;

import models.Product;
import utils.ConsoleColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();

        String sql = """
                SELECT p.product_id, p.name, p.description, p.price, p.category_id, c.category_name, pi.image_url
                FROM Products p
                JOIN Categories c ON p.category_id = c.category_id
                LEFT JOIN ProductImages pi ON p.product_id = pi.product_id AND pi.is_primary = TRUE
                ORDER BY p.product_id
                """;

        try (Connection connection = DatabaseConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                int categoryId = resultSet.getInt("category_id");
                String categoryName = resultSet.getString("category_name");
                String imageUrl = resultSet.getString("image_url");

                Product product = new Product(productId, name, description, price, categoryId, categoryName, imageUrl);
                products.add(product);
            }

        } catch (SQLException e) {
            System.err.println(ConsoleColor.RED + "Error fetching products: " + e.getMessage() + ConsoleColor.RESET);
            e.printStackTrace();
        }

        return products;
    }

    public Product getProductById(int productId) {
        String sql = """
                SELECT p.product_id, p.name, p.description, p.price, p.category_id, c.category_name, pi.image_url
                FROM Products p
                JOIN Categories c ON p.category_id = c.category_id
                LEFT JOIN ProductImages pi ON p.product_id = pi.product_id AND pi.is_primary = TRUE
                WHERE p.product_id = ?
                """;

        try (Connection connection = DatabaseConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, productId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int pid = resultSet.getInt("product_id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                int categoryId = resultSet.getInt("category_id");
                String categoryName = resultSet.getString("category_name");
                String imageUrl = resultSet.getString("image_url");

                return new Product(pid, name, description, price, categoryId, categoryName, imageUrl);
            }

        } catch (SQLException e) {
            System.err.println(
                    ConsoleColor.RED + "Error fetching product details: " + e.getMessage() + ConsoleColor.RESET);
            e.printStackTrace();
        }

        return null;
    }
}
