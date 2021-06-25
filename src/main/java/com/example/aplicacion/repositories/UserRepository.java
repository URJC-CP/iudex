package com.example.aplicacion.repositories;

import com.example.aplicacion.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserById(Long id);

    boolean existsUserByNickname(String nickname);

    boolean existsUserByEmail(String mail);

    Optional<User> findByNickname(String nickname);

    Optional<User> findUserById(long id);
}
