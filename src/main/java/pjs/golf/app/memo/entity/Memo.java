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
public class Memo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private int round;

    @ManyToOne
    @JoinColumn(name = "field")
    private Fields field;

    @ManyToOne
    @JoinColumn(name = "account")
    private Account account;
}
