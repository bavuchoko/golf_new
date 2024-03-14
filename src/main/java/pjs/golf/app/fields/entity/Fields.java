package pjs.golf.app.fields.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @ManyToOne
    @JoinColumn(name = "register")
    private Account register;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime playDate;

    private boolean isRemoved;

    public void removeField() {
        this.isRemoved = true;
    }

    public void updateField(FieldsRequestDto fieldsRequestDto) {

    }
}
