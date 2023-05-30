package com.springecomm.springecomm.Repository;

import com.springecomm.springecomm.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
