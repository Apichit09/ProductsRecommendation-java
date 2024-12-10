package models;

import java.sql.Timestamp;

public class VisitHistory {

    private int viewId;
    private int userId;
    private int productId;
    private Timestamp viewedAt;

    public VisitHistory(int viewId, int userId, int productId, Timestamp viewedAt) {
        this.viewId = viewId;
        this.userId = userId;
        this.productId = productId;
        this.viewedAt = viewedAt;
    }

    public int getViewId() {
        return viewId;
    }

    public int getUserId() {
        return userId;
    }

    public int getProductId() {
        return productId;
    }

    public Timestamp getViewedAt() {
        return viewedAt;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setViewedAt(Timestamp viewedAt) {
        this.viewedAt = viewedAt;
    }

    @Override
    public String toString() {
        return "VisitHistory{" +
                "viewId=" + viewId +
                ", userId=" + userId +
                ", productId=" + productId +
                ", viewedAt=" + viewedAt +
                '}';
    }
}
