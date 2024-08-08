package pjs.golf.app.game.dto;

import lombok.*;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.sheet.entity.Sheet;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameRequestDto {
    private Long id;
    private int round;

    private Account host;
    private int hole;
    private int course;
    private LocalDateTime playDate;
    private Fields field;
    private List<Account> players;
    private List<Sheet> sheets;

    private String[] names;
    private GameStatus status;
}
