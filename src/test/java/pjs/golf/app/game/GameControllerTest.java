package pjs.golf.app.game;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import pjs.golf.app.game.dto.GameRequestDto;
import pjs.golf.app.game.dto.GameResponseDto;
import pjs.golf.app.member.dto.MemberRequestDto;
import pjs.golf.app.member.dto.MemberResponseDto;
import pjs.golf.app.member.entity.Gender;
import pjs.golf.app.member.entity.Member;
import pjs.golf.app.member.entity.MemberRole;
import pjs.golf.app.member.mapper.MemberMapper;
import pjs.golf.app.member.service.MemberService;
import pjs.golf.common.BaseControllerTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static javax.management.Query.or;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GameControllerTest extends BaseControllerTest {

    @Autowired
    MemberService memberService;

    private String getBaererToken() throws Exception {
        return "Bearer " + getAccescToken();
    }
    private String getAccescToken() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        String username = "9301234569";
        String password = "1234";
        LocalDateTime joninDate = LocalDateTime.now();
        String name = "이름";
        MemberRequestDto testUser = MemberRequestDto.builder()
                .username(username)
                .password(password)
                .name(name)
                .birth("6001011")
                .gender(Gender.MALE)
                .joinDate(joninDate)
                .roles(Set.of(MemberRole.USER))
                .build();
        this.memberService.createMember(MemberMapper.Instance.toEntity(testUser));
        String Token = this.memberService.authorize(testUser, response, request);
        return Token;
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
                    .andExpect(status().isNoContent());
    }

    @Test
    public void createMember() throws Exception {

        String[] names = {"이름","memeber_1","bbb","ccc"};
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

                    // 게임 정보에서 플레이어 목록을 가져옵니다.
                    List<MemberResponseDto> players = gameResponseDto.getPlayers();

                    // 플레이어 목록을 순회하면서 username이 "9301234569"인 플레이어가 있는지 확인합니다.
                    boolean userExists = players.stream()
                            .anyMatch(playerDto -> "9301234569".equals(playerDto.getUsername()));

                    players.stream().forEach(e-> System.out.println(e.getName()));
                    // 해당 유저가 존재하는지 확인합니다.
                    assertTrue(userExists, "User with username '9301234569' does not exist in the game");
                });
    }

}