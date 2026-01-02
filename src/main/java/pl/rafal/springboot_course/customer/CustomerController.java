package pl.rafal.springboot_course.customer;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<Page<Customer>> getAll(Pageable pageable) {
        Page<Customer> page = customerService.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getById(@PathVariable Long id) {
        Customer customer = customerService.getById(id);
        return ResponseEntity.ok(customer);
    }


    @PostMapping
    public ResponseEntity<Customer> create(
            @RequestBody @Valid CustomerCreateRequest request) {

        Customer toCreate = new Customer(
                null,
                request.name(),
                request.email()
        );

        Customer created = customerService.create(toCreate);

        URI location = URI.create("/api/customers/" + created.getId());

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(
            @PathVariable Long id,
            @RequestBody @Valid CustomerUpdateRequest request) {

        Customer updated = new Customer(
                null,
                request.name(),
                request.email()
        );

        return customerService.update(id, updated)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean removed = customerService.delete(id);
        if (!removed) {
            throw new CustomerNotFoundException(id);
        }
        return ResponseEntity.noContent().build();
    }
}
