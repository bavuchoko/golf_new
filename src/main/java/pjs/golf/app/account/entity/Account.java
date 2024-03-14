package pjs.golf.app.account.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id", callSuper = false)
public class Account{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    @Column(unique = true)
    private String username;
    private String birth;
    @JsonIgnore
    private String password;
    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String portrait;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;

    public void overwritePassword(String password) {
        this.password = password;
    }
}
