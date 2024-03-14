package pjs.golf.app.account.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import pjs.golf.app.account.dto.AccountRequestDto;
import pjs.golf.app.account.entity.Account;

import java.util.List;


public interface AccountService extends UserDetailsService {

    Account createAccount(Account account);

    String authorize(AccountRequestDto accountRequestDto, HttpServletResponse response, HttpServletRequest request);

    void logout(HttpServletRequest req, HttpServletResponse res);

    String reIssueToken(HttpServletRequest request);

    List<Account> getTempUsersByUserNames(List<String> names);

    List createUserIfDosenExist(List<String> name);
}
