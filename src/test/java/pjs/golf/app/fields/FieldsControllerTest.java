package pjs.golf.app.fields;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import pjs.golf.app.account.service.AccountService;
import pjs.golf.app.fields.dto.FieldsRequestDto;
import pjs.golf.common.BaseControllerTest;
import pjs.golf.common.TestHelper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FieldsControllerTest extends BaseControllerTest {


    @Autowired
    AccountService accountService;
    @Test
    @Order(1)
    public void setupUser() {
        TestHelper.setupUserData(accountService);
    }



    

    @Test
    @Description("경기장 생성")
    public void createField() throws Exception {

        String[] names = {"aaa"};
        FieldsRequestDto fieldsRequestDto = FieldsRequestDto.builder()
                .address("주소")
                .name("경기장명")
                .build();

        String token =TestHelper.getBaererToken(accountService, "test_user1");

        mockMvc.perform(post("/api/field")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fieldsRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @Description("경기장 조회")
    public void getFields() throws Exception {

        String token =TestHelper.getBaererToken(accountService, "test_user1");
        mockMvc.perform(get("/api/field")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._embedded.fieldsResponseDtoList", hasSize(1)));
    }
    
}