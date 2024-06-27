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
@IdClass(SheetId.class)
public class Sheet {

    @Id
    @ManyToOne
    private Game game;

    @Id
    private int hole;
    @Id
    private int course;

    private int round;

    @Id
    @ManyToOne
    @JoinColumn(name = "player")
    private Account player;



    private int hit;


    public void updateHit(int hit, int course, int round, int hole) {
        this.course =course;
        this.round =round;
        this.hole =hole;
        this.hit =hit;
    }
}
