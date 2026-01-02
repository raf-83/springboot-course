package pl.rafal.springboot_course.customer;

import pl.rafal.springboot_course.api.NotFoundException;

public class CustomerNotFoundException extends NotFoundException {

    public CustomerNotFoundException(Long id) {
        super("Customer not found with id: " + id);
    }
}
