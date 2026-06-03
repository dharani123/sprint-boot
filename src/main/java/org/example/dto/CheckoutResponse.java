package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutResponse {

    private String         orderId;
    private List<LineItem> items;
    private double         subtotal;
    private double         deliveryFee;
    private double         platformFee;
    private double         discount;
    private double         total;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LineItem {
        private Long   productId;
        private String name;
        private String emoji;
        private int    quantity;
        private double unitPrice;
        private double itemTotal;
    }
}
