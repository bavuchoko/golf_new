package pjs.golf.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pjs.golf.app.account.dto.AccountRequestDto;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.account.service.AccountService;
import pjs.golf.app.fields.dto.FieldsRequestDto;
import pjs.golf.app.fields.entity.Fields;
import pjs.golf.app.fields.respository.FieldsJpaRepository;
import pjs.golf.app.fields.service.FieldsService;
import pjs.golf.app.game.dto.GameRequestDto;
import pjs.golf.app.game.dto.GameStatus;
import pjs.golf.app.game.service.GameService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;


public class TestHelper extends BaseControllerTest{



    public static void setupUserData(AccountService accountService) {
        //유저생성
        AccountRequestDto account_1 = AccountRequestDto.builder()
                .birth("196001011").username("test_user1")
                .name("유저1").password("pass").build();
        AccountRequestDto account_2 = AccountRequestDto.builder()
                .birth("196001011").username("test_user2")
                .name("유저2").password("pass").build();
        AccountRequestDto account_3 = AccountRequestDto.builder()
                .birth("196001011").username("test_user3")
                .name("유저3").password("pass").build();
        List<Account> list = accountService.getUserList();
        if(list.size()<2) {
            accountService.createAccount(account_1);
            accountService.createAccount(account_2);
            accountService.createAccount(account_3);
        }
    }



    public static String getBaererToken( AccountService accountService, String name) {
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        AccountRequestDto account = AccountRequestDto.builder().username(name)
                .password("pass").build();
        String token = accountService.authorize(account, response, request);
        return "Bearer " + token;
    }

    public static void setupGameData(GameService gameService, Fields fields) {
        Account account = Account.builder().id(1l).build();
        //유저생성
        if(gameService.getGameList().size()<1){
            GameRequestDto field = GameRequestDto.builder()
                    .field(fields)
                    .playDate(LocalDateTime.now())
                    .players(Arrays.asList(account))
                    .host(account)
                    .status(GameStatus.OPEN)
                    .build();
            gameService.createGame(field, account);
        }
    }


    public static void deleteUser(AccountService accountService) {
        //유저생성
        Account account_1 = Account.builder()
                .birth("196001011").username("test_user1")
                .name("유저1").password("pass").build();
        Account account_2 = Account.builder()
                .birth("196001011").username("test_user2")
                .name("유저2").password("pass").build();
        Account account_3 = Account.builder()
                .birth("196001011").username("test_user3")
                .name("유저3").password("pass").build();
        accountService.deleteAccount(account_1);
        accountService.deleteAccount(account_2);
        accountService.deleteAccount(account_3);
    }

    public static void setupFieldData(FieldsJpaRepository fieldsJpaRepository) {
        Account account = Account.builder().id(1l).build();
        Fields fields = Fields.builder().address("주소").addressDetail("디테일").name("경기장").courses(4).register(account).build();
        fieldsJpaRepository.save(fields);
    }

}
