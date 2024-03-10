package pjs.golf.app.competition;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pjs.golf.app.competition.service.CompetitionService;

@RestController
@RequestMapping(value = "/api/competition", produces = "application/json;charset=UTF-8")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;

}
