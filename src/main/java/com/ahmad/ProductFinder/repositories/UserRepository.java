package com.ahmad.ProductFinder.repositories;

import com.ahmad.ProductFinder.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
//     User findByUsername(String username);

     Optional<User> findByIdAndActiveTrue(Long userId);

     boolean existsByEmail(String email);

     Optional<User> findByEmail(String email);

     boolean existsByUsername(String username);

     List<User> findAllByActiveTrue();

     User findByUsernameAndActiveTrue(String username);

     boolean existsByUsernameAndActiveTrue(String username);
}
