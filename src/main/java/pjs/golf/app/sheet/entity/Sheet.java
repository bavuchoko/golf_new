package pjs.golf.app.sheet.entity;

import jakarta.persistence.*;
import lombok.*;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.account.entity.Account;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id", callSuper = false)
public class Sheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Game game;

    @ManyToOne
    @JoinColumn(name = "player")
    private Account player;
    private int round;
    private int hit;


    public void updateHit(int hit) {
        this.hit =hit;
    }
}
