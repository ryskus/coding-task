package com.codingtask.controller;

import com.codingtask.model.entity.Order;
import com.codingtask.model.form.OrderForm;
import com.codingtask.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity<Long> createOrder(@Validated @RequestBody OrderForm orderForm) {
        return ResponseEntity.ok(orderService.createOrder(orderForm));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        return orderService.findById(orderId).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order")
    public ResponseEntity<List<Order>> getByOrderDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Order> byOrderDate = orderService.findByOrderDate(startDate, endDate);
        return byOrderDate.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(byOrderDate);
    }
}
