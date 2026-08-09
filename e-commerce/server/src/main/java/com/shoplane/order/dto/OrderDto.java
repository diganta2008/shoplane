package com.shoplane.order.dto;

import com.shoplane.order.Order;
import com.shoplane.order.OrderItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderDto(
        Long id,
        String orderNumber,
        String status,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal shipping,
        BigDecimal tax,
        BigDecimal total,
        String couponCode,
        String paymentMethod,
        ShippingAddress shippingAddress,
        Instant placedAt,
        Instant updatedAt,
        List<Line> items) {

    public record ShippingAddress(String name, String email, String phone, String address,
                                  String city, String state, String zip, String country) {}

    public record Line(Long id, Long productId, String name, BigDecimal price,
                       Integer qty, String size, String color, BigDecimal lineTotal) {}

    public static OrderDto from(Order o) {
        List<Line> lines = o.getItems().stream().map(OrderDto::line).toList();
        return new OrderDto(
                o.getId(), o.getOrderNumber(), o.getStatus(),
                o.getSubtotal(), o.getDiscount(), o.getShipping(), o.getTax(), o.getTotal(),
                o.getCouponCode(), o.getPaymentMethod(),
                new ShippingAddress(o.getShipName(), o.getShipEmail(), o.getShipPhone(),
                        o.getShipAddress(), o.getShipCity(), o.getShipState(),
                        o.getShipZip(), o.getShipCountry()),
                o.getPlacedAt(), o.getUpdatedAt(),
                lines);
    }

    private static Line line(OrderItem i) {
        BigDecimal lt = i.getPrice().multiply(BigDecimal.valueOf(i.getQty()));
        return new Line(i.getId(), i.getProduct().getId(), i.getName(), i.getPrice(),
                i.getQty(), i.getSize(), i.getColor(), lt);
    }
}
