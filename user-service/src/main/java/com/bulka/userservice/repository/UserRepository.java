package com.bulka.userservice.repository;

import com.bulka.userservice.dto.response.UserResponse;
import com.bulka.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("""
            select new com.bulka.userservice.dto.response.UserResponse(
            u.id,
            u.email,
            u.firstName,
            u.lastName,
            u.role,
            u.status
        )
        from User u
        where u.id = :id
    """)
    Optional<UserResponse> findUserResponseById(UUID id);

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
