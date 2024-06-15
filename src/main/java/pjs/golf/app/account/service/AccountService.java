package pjs.golf.app.account.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.security.core.userdetails.UserDetailsService;
import pjs.golf.app.account.dto.AccountRequestDto;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.game.entity.Game;

import java.util.List;


public interface AccountService extends UserDetailsService {

    Account createAccount(AccountRequestDto accountRequestDto);

    String authorize(AccountRequestDto accountRequestDto, HttpServletResponse response, HttpServletRequest request);

    void logout(HttpServletRequest req, HttpServletResponse res);

    String reIssueToken(HttpServletRequest request, HttpServletResponse response);

    List<Account> getTempUsersByUserNames(List<String> names);

    List createUserIfDosenExist(List<String> name);

    void deleteAccount(Account account);

    List getUserList();

    Page<Account> getUserListPage(Pageable pageable);

    boolean validateToken(String token);
}
