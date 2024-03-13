package pjs.golf.app.fields.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import pjs.golf.app.member.entity.Member;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldsRequestDto {
    private Long id;
    private String name;
    private String address;
    private String addressDetail;
    private Member register;
    private LocalDateTime createDate;
    private boolean isRemoved;
}
