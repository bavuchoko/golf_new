package pjs.golf.app.sheet.dto;

import lombok.*;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.account.entity.Account;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SheetRequestDto {
    private Game game;
    private int round;
    private Account player;
    private int hit;
}
