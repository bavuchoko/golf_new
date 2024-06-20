package pjs.golf.app.memo.entity;

import lombok.*;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.fields.entity.Fields;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MemoId implements Serializable {
    private int round;
    private Fields field;
    private Account account;
}
