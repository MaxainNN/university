package io.mkalugin.university.repository;

import io.mkalugin.university.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для таблицы "users".
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Найти пользователя по имени.
     *
     * @param username имя пользователя
     * @return пользователь
     */
    Optional<User> findByUsername(String username);

    /**
     * Проверка существования пользователя по имени.
     *
     * @param username имя пользователя
     * @return true , если пользователь с указанным именем существует
     */
    boolean existsByUsername(String username);

    /**
     * Проверка существования пользователя по почте.
     *
     * @param email почта пользователя
     * @return true , если пользователь с указанным email существует
     */
    boolean existsByEmail(String email);
}
