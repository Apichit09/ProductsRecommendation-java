package models;

import java.util.*;
import java.util.stream.Collectors;

public class ProductRecommendation {

    private Map<Integer, List<Integer>> userVisitHistory;
    private Map<Integer, Product> products;
    private Map<Integer, Integer> productPopularity;

    public ProductRecommendation() {
        userVisitHistory = new HashMap<>();
        products = new HashMap<>();
        productPopularity = new HashMap<>();
    }

    public void addVisitHistory(int userId, int productId) {
        userVisitHistory.putIfAbsent(userId, new ArrayList<>());
        userVisitHistory.get(userId).add(productId);
        productPopularity.put(productId, productPopularity.getOrDefault(productId, 0) + 1);
    }

    public void addProduct(Product product) {
        products.put(product.getProductId(), product);
    }

    public List<Product> recommendProducts(int userId) {
        List<Product> recommendedProducts = new ArrayList<>();
        List<Integer> visitedProductIds = userVisitHistory.getOrDefault(userId, new ArrayList<>());

        if (visitedProductIds.isEmpty()) {
            recommendedProducts.addAll(getTopPopularProducts(5));
            return recommendedProducts;
        }

        Set<Integer> visitedCategories = visitedProductIds.stream()
                .map(pid -> products.get(pid).getCategoryId())
                .collect(Collectors.toSet());

        for (Integer categoryId : visitedCategories) {
            List<Product> categoryProducts = products.values().stream()
                    .filter(p -> p.getCategoryId() == categoryId && !visitedProductIds.contains(p.getProductId()))
                    .sorted((p1, p2) -> productPopularity.getOrDefault(p2.getProductId(), 0)
                            - productPopularity.getOrDefault(p1.getProductId(), 0))
                    .collect(Collectors.toList());

            for (Product product : categoryProducts) {
                if (recommendedProducts.size() >= 5)
                    break;
                recommendedProducts.add(product);
            }
            if (recommendedProducts.size() >= 5)
                break;
        }

        if (recommendedProducts.size() < 5) {
            List<Product> topPopular = getTopPopularProducts(5 - recommendedProducts.size());
            for (Product product : topPopular) {
                if (!visitedProductIds.contains(product.getProductId()) && !recommendedProducts.contains(product)) {
                    recommendedProducts.add(product);
                }
            }
        }

        return recommendedProducts;
    }

    private List<Product> getTopPopularProducts(int n) {
        return productPopularity.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue() - e1.getValue())
                .limit(n)
                .map(e -> products.get(e.getKey()))
                .collect(Collectors.toList());
    }
}
