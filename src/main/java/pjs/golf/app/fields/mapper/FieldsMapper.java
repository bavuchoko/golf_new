package pjs.golf.app.fields.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pjs.golf.app.account.mapper.AccountMapper;
import pjs.golf.app.fields.dto.FieldsRequestDto;
import pjs.golf.app.fields.dto.FieldsResponseDto;
import pjs.golf.app.fields.entity.Fields;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {AccountMapper.class})
public interface FieldsMapper {

    FieldsMapper Instance = Mappers.getMapper(FieldsMapper.class);

    Fields toEntity(FieldsRequestDto fieldsRequestDto);

    @Mapping(source = "register", target = "register", qualifiedByName = "accountResponse")
    FieldsResponseDto toDto(Fields fields);
}
