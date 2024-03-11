package pjs.golf.app.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pjs.golf.app.game.dto.GameRequestDto;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.member.entity.Member;

import java.util.List;

public interface GameJpaRepository extends JpaRepository<Game, Long> {

}
 