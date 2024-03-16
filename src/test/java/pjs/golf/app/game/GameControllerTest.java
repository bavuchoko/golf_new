package pjs.golf.app.game;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import pjs.golf.app.game.dto.GameRequestDto;
import pjs.golf.app.game.dto.GameResponseDto;
import pjs.golf.app.account.dto.AccountRequestDto;
import pjs.golf.app.account.dto.AccountResponseDto;
import pjs.golf.app.account.entity.Gender;
import pjs.golf.app.account.entity.AccountRole;
import pjs.golf.app.account.mapper.AccountMapper;
import pjs.golf.app.account.service.AccountService;
import pjs.golf.common.BaseControllerTest;
import pjs.golf.common.TestHelper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GameControllerTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;


    private String getBaererToken() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        AccountRequestDto account = AccountRequestDto.builder().username("test_user1")
                .password("pass").build();
        String token =this.accountService.authorize(account, response, request);
        return "Bearer " + token;
    }

    private String getBaererToken2() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        AccountRequestDto account = AccountRequestDto.builder().username("test_user2")
                .password("pass").build();
        String token =this.accountService.authorize(account, response, request);
        return "Bearer " + token;
    }


    @Test
    @Order(1)
    public void setupUser() {
        TestHelper.setupUserData(accountService);
    }


    @Test
    @Order(2)
    @Description("경기 생성")
    public void createGameBefore() throws Exception {


        String[] names = {"aaa"};
        GameRequestDto game = GameRequestDto.builder()
                .names(names)
                .build();

        mockMvc.perform(post("/api/game")
                        .header(HttpHeaders.AUTHORIZATION, getBaererToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(game)));
    }

    @Test
    @Description("목록조회")
    public void getGameList() throws Exception {

        String startDate = "startDate=2023-01-01T00:00:00";
        String endDate = "endDate=2023-10-01T00:00:00";
        String sort = "sort=playDate,desc";

        mockMvc.perform(get("/api/game?"+startDate+"&"+endDate+"&"+sort)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
    }

    @Test
    @Description("단일조회")
    public void getSingleGame() throws Exception {

        mockMvc.perform(get("/api/game/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
    }

    @Test
    @Description("경기 생성")
    public void createGame() throws Exception {

        String[] names = {"memeber_1","bbb","ccc"};
        GameRequestDto game = GameRequestDto.builder()
                .names(names)
                .build();

        mockMvc.perform(post("/api/game")
                        .header(HttpHeaders.AUTHORIZATION, getBaererToken())
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
                    players.stream().forEach(e-> System.out.println(e.getName()));
                    assertTrue(userExists, "User with username 'test_user1' does not exist in the game");
                });
    }


    @Test
    @Description("경기참가")
    public void enrollAccount() throws Exception {
        mockMvc.perform(put("/api/game/enroll/{id}",1)
                        .header(HttpHeaders.AUTHORIZATION, getBaererToken2())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.players", hasSize(3)));
    }

}