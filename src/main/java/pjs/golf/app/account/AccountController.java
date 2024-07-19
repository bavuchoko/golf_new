package pjs.golf.app.account;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import pjs.golf.app.account.dto.AccountRequestDto;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.account.service.AccountService;
import pjs.golf.common.exception.AlreadyExistSuchDataCustomException;
import pjs.golf.config.filter.TokenFilter;


@RestController
@RequestMapping(value = "/api/user", produces = "application/json;charset=UTF-8")
@RequiredArgsConstructor
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;

    @GetMapping
    public ResponseEntity getUsers(
            Pageable pageable
    ) {
        Page<Account> resources =  accountService.getUserListPage(pageable);
        return new ResponseEntity(resources, HttpStatus.OK);
    }

    @PostMapping("/join")
    public ResponseEntity create(
            @Valid @RequestBody AccountRequestDto accountRequestDto,
            HttpServletResponse response,
            HttpServletRequest request
    ) {
        try {
            accountService.createAccount(accountRequestDto);
            String accessToken = accountService.authorize(accountRequestDto,response, request);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(TokenFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);
            return new ResponseEntity(accessToken, httpHeaders, HttpStatus.OK);
        } catch (AlreadyExistSuchDataCustomException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.ACCEPTED);
        }
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
    public ResponseEntity reissue(HttpServletRequest request, HttpServletResponse response) {
        try{
            String accessToken = accountService.reIssueToken(request, response);
            log.info("reissue token is ={}", accessToken);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(TokenFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);

            return new ResponseEntity(accessToken, httpHeaders, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity("fail to refresh token", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/validation")
    public ResponseEntity valdationTimeCheck(@RequestHeader(name = "Authorization") String token) {
        log.info("validation token is ={}", token);
        if(StringUtils.hasText(token)){
            try {
                token = token.replace("Bearer ", "");
                if (accountService.validateToken(token)) {
                    return new ResponseEntity(token, HttpStatus.OK);                       //200
                }
            } catch (Exception e) {
                return new ResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN); //403
            }
        }
        return new ResponseEntity("token is empty", HttpStatus.UNAUTHORIZED); //401
    }


    @GetMapping("/logout")
    public void logout(HttpServletRequest req, HttpServletResponse res){
        accountService.logout(req, res);
    }



}
