package es.urjc.etsii.grafo.iudex.repositories;

import es.urjc.etsii.grafo.iudex.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserById(Long id);

    boolean existsUserByNickname(String nickname);

    boolean existsUserByEmail(String mail);

    Optional<User> findByNickname(String nickname);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserById(long id);
}
