package pjs.golf.app.account.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import pjs.golf.app.account.dto.AccountRequestDto;
import pjs.golf.app.account.dto.AccountResponseDto;
import pjs.golf.app.account.entity.Account;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = {})
public interface AccountMapper {

    AccountMapper Instance = Mappers.getMapper(AccountMapper.class);

    Account toEntity(AccountRequestDto accountRequestDto);

    @Named("accountResponse")
    @Mapping(source = "roles", target = "roles", ignore = true)
    @Mapping(source = "password", target = "password", ignore = true)
    AccountResponseDto AccountResponse(Account account);
}