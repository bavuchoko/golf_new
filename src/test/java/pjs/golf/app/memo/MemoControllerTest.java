package pjs.golf.app.memo;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import pjs.golf.app.account.service.AccountService;
import pjs.golf.app.fields.respository.FieldsJpaRepository;
import pjs.golf.app.fields.service.FieldsService;
import pjs.golf.common.BaseControllerTest;
import pjs.golf.common.TestHelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemoControllerTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Autowired
    FieldsJpaRepository fieldsJpaRepository;

    @Test
    @Order(1)
    public void setupUser() {
        TestHelper.setupUserData(accountService);
    }

    @Test
    @Order(2)
    public void setupField() {
        TestHelper.setupFieldData(fieldsJpaRepository);
    }

    @Test
    @Description("메모 조회")
    public void getMemoList() throws Exception {

        String token =TestHelper.getBaererToken(accountService, "test_user1");

        mockMvc.perform(get("/api/memo/{id}",1)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}