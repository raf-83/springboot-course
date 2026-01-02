package pl.rafal.springboot_course.customer;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final CustomerRepository customerRepository;

    public OrdersService(OrdersRepository ordersRepository, CustomerRepository customerRepository) {
        this.ordersRepository = ordersRepository;
        this.customerRepository = customerRepository;
    }

    public Page<OrdersResponse> findOrdersForCustomer(Long customerId, Pageable pageable) {
        return ordersRepository.findByCustomerId(customerId, pageable)
                .map(this::mapToResponse);
    }

    public OrdersResponse createOrder(Long customerId, OrdersRequest  request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        Orders order = new Orders(request.description(), request.amount(), customer);
        Orders saved = ordersRepository.save(order);

        return mapToResponse(saved);
    }

    private OrdersResponse mapToResponse(Orders order) {
        return new OrdersResponse(
                order.getId(),
                order.getDescription(),
                order.getAmount(),
                order.getCreatedAt()
        );
    }
}

