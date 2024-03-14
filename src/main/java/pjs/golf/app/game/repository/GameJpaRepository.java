package pjs.golf.app.game.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pjs.golf.app.game.entity.Game;

public interface GameJpaRepository extends JpaRepository<Game, Long> {

}
 