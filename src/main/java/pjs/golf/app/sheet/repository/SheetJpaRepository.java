package pjs.golf.app.sheet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.member.entity.Member;
import pjs.golf.app.sheet.entity.Sheet;

import java.util.List;
import java.util.Optional;

public interface SheetJpaRepository extends JpaRepository<Sheet, Long> {


    List<Sheet> findAllByGame(Game game);
    Optional<Sheet> findByGameAndPlayerAndRound(Game game, Member member, int round);
}
