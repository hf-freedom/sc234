package com.rental.controller;

import com.rental.dto.RentalRequest;
import com.rental.dto.ReturnRequest;
import com.rental.dto.StatisticsDTO;
import com.rental.entity.RentalOrder;
import com.rental.service.RentalOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class RentalOrderController {
    @Autowired
    private RentalOrderService rentalOrderService;

    @PostMapping
    public ResponseEntity<RentalOrder> createOrder(@RequestBody RentalRequest request) {
        try {
            return ResponseEntity.ok(rentalOrderService.createOrder(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/freeze-deposit")
    public ResponseEntity<RentalOrder> freezeDeposit(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(rentalOrderService.freezeDeposit(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/confirm-rental")
    public ResponseEntity<RentalOrder> confirmRental(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(rentalOrderService.confirmRental(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/return")
    public ResponseEntity<RentalOrder> returnProduct(@RequestBody ReturnRequest request) {
        try {
            return ResponseEntity.ok(rentalOrderService.returnProduct(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/early-return")
    public ResponseEntity<RentalOrder> earlyReturn(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(rentalOrderService.earlyReturn(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<RentalOrder> completeOrder(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(rentalOrderService.completeOrder(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<RentalOrder>> getAllOrders() {
        return ResponseEntity.ok(rentalOrderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentalOrder> getOrderById(@PathVariable Long id) {
        RentalOrder order = rentalOrderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsDTO> getStatistics() {
        return ResponseEntity.ok(rentalOrderService.getStatistics());
    }
}
