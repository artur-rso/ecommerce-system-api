package ecommerce.system.api.entities;

import ecommerce.system.api.models.OrdersByStoreReportModel;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity(name = "OrdersByStoreReportEntity")
@Immutable
@Table(name = "vw_ordersByStoreAndStatus")
public class OrdersByStoreReportEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "storeId")
    private int storeId;

    @Column(name = "storeName")
    private String storeName;

    @Column(name = "orders")
    private int orders;

    @Column(name = "receivedOrders")
    private int receivedOrders;

    @Column(name = "paidOrders")
    private int paidOrders;

    @Column(name = "sentOrders")
    private int sentOrders;

    @Column(name = "finishedOrders")
    private int finishedOrders;

    public OrdersByStoreReportEntity() {
    }

    public OrdersByStoreReportEntity(UUID id, int storeId, String storeName, int orders, int receivedOrders, int paidOrders, int sentOrders, int finishedOrders) {
        this.id = id;
        this.storeId = storeId;
        this.storeName = storeName;
        this.orders = orders;
        this.receivedOrders = receivedOrders;
        this.paidOrders = paidOrders;
        this.sentOrders = sentOrders;
        this.finishedOrders = finishedOrders;
    }

    public OrdersByStoreReportModel toModel() {
        return new OrdersByStoreReportModel(this.id, this.storeId, this.storeName, this.orders, this.receivedOrders, this.paidOrders, this.sentOrders, this.finishedOrders);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getOrders() {
        return orders;
    }

    public void setOrders(int orders) {
        this.orders = orders;
    }

    public int getReceivedOrders() {
        return receivedOrders;
    }

    public void setReceivedOrders(int receivedOrders) {
        this.receivedOrders = receivedOrders;
    }

    public int getPaidOrders() {
        return paidOrders;
    }

    public void setPaidOrders(int paidOrders) {
        this.paidOrders = paidOrders;
    }

    public int getSentOrders() {
        return sentOrders;
    }

    public void setSentOrders(int sentOrders) {
        this.sentOrders = sentOrders;
    }

    public int getFinishedOrders() {
        return finishedOrders;
    }

    public void setFinishedOrders(int finishedOrders) {
        this.finishedOrders = finishedOrders;
    }
}
