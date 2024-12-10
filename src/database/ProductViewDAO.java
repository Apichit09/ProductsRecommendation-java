package database;

import models.VisitHistory;
import utils.ConsoleColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductViewDAO {

    public boolean saveVisitHistory(int userId, int productId) {
        String sql = "INSERT INTO ProductViews (user_id, product_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println(ConsoleColor.RED + "Error saving visit history: " + e.getMessage() + ConsoleColor.RESET);
            e.printStackTrace();
            return false;
        }
    }

    public List<Integer> getVisitedProductIds(int userId) {
        List<Integer> visitedProductIds = new ArrayList<>();
        String sql = "SELECT product_id FROM ProductViews WHERE user_id = ?";

        try (Connection connection = DatabaseConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                visitedProductIds.add(resultSet.getInt("product_id"));
            }

        } catch (SQLException e) {
            System.err
                    .println(ConsoleColor.RED + "Error fetching visit history: " + e.getMessage() + ConsoleColor.RESET);
            e.printStackTrace();
        }

        return visitedProductIds;
    }

    public List<VisitHistory> getVisitHistories(int userId) {
        List<VisitHistory> visitHistories = new ArrayList<>();
        String sql = """
                SELECT view_id, product_id, viewed_at
                FROM ProductViews
                WHERE user_id = ?
                ORDER BY viewed_at DESC
                """;

        try (Connection connection = DatabaseConnector.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int viewId = resultSet.getInt("view_id");
                int productId = resultSet.getInt("product_id");
                java.sql.Timestamp viewedAt = resultSet.getTimestamp("viewed_at");

                VisitHistory visitHistory = new VisitHistory(viewId, userId, productId, viewedAt);
                visitHistories.add(visitHistory);
            }

        } catch (SQLException e) {
            System.err
                    .println(ConsoleColor.RED + "Error fetching visit history: " + e.getMessage() + ConsoleColor.RESET);
            e.printStackTrace();
        }

        return visitHistories;
    }
}
