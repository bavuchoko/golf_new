package pjs.golf.app.account.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pjs.golf.app.account.entity.Account;

import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<Account, Long> {


    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.roles WHERE a.username = :username")
    Optional<Account> findByUsernameWithRoles(@Param("username") String username);

    Optional<Account> findByUsername(String username);

    Page<Account> findAll(Pageable pageable);

}
