package pl.rafal.springboot_course.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    Page<Orders> findByCustomerId(Long customerId, Pageable pageable);

}
