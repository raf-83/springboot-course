package pl.rafal.springboot_course.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers/{customerId}/orders")
public class OrdersController {

    private final OrdersService ordersService;

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @GetMapping
    public ResponseEntity<Page<OrdersResponse>> getOrders(
            @PathVariable Long customerId,
            Pageable pageable) {

        Page<OrdersResponse> orders = ordersService.findOrdersForCustomer(customerId, pageable);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<OrdersResponse> createOrder(
            @PathVariable Long customerId,
            @RequestBody OrdersRequest request) {

        OrdersResponse created = ordersService.createOrder(customerId, request);
        return ResponseEntity.ok(created);
    }
}
