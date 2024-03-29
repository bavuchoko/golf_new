package pjs.golf.app.game.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.game.dto.GameStatus;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.sheet.entity.Sheet;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id", callSuper = false)
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int round;

    @ManyToOne
    @JoinColumn(name = "host")
    private Account host;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime playDate;

    @ManyToOne
    @JoinColumn(name = "filed")
    private Fields field;

    @ManyToMany
    @JoinTable(name = "game_players")
    private List<Account> players;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private List<Sheet> sheets;

    private boolean isRemoved;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    public void enrollGame(Game game, Account account) {
        this.players.add(account);
    }

    public void changeStatus(GameStatus gameStatus) {
        this.status = gameStatus;
    }

    public void removeGame() {
        this.isRemoved =true;
    }
    public void initRound(int  round) {
        this.round =round;
    }
}
