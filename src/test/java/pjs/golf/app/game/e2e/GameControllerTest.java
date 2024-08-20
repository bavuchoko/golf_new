package pjs.golf.app.game.e2e;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import pjs.golf.app.account.dto.AccountResponseDto;
import pjs.golf.app.account.service.AccountService;
import pjs.golf.app.game.dto.GameRequestDto;
import pjs.golf.app.game.dto.GameResponseDto;
import pjs.golf.app.game.dto.GameStatus;
import pjs.golf.common.BaseControllerTest;
import pjs.golf.common.TestHelper;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GameControllerTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;


    @Test
    @Order(1)
    public void setupUser() {
        TestHelper.setupUserData(accountService);
    }


    @Test
    @Order(2)
    @Description("경기 생성")
    public void 기존경기_생성() throws Exception {


        String[] names = {"aaa"};
        GameRequestDto game = GameRequestDto.builder()
                .names(names)
                .status(GameStatus.OPEN)
                .build();

        String token =TestHelper.getBaererToken(accountService, "test_user1");

        mockMvc.perform(post("/api/game")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(game)));
    }

    @Test
    @Description("목록조회")
    public void 목록조회() throws Exception {

        String startDate = "startDate=2023-01-01T00:00:00";
        String endDate = "endDate=2023-10-01T00:00:00";
        String sort = "sort=playDate,desc";

        mockMvc.perform(get("/api/game?"+startDate+"&"+endDate+"&"+sort)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
    }

    @Test
    @Description("단일조회")
    public void 단일조회() throws Exception {

        mockMvc.perform(get("/api/game/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
    }

    @Test
    @Description("경기 생성")
    public void 경기생성() throws Exception {

        String[] names = {"memeber_1","bbb","ccc"};
        GameRequestDto game = GameRequestDto.builder()
                .names(names)
                .build();


        String token =TestHelper.getBaererToken(accountService, "test_user1");

        mockMvc.perform(post("/api/game")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(game)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.players", hasSize(4)))
                .andDo(result -> {
                    String contentAsString = result.getResponse().getContentAsString();
                    GameResponseDto gameResponseDto = objectMapper.readValue(contentAsString, GameResponseDto.class);

                    List<AccountResponseDto> players = gameResponseDto.getPlayers();

                    boolean userExists = players.stream()
                            .anyMatch(playerDto -> "test_user1".equals(playerDto.getUsername()));
                    assertTrue(userExists, "User with username 'test_user1' does not exist in the game");
                });
    }


    @Test
    @Order(3)
    @Description("경기참가")
    public void 경기참가() throws Exception {

        String token =TestHelper.getBaererToken(accountService, "test_user2");

        mockMvc.perform(put("/api/game/enroll/{id}",1)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.players", hasSize(3)));
    }



    @Test
    @Order(4)
    @Description("경기시작")
    public void 경기시작() throws Exception {
        String token =TestHelper.getBaererToken(accountService, "test_user1");
        mockMvc.perform(put("/api/game/play/{id}",1)
                         .param("hole", "1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(GameStatus.PLAYING.toString()))
                .andExpect(jsonPath("$.round").value(1))
        ;

    }

    @Test
    @Description("경기종료")
    public void 경기종료() throws Exception {
        String token =TestHelper.getBaererToken(accountService, "test_user1");
        mockMvc.perform(put("/api/game/end/{id}",1)
                        .param("hole", "1")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        ;

        mockMvc.perform(get("/api/game/score/{id}", 1)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(GameStatus.END.toString()));
    }
}