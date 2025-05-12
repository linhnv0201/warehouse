package com.linh.warehouse.repository;

import com.linh.warehouse.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    // You can add custom query methods if needed
}
