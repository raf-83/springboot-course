package pl.rafal.springboot_course.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdersController.class) // <-- dopasuj nazwę kontrolera: OrderController vs OrdersController
class OrdersControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    OrdersService ordersService;

    @Test
    void shouldReturnOrdersForCustomerPage() throws Exception {

        // GIVEN
        Long customerId = 1L;

        OrdersResponse r1 = new OrdersResponse(101L, "desc1", 10.0, LocalDateTime.now());
        OrdersResponse r2 = new OrdersResponse(102L, "desc2", 20.0, LocalDateTime.now().minusDays(1));

        Page<OrdersResponse> pageFromService = new PageImpl<>(
                List.of(r1, r2),
                PageRequest.of(0, 10, Sort.by("createdAt").descending()),
                2
        );

        when(ordersService.findOrdersForCustomer(eq(customerId), any(Pageable.class)))
                .thenReturn(pageFromService);

        // WHEN + THEN
        mockMvc.perform(get("/api/customers/{customerId}/orders", customerId)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].description").value("desc1"))
                .andExpect(jsonPath("$.content[0].amount").value(10.0))
                .andExpect(jsonPath("$.content[1].description").value("desc2"))
                .andExpect(jsonPath("$.content[1].amount").value(20.0))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.number").value(0));
    }
}
