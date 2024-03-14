package pjs.golf.app.competition.entitiy;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.game.entity.Game;
import pjs.golf.app.account.entity.Account;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id", callSuper = false)
public class Competition {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "host")
    private Account host;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime playDate;

    @ManyToOne
    @JoinColumn(name = "filed")
    private Fields field;

    @OneToMany
    @JoinTable(name = "competitors")
    private List<Account> players;

    @OneToMany
    @JoinColumn(name = "competition")
    private List<Game> games;

}
