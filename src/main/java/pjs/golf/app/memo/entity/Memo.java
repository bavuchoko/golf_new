package pjs.golf.app.memo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.account.entity.Account;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MemoId.class)
public class Memo {

    @Id
    private int hole;
    @Id
    private int course;

    @Id
    @ManyToOne
    @JoinColumn(name = "field")
    private Fields field;

    @Id
    @ManyToOne
    @JoinColumn(name = "account")
    private Account account;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDate;

    private String content;

    public void updateContent(String content) {
        this.content = content;
    }
}
