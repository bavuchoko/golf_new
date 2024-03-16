package pjs.golf.common;

import org.springframework.beans.factory.annotation.Autowired;
import pjs.golf.app.account.dto.AccountRequestDto;
import pjs.golf.app.account.entity.Account;
import pjs.golf.app.account.service.AccountService;

import java.util.List;


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
        if(list.size()<1) {
            accountService.createAccount(account_1);
            accountService.createAccount(account_2);
            accountService.createAccount(account_3);
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

    public static void setupFieldData() {
        //유저생성
        Account account_1 = Account.builder().name("유저1").password("pass").build();
        Account account_2 = Account.builder().name("유저2").password("pass").build();
        Account account_3 = Account.builder().name("유저3").password("pass").build();
    }

}
