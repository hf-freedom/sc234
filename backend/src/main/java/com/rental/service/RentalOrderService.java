package com.rental.service;

import com.rental.dto.RentalRequest;
import com.rental.dto.ReturnRequest;
import com.rental.dto.StatisticsDTO;
import com.rental.entity.Product;
import com.rental.entity.RentalOrder;
import com.rental.enums.OrderStatus;
import com.rental.enums.ProductStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RentalOrderService {
    private final Map<Long, RentalOrder> orderRepository = new ConcurrentHashMap<>();
    private final AtomicLong orderIdGenerator = new AtomicLong(1);

    @Autowired
    private ProductService productService;

    public RentalOrder createOrder(RentalRequest request) {
        Product product = productService.getProductById(request.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!productService.isProductAvailable(request.getProductId())) {
            throw new RuntimeException("商品不可用");
        }

        long days = ChronoUnit.DAYS.between(request.getStartTime().toLocalDate(), request.getEndTime().toLocalDate()) + 1;
        if (days <= 0) {
            throw new RuntimeException("租赁时长无效");
        }

        RentalOrder order = new RentalOrder();
        order.setId(orderIdGenerator.getAndIncrement());
        order.setOrderNo("ORD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setUserId(request.getUserId());
        order.setUserName(request.getUserName());
        order.setDeposit(product.getDeposit());
        order.setDailyRate(product.getDailyRate());
        order.setStartTime(request.getStartTime());
        order.setEndTime(request.getEndTime());
        order.setRentalDays((int) days);
        order.setRentalAmount(product.getDailyRate().multiply(BigDecimal.valueOf(days)));
        order.setOverdueDays(0);
        order.setOverdueAmount(BigDecimal.ZERO);
        order.setDamageAmount(BigDecimal.ZERO);
        order.setRefundAmount(BigDecimal.ZERO);
        order.setFreezeStatus("未冻结");
        order.setFreezeSerialNo("");
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.put(order.getId(), order);
        return order;
    }

    public RentalOrder freezeDeposit(Long orderId) {
        RentalOrder order = orderRepository.get(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setFreezeStatus("已冻结");
        order.setFreezeSerialNo("FZ" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        order.setStatus(OrderStatus.DEPOSIT_FROZEN);
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    public RentalOrder confirmRental(Long orderId) {
        RentalOrder order = orderRepository.get(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.DEPOSIT_FROZEN) {
            throw new RuntimeException("订单状态不正确");
        }

        productService.updateProductStatus(order.getProductId(), ProductStatus.RENTED);
        order.setStatus(OrderStatus.RENTING);
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    public RentalOrder returnProduct(ReturnRequest request) {
        RentalOrder order = orderRepository.get(request.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.RENTING) {
            throw new RuntimeException("订单状态不正确");
        }

        LocalDateTime actualReturnTime = LocalDateTime.now();
        order.setActualReturnTime(actualReturnTime);

        if (actualReturnTime.isAfter(order.getEndTime())) {
            long overdueDays = ChronoUnit.DAYS.between(order.getEndTime().toLocalDate(), actualReturnTime.toLocalDate());
            order.setOverdueDays((int) overdueDays);
            BigDecimal overdueRate = new BigDecimal("1.5");
            order.setOverdueAmount(order.getDailyRate().multiply(overdueRate).multiply(BigDecimal.valueOf(overdueDays)));
        }

        if (request.getHasDamage() && request.getDamageAmount() != null) {
            order.setDamageAmount(request.getDamageAmount());
            order.setDamageDescription(request.getDamageDescription());
        }

        BigDecimal totalDeduction = order.getOverdueAmount().add(order.getDamageAmount());
        BigDecimal refund = order.getDeposit().subtract(totalDeduction);
        order.setRefundAmount(refund.compareTo(BigDecimal.ZERO) > 0 ? refund : BigDecimal.ZERO);

        productService.updateProductStatus(order.getProductId(), ProductStatus.IDLE);
        order.setStatus(OrderStatus.RETURNED);
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    public RentalOrder earlyReturn(Long orderId) {
        RentalOrder order = orderRepository.get(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != OrderStatus.RENTING) {
            throw new RuntimeException("订单状态不正确");
        }

        LocalDateTime actualReturnTime = LocalDateTime.now();
        order.setActualReturnTime(actualReturnTime);

        long actualDays = ChronoUnit.DAYS.between(order.getStartTime().toLocalDate(), actualReturnTime.toLocalDate()) + 1;
        long remainingDays = order.getRentalDays() - actualDays;

        if (remainingDays > 0) {
            BigDecimal refundRate = new BigDecimal("0.5");
            BigDecimal partialRefund = order.getDailyRate().multiply(BigDecimal.valueOf(remainingDays)).multiply(refundRate);
            order.setRefundAmount(order.getRefundAmount().add(partialRefund));
            order.setRentalAmount(order.getDailyRate().multiply(BigDecimal.valueOf(actualDays)));
        }

        if (actualReturnTime.isAfter(order.getEndTime())) {
            long overdueDays = ChronoUnit.DAYS.between(order.getEndTime().toLocalDate(), actualReturnTime.toLocalDate());
            order.setOverdueDays((int) overdueDays);
            BigDecimal overdueRate = new BigDecimal("1.5");
            order.setOverdueAmount(order.getDailyRate().multiply(overdueRate).multiply(BigDecimal.valueOf(overdueDays)));
        }

        productService.updateProductStatus(order.getProductId(), ProductStatus.IDLE);
        order.setStatus(OrderStatus.RETURNED);
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    public RentalOrder completeOrder(Long orderId) {
        RentalOrder order = orderRepository.get(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        order.setStatus(OrderStatus.COMPLETED);
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    public List<RentalOrder> getAllOrders() {
        return new ArrayList<>(orderRepository.values());
    }

    public RentalOrder getOrderById(Long id) {
        return orderRepository.get(id);
    }

    public StatisticsDTO getStatistics() {
        StatisticsDTO stats = new StatisticsDTO();
        List<RentalOrder> orders = new ArrayList<>(orderRepository.values());

        stats.setTotalOrders((long) orders.size());
        stats.setTotalProducts((long) productService.getAllProducts().size());
        stats.setRentalCount(orders.size());

        long overdueCount = orders.stream().filter(o -> o.getOverdueDays() != null && o.getOverdueDays() > 0).count();
        stats.setOverdueRate(orders.isEmpty() ? 0.0 : (double) overdueCount / orders.size() * 100);

        long damageCount = orders.stream().filter(o -> o.getDamageAmount() != null && o.getDamageAmount().compareTo(BigDecimal.ZERO) > 0).count();
        stats.setDamageRate(orders.isEmpty() ? 0.0 : (double) damageCount / orders.size() * 100);

        BigDecimal totalDamage = orders.stream()
                .map(o -> o.getDamageAmount() != null ? o.getDamageAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalDamageAmount(totalDamage);

        BigDecimal totalRental = orders.stream()
                .map(o -> o.getRentalAmount() != null ? o.getRentalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalRentalAmount(totalRental);

        return stats;
    }
}
