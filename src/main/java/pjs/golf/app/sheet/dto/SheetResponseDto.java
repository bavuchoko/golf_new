package pjs.golf.app.sheet.dto;


import lombok.*;
import pjs.golf.app.game.dto.GameResponseDto;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.member.dto.MemberResponseDto;
import pjs.golf.app.member.entity.Member;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SheetResponseDto {
    private Long id;
    private GameResponseDto game;
    private MemberResponseDto player;
    private int round;
    private int score;
}
