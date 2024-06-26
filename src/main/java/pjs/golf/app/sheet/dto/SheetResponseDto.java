package pjs.golf.app.sheet.dto;


import lombok.*;
import pjs.golf.app.game.dto.GameResponseDto;
import pjs.golf.app.account.dto.AccountResponseDto;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SheetResponseDto {
    private GameResponseDto game;
    private int round;
    private AccountResponseDto player;
    private int hole;
    private int hit;
}
