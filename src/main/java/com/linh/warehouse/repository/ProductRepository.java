package com.linh.warehouse.repository;

import com.linh.warehouse.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    boolean existsByCode(String code);
    List<Product> findBySupplierId(Integer supplierId);
}
