package pjs.golf.app.fields;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import pjs.golf.app.account.dto.AccountRequestDto;
import pjs.golf.app.account.entity.AccountRole;
import pjs.golf.app.account.entity.Gender;
import pjs.golf.app.account.service.AccountService;
import pjs.golf.app.fields.dto.FieldsRequestDto;
import pjs.golf.common.BaseControllerTest;

import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FieldsControllerTest extends BaseControllerTest {


    @Autowired
    AccountService accountService;

    @Test
    @Order(1)
    public void generateUser(){
        LocalDateTime joninDate = LocalDateTime.now();
        AccountRequestDto testUser = AccountRequestDto.builder()
                .username("user")
                .password("pass")
                .name("이름")
                .birth("6001011")
                .gender(Gender.MALE)
                .joinDate(joninDate)
                .roles(Set.of(AccountRole.USER))
                .build();
        this.accountService.createAccount(testUser);
    }

    private String getBaererToken() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        AccountRequestDto account = AccountRequestDto.builder().username("user")
                .password("pass").build();
        String token =this.accountService.authorize(account, response, request);
        return "Bearer " + token;
    }
    


    @Description("경기장 생성")
    public void createFieldBefore() throws Exception {

        String[] names = {"aaa"};
        FieldsRequestDto fieldsRequestDto = FieldsRequestDto.builder()
                .address("주소")
                .name("경기장명")
                .build();

        mockMvc.perform(post("/api/field")
                        .header(HttpHeaders.AUTHORIZATION, getBaererToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fieldsRequestDto)))
                .andExpect(status().isOk());
    }
    @Test
    @Description("경기장 생성")
    public void createField() throws Exception {

        String[] names = {"aaa"};
        FieldsRequestDto fieldsRequestDto = FieldsRequestDto.builder()
                .address("주소")
                .name("경기장명")
                .build();

        mockMvc.perform(post("/api/field")
                .header(HttpHeaders.AUTHORIZATION, getBaererToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fieldsRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @Description("경기장 조회")
    public void getFields() throws Exception {

        mockMvc.perform(get("/api/field")
                        .header(HttpHeaders.AUTHORIZATION, getBaererToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.fieldsResponseDtoList", hasSize(1)));
    }
    
}