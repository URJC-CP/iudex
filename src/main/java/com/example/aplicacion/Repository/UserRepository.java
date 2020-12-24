package com.example.aplicacion.Repository;

import com.example.aplicacion.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String mail);

    Optional<User> findByNickname(String nickname);
    Optional<User> findUserById(long id);
}
