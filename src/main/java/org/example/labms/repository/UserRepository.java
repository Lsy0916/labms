package org.example.labms.repository;

import org.example.labms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
    
    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    Optional<User> findUserWithClassNameByUserId(@Param("userId") String userId);

    Optional<User> findByEmail(String email);
}