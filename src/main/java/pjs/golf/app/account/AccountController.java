package pjs.golf.app.account;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import pjs.golf.app.account.dto.AccountRequestDto;
import pjs.golf.app.account.dto.AccountRequestDto;
import pjs.golf.app.account.entity.AccountRole;
import pjs.golf.app.account.mapper.AccountMapper;
import pjs.golf.app.account.service.AccountService;
import pjs.golf.app.account.entity.Account;
import pjs.golf.config.filter.TokenFilter;

import java.util.Set;


@RestController
@RequestMapping(value = "/api/user", produces = "application/json;charset=UTF-8")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/join")
    public ResponseEntity create(
            @Valid @RequestBody AccountRequestDto accountRequestDto
    ) {
        accountRequestDto.setRoles(Set.of(AccountRole.USER));
        Account userAccount = AccountMapper.Instance.toEntity(accountRequestDto);
        accountService.createAccount(userAccount);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/login")
    public ResponseEntity authenticate(
            @RequestBody AccountRequestDto accountRequestDto,
            HttpServletResponse response,
            HttpServletRequest request) {
        try {
            String accessToken = accountService.authorize(accountRequestDto,response, request);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(TokenFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);
            return new ResponseEntity(accessToken, httpHeaders, HttpStatus.OK);
        }catch (BadCredentialsException e){
            return new ResponseEntity<>("fail to login",HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/reissue")
    public ResponseEntity reissue(HttpServletRequest request) {

        try{
            String accessToken = accountService.reIssueToken(request);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(TokenFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);

            return new ResponseEntity(accessToken, httpHeaders, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity("fail to refresh token", HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/logout")
    public void logout(HttpServletRequest req, HttpServletResponse res){
        accountService.logout(req, res);
    }
}