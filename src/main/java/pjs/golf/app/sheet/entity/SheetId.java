package pjs.golf.app.sheet.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.game.entity.Game;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SheetId implements Serializable {

    private Game game;
    private int course;
    private int hole;
    private Account player;
}
