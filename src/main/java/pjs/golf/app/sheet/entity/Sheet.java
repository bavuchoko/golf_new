package pjs.golf.app.sheet.entity;

import jakarta.persistence.*;
import lombok.*;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.member.entity.Member;

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
    private Member player;
    private int round;
    private int score;
}
