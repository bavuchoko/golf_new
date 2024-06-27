package pjs.golf.app.memo.dto;

import lombok.*;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.account.entity.Account;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoRequestDto {
    private int round;
    private Fields field;
    private Account account;
    private int hole;
    private int course;
    private String content;
}
