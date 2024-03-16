package pjs.golf.app.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import pjs.golf.app.account.dto.AccountRequestDto;
import pjs.golf.app.account.service.AccountService;
import pjs.golf.common.BaseControllerTest;
import pjs.golf.common.TestHelper;
import pjs.golf.config.AppProperties;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class AccountControllerTest extends BaseControllerTest {

    @Autowired
    AppProperties appProperties;
    @Autowired
    AccountService accountService;
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Test
    @Description("회원가입")
    public void createAccount() throws Exception {

        AccountRequestDto account = AccountRequestDto.builder()
                .username(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .birth(appProperties.getUserBirth())
                .name(appProperties.getUserName())
                .build();

        mockMvc.perform(post("/api/user/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isOk());
    }

}