package pjs.golf.app.memo.entity;

import jakarta.persistence.*;
import lombok.*;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.account.entity.Account;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id", callSuper = false)
@IdClass(MemoId.class)
public class Memo {

    @Id
    private int round;

    @Id
    @ManyToOne
    @JoinColumn(name = "field")
    private Fields field;

    @Id
    @ManyToOne
    @JoinColumn(name = "account")
    private Account account;

    private String content;

    public void updateContent(String content) {
        this.content = content;
    }
}
