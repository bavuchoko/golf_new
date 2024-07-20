package pjs.golf.app.account.mapper;


import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import pjs.golf.app.account.dto.AccountRequestDto;
import pjs.golf.app.account.dto.AccountResponseDto;
import pjs.golf.app.account.entity.Account;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = {})
public interface AccountMapper {

    AccountMapper Instance = Mappers.getMapper(AccountMapper.class);

    Account toEntity(AccountRequestDto accountRequestDto);

    @Named("accountResponse")
    @Mapping(source = "roles", target = "roles", ignore = true)
    @Mapping(source = "password", target = "password", ignore = true)
    AccountResponseDto AccountResponse(Account account);


    @Named("accountResponseList")
    @IterableMapping(qualifiedByName = "accountResponse")
    List<AccountResponseDto> accountResponseList(List<Account> account);
}