package pjs.golf.app.game.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.game.dto.GameStatus;
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

    private int course;
    private int hole;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime playDate;

    @ManyToOne
    @JoinColumn(name = "filed")
    private Fields field;

    @ManyToMany
    @JoinTable(name = "game_players")
    private List<Account> players;

    @OneToMany(mappedBy = "game", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Sheet> sheets;


    @Enumerated(EnumType.STRING)
    private GameStatus status;

    public void enrollGame(Game game, Account account) {
        this.players.add(account);
    }

    public void changeStatus(GameStatus gameStatus) {
        this.status = gameStatus;
    }

    public void startGame(int course, int round, int hole){
        this.course =course;
        this.round = round;
        this.hole = hole;
        this.status = GameStatus.PLAYING;
    }
    public void updateRound(int  round) {
        this.round =round;
    }
    public void progressRound() {

        int base = 9;
        int remain = this.hole % base;

        if( remain != 0){ //마지막홀이 아님
            if(round % base == 0){ //홀은 중간인데 라운드는 해당 코스의 마지막 라운드임
                ++hole;
            }else{ //홀 번호도 중간이고 해당 코스 진행중임.
                ++hole;
            }
        }else{ //마지막 홀을 치고있음
            hole =1;
        }
        ++round;
        this.course = ((this.round - 1)/9) + 1;
    }
}
