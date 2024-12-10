package database;

import models.User;
import utils.ConsoleColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean registerUser(Connection connection, String username, String email, String password) {
        String sql = "INSERT INTO Users (username, email, password) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println(ConsoleColor.RED + "Error registering user: " + e.getMessage() + ConsoleColor.RESET);
            return false;
        }
    }

    public User loginUser(Connection connection, String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String userEmail = rs.getString("email");
                    return new User(userId, username, userEmail, password);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println(ConsoleColor.RED + "Error logging in: " + e.getMessage() + ConsoleColor.RESET);
            return null;
        }
    }
}
