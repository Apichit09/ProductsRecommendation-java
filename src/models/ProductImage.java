package models;

public class ProductImage {

    private int imageId;
    private int productId;
    private String imageUrl;
    private boolean isPrimary;

    public ProductImage(int imageId, int productId, String imageUrl, boolean isPrimary) {
        this.imageId = imageId;
        this.productId = productId;
        this.imageUrl = imageUrl;
        this.isPrimary = isPrimary;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    @Override
    public String toString() {
        return "ProductImage{" +
                "imageId=" + imageId +
                ", productId=" + productId +
                ", imageUrl='" + imageUrl + '\'' +
                ", isPrimary=" + isPrimary +
                '}';
    }
}
