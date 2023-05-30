package com.springecomm.springecomm.Repository;

import com.springecomm.springecomm.Entity.CartItem;
import com.springecomm.springecomm.Entity.Product;
import com.springecomm.springecomm.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface CartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    CartItem findByUserAndProduct(User user, Product product);


}
