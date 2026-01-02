package pl.rafal.springboot_course.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CustomerService customerService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldReturnCustomersPage() throws Exception {

        // GIVEN
        Customer c1 = new Customer(1L, "Rafal", "rafal@example.com");
        Customer c2 = new Customer(2L, "Jan", "jan@example.com");

        Page<Customer> pageFromService = new PageImpl<>(
                List.of(c1, c2),
                PageRequest.of(0, 10, Sort.by("id").descending()),
                2
        );

        when(customerService.findAll(any(Pageable.class))).thenReturn(pageFromService);

        // WHEN + THEN
        mockMvc.perform(get("/api/customers")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // sprawdzamy content
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Rafal"))
                .andExpect(jsonPath("$.content[0].email").value("rafal@example.com"))
                // page metadata
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }


    @Test
    void shouldReturn404WhenCustomerNotFound() throws Exception {

        Long id = 999L;

        when(customerService.getById(id))
                .thenThrow(new CustomerNotFoundException(id));

        mockMvc.perform(get("/api/customers/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Customer not found with id: 999"));
    }


    @Test
    void shouldCreateCustomer() throws Exception {

        // GIVEN
        Customer created = new Customer(1L, "Rafal", "rafal@example.com");
        when(customerService.create(any(Customer.class))).thenReturn(created);

        String json = """
        {
          "name": "Rafal",
          "email": "rafal@example.com"
        }
        """;

        // WHEN + THEN
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/customers/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Rafal"))
                .andExpect(jsonPath("$.email").value("rafal@example.com"));
    }


    @Test
    void shouldReturn400_whenEmailBlank() throws Exception {
        // given
        CustomerCreateRequest request =
                new CustomerCreateRequest("Jan", "");

        // when + then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists());

        verifyNoInteractions(customerService);
    }
}
