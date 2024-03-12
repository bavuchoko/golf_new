package pjs.golf.app.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pjs.golf.app.member.entity.Member;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {


    @Query("SELECT a FROM Member a LEFT JOIN FETCH a.roles WHERE a.username = :username")
    Optional<Member> findByUsernameWithRoles(@Param("username") String username);

    Optional<Member> findByUsername(String username);

}
