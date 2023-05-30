package com.springecomm.springecomm.Repository;

import com.springecomm.springecomm.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
