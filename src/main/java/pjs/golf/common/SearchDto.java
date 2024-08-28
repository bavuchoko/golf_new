package pjs.golf.common;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDto {

    private String searchTxt;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String city;
    private String option;

}
