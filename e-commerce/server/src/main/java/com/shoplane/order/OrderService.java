package com.shoplane.order;

import com.shoplane.address.Address;
import com.shoplane.address.AddressRepository;
import com.shoplane.cart.Cart;
import com.shoplane.cart.CartRepository;
import com.shoplane.common.ApiException;
import com.shoplane.common.PageMeta;
import com.shoplane.coupon.Coupon;
import com.shoplane.coupon.CouponService;
import com.shoplane.order.dto.CreateOrderRequest;
import com.shoplane.order.dto.OrderDto;
import com.shoplane.product.Product;
import com.shoplane.product.ProductRepository;
import com.shoplane.user.User;
import com.shoplane.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private static final SecureRandom RNG = new SecureRandom();
    private static final char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    private final OrderRepository orders;
    private final ProductRepository products;
    private final CartRepository carts;
    private final CouponService couponService;
    private final AddressRepository addresses;
    private final UserRepository users;

    public OrderService(OrderRepository orders,
                        ProductRepository products,
                        CartRepository carts,
                        CouponService couponService,
                        AddressRepository addresses,
                        UserRepository users) {
        this.orders = orders;
        this.products = products;
        this.carts = carts;
        this.couponService = couponService;
        this.addresses = addresses;
        this.users = users;
    }

    @Transactional
    public OrderDto create(Long userId, CreateOrderRequest req) {
        List<CreateOrderRequest.OrderLine> lines = req.items();
        Cart cart = null;

        if (lines == null || lines.isEmpty()) {
            cart = carts.findFirstByUserIdOrderByIdAsc(userId)
                    .orElseThrow(() -> ApiException.badRequest("Cart is empty"));
            if (cart.getItems().isEmpty()) throw ApiException.badRequest("Cart is empty");
            lines = cart.getItems().stream()
                    .map(ci -> new CreateOrderRequest.OrderLine(
                            ci.getProduct().getId(), ci.getQty(), ci.getSize(), ci.getColor()))
                    .toList();
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("confirmed");
        order.setPaymentMethod(req.paymentMethod());
        applyShippingTo(order, userId, req);

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> resolvedItems = new ArrayList<>();
        for (var line : lines) {
            Product p = products.findByIdAndActiveTrue(line.productId())
                    .orElseThrow(() -> ApiException.notFound("Product " + line.productId() + " not available"));

            int decremented = products.decrementStock(p.getId(), line.qty());
            if (decremented != 1) {
                throw ApiException.conflict("Insufficient stock for " + p.getName());
            }

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(p);
            oi.setName(p.getName());
            oi.setPrice(p.getPrice());
            oi.setQty(line.qty());
            oi.setSize(line.size());
            oi.setColor(line.color());
            resolvedItems.add(oi);
            subtotal = subtotal.add(p.getPrice().multiply(BigDecimal.valueOf(line.qty())));
        }

        BigDecimal discount = BigDecimal.ZERO;
        String couponCode = null;
        if (req.couponCode() != null && !req.couponCode().isBlank()) {
            Coupon c = couponService.require(req.couponCode());
            discount = couponService.computeDiscount(c, subtotal);
            couponCode = c.getCode();
        }

        BigDecimal taxable = subtotal.subtract(discount).max(BigDecimal.ZERO);
        BigDecimal taxRate = req.taxRate() != null ? req.taxRate() : BigDecimal.ZERO;
        BigDecimal tax = taxable.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal shipping = req.shippingFee() != null ? req.shippingFee() : BigDecimal.ZERO;
        BigDecimal total = taxable.add(tax).add(shipping).max(BigDecimal.ZERO);

        order.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        order.setDiscount(discount.setScale(2, RoundingMode.HALF_UP));
        order.setShipping(shipping.setScale(2, RoundingMode.HALF_UP));
        order.setTax(tax);
        order.setTotal(total.setScale(2, RoundingMode.HALF_UP));
        order.setCouponCode(couponCode);
        order.setOrderNumber(generateOrderNumber());
        order.getItems().addAll(resolvedItems);

        Order saved = orders.save(order);

        // Empty the user's cart on successful checkout
        if (cart == null) cart = carts.findFirstByUserIdOrderByIdAsc(userId).orElse(null);
        if (cart != null && !cart.getItems().isEmpty()) {
            cart.getItems().clear();
            carts.save(cart);
        }

        return OrderDto.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResult<OrderDto> list(Long userId, int limit, int offset) {
        int size = Math.max(1, Math.min(limit, 100));
        int page = Math.max(0, offset / size);
        Page<Order> pageR = orders.findByUserIdOrderByPlacedAtDesc(userId, PageRequest.of(page, size));
        return new PageResult<>(
                pageR.getContent().stream().map(OrderDto::from).toList(),
                new PageMeta(pageR.getTotalElements(), size, offset));
    }

    @Transactional(readOnly = true)
    public OrderDto getByNumber(Long userId, String orderNumber) {
        return orders.findByOrderNumberAndUserId(orderNumber, userId)
                .map(OrderDto::from)
                .orElseThrow(() -> ApiException.notFound("Order not found"));
    }

    private static String generateOrderNumber() {
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        StringBuilder rand = new StringBuilder(6);
        for (int i = 0; i < 6; i++) rand.append(ALPHABET[RNG.nextInt(ALPHABET.length)]);
        return String.format("SL-%04d%02d-%s", now.getYear(), now.getMonthValue(), rand);
    }

    /**
     * Decide which shipping details to persist on this Order. Precedence:
     *   1. {@code req.addressId()} → look up saved Address for this user, use its fields.
     *      Email is filled from the user record (Address book doesn't store email).
     *   2. {@code req.shipping()} → use the one-off shipping block supplied inline.
     *   3. Neither → 400 Bad Request.
     */
    private void applyShippingTo(Order order, Long userId, CreateOrderRequest req) {
        if (req.addressId() != null) {
            Address a = addresses.findByIdAndUserId(req.addressId(), userId)
                    .orElseThrow(() -> ApiException.notFound("Address " + req.addressId() + " not found"));
            String email = users.findById(userId).map(User::getEmail).orElse("noreply@shoplane.dev");
            order.setShipName(a.getFullName());
            order.setShipEmail(email);
            order.setShipPhone(a.getPhone());
            order.setShipAddress(a.getStreet());
            order.setShipCity(a.getCity());
            order.setShipState(a.getState());
            order.setShipZip(a.getZip());
            order.setShipCountry(a.getCountry());
            return;
        }
        if (req.shipping() == null) {
            throw ApiException.badRequest("Either addressId or shipping is required");
        }
        var s = req.shipping();
        order.setShipName(s.name());
        order.setShipEmail(s.email());
        order.setShipPhone(s.phone());
        order.setShipAddress(s.address());
        order.setShipCity(s.city());
        order.setShipState(s.state());
        order.setShipZip(s.zip());
        order.setShipCountry(s.country());
    }

    public record PageResult<T>(List<T> items, PageMeta meta) {}
}
