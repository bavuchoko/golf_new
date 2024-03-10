package pjs.golf.app.competition.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pjs.golf.app.competition.dto.CompetitionRequestDto;
import pjs.golf.app.competition.entitiy.Competition;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {})
public interface CompetitionMapper {
    CompetitionMapper Instance = Mappers.getMapper(CompetitionMapper.class);

    Competition toEntity(CompetitionRequestDto competitionRequestDto);
}
