package database;

import models.Product;
import models.ProductRecommendation;

import java.util.List;

public class ProductRecommendationDAO {

    private ProductRecommendation recommendationSystem;
    private ProductDAO productDAO;
    private ProductViewDAO productViewDAO;

    public ProductRecommendationDAO() {
        this.recommendationSystem = new ProductRecommendation();
        this.productDAO = new ProductDAO();
        this.productViewDAO = new ProductViewDAO();

        List<Product> allProducts = productDAO.getAllProducts();
        for (Product product : allProducts) {
            recommendationSystem.addProduct(product);
        }
    }

    public void updateRecommendations(int userId) {
        List<Integer> visitedProductIds = productViewDAO.getVisitedProductIds(userId);
        for (Integer pid : visitedProductIds) {
            recommendationSystem.addVisitHistory(userId, pid);
        }
    }

    public List<Product> getRecommendedProducts(int userId) {
        updateRecommendations(userId);
        return recommendationSystem.recommendProducts(userId);
    }

    public void addNewVisit(int userId, int productId) {
        productViewDAO.saveVisitHistory(userId, productId);
        recommendationSystem.addVisitHistory(userId, productId);
    }
}
