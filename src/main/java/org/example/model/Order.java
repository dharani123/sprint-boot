package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private int orderId;
    private int productId;
    private String productName;
    private int quantity;
    private double totalPrice;
    private String status;

    public Order(int orderId, int productId, String productName, int quantity, double totalPrice) {
        this.orderId      = orderId;
        this.productId    = productId;
        this.productName  = productName;
        this.quantity     = quantity;
        this.totalPrice   = totalPrice;
        this.status       = "CONFIRMED";
    }
}
