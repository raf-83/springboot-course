package pl.rafal.springboot_course.customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // na razie nic więcej nie potrzebujemy
}
