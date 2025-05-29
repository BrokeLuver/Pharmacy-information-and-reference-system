package ru.store.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.store.pharmacy.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmail(String email);
}
