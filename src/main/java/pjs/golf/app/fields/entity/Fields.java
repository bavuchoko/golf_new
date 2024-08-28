package pjs.golf.app.fields.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import pjs.golf.app.fields.dto.FieldsRequestDto;
import pjs.golf.app.account.entity.Account;

import java.time.LocalDateTime;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id", callSuper = false)
public class Fields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String addressDetail;
    private String city;
    private Double latitude;
    private Double longitude;

    private int courses;
    private int holes;

    @ManyToOne
    @JoinColumn(name = "register")
    private Account register;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;


    private boolean isRemoved;

    public void removeField() {
        this.isRemoved = true;
    }
}
