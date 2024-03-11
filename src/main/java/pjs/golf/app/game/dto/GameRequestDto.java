package pjs.golf.app.game.dto;

import lombok.*;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.member.entity.Member;
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
    private Member host;
    private LocalDateTime playDate;
    private Fields field;
    private List<Member> players;
    private List<Sheet> sheets;

    private String[] names;
    private boolean isRemoved;
    private GameStatus status;
}
