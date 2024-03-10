package pjs.golf.app.sheet.dto;

import lombok.*;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.member.entity.Member;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SheetRequestDto {
    private Long id;
    private Game game;
    private Member player;
    private int round;
    private int score;
}
