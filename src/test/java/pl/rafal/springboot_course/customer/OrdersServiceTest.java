package pl.rafal.springboot_course.customer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrdersServiceTest {

    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private OrdersService ordersService;

    @Test
    void shouldCreateOrderForExistingCustomer() {

        // GIVEN
        Long customerId = 1L;

        Customer existingCustomer = new Customer(customerId, "Rafal", "rafal@example.com");
        OrdersRequest input = new OrdersRequest("test_desc", 123.3);

        Orders savedOrder = new Orders("test_desc", 123.3, existingCustomer);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(ordersRepository.save(any(Orders.class))).thenReturn(savedOrder);

        // WHEN
        OrdersResponse result = ordersService.createOrder(customerId, input);

        // THEN – wynik metody
        assertThat(result).isNotNull();
        assertThat(result.description()).isEqualTo("test_desc");
        assertThat(result.amount()).isEqualTo(123.3);

        // THEN – interakcje + zawartość zapisywanej encji
        verify(customerRepository).findById(customerId);

        ArgumentCaptor<Orders> captor = ArgumentCaptor.forClass(Orders.class);
        verify(ordersRepository).save(captor.capture());

        Orders passed = captor.getValue();
        assertThat(passed.getDescription()).isEqualTo("test_desc");
        assertThat(passed.getAmount()).isEqualTo(123.3);
        assertThat(passed.getCustomer()).isEqualTo(existingCustomer);
    }


    @Test
    void shouldThrowWhenCustomerDoesNotExist() {

        // GIVEN
        Long customerId = 1L;

        OrdersRequest input = new OrdersRequest("test_desc", 123.3);

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ordersService.createOrder(customerId, input))
                .isInstanceOf(CustomerNotFoundException.class);

        verify(customerRepository).findById(customerId);
        verify(ordersRepository, never()).save(any());

    }

    @Test
    void shouldFindOrdersForCustomerWithPaginationAndMapping() {

        // GIVEN
        Long customerId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        Customer existingCustomer = new Customer(customerId, "Rafal", "rafal@example.com");

        Orders o1 = new Orders("desc1", 10.0, existingCustomer);
        Orders o2 = new Orders("desc2", 20.0, existingCustomer);

        Page<Orders> pageFromRepo = new PageImpl<>(
                List.of(o1, o2),
                pageable,
                2
        );

        when(ordersRepository.findByCustomerId(customerId, pageable))
                .thenReturn(pageFromRepo);

        // WHEN
        Page<OrdersResponse> resultPage = ordersService.findOrdersForCustomer(customerId, pageable);

        // THEN — czy to jest Page i czy rozmiar się zgadza
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(2);
        assertThat(resultPage.getContent()).hasSize(2);

        // THEN — mapowanie pól (to jest najważniejsze)
        OrdersResponse r1 = resultPage.getContent().get(0);
        assertThat(r1.description()).isEqualTo("desc1");
        assertThat(r1.amount()).isEqualTo(10.0);

        OrdersResponse r2 = resultPage.getContent().get(1);
        assertThat(r2.description()).isEqualTo("desc2");
        assertThat(r2.amount()).isEqualTo(20.0);

        // THEN — verify repo
        verify(ordersRepository).findByCustomerId(customerId, pageable);
    }

}