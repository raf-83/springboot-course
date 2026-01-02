package pl.rafal.springboot_course.customer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldCreateCustomerWithNullIdAndReturnSaved() {
        // given
        Customer input = new Customer(123L, "Rafal", "rafal@example.com"); // id ustawione, ale serwis ma je nadpisać na null
        Customer saved = new Customer(1L, "Rafal", "rafal@example.com");

        when(customerRepository.save(any(Customer.class))).thenReturn(saved);

        // when
        Customer result = customerService.create(input);

        // then
        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(captor.capture());

        Customer passedToSave = captor.getValue();
        assertThat(passedToSave.getId()).isNull(); // serwis powinien wyczyścić id
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Rafal");
    }

    @Test
    void shouldUpdateExistingCustomer() {
        // given
        Long id = 1L;
        Customer existing = new Customer(id, "Old Name", "old@example.com");
        Customer updated = new Customer(null, "New Name", "new@example.com");
        Customer saved = new Customer(id, "New Name", "new@example.com");

        when(customerRepository.findById(id)).thenReturn(Optional.of(existing));
        when(customerRepository.save(existing)).thenReturn(saved);

        // when
        Optional<Customer> resultOpt = customerService.update(id, updated);

        // then
        assertThat(resultOpt).isPresent();
        Customer result = resultOpt.get();
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getEmail()).isEqualTo("new@example.com");

        verify(customerRepository).findById(id);
        verify(customerRepository).save(existing);
    }

    @Test
    void shouldReturnEmptyWhenUpdatingNonExistingCustomer() {
        // given
        Long id = 99L;
        Customer updated = new Customer(null, "Name", "email@example.com");

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        // when
        Optional<Customer> resultOpt = customerService.update(id, updated);

        // then
        assertThat(resultOpt).isEmpty();
        verify(customerRepository).findById(id);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldDeleteExistingCustomer() {
        // given
        Long id = 1L;
        when(customerRepository.existsById(id)).thenReturn(true);

        // when
        boolean result = customerService.delete(id);

        // then
        assertThat(result).isTrue();
        verify(customerRepository).existsById(id);
        verify(customerRepository).deleteById(id);
    }

    @Test
    void shouldNotDeleteWhenCustomerDoesNotExist() {
        // given
        Long id = 1L;
        when(customerRepository.existsById(id)).thenReturn(false);

        // when
        boolean result = customerService.delete(id);

        // then
        assertThat(result).isFalse();
        verify(customerRepository).existsById(id);
        verify(customerRepository, never()).deleteById(anyLong());
    }
}
