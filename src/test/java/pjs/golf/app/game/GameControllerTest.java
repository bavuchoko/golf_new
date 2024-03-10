package pjs.golf.app.game;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import pjs.golf.app.game.dto.GameRequestDto;
import pjs.golf.app.member.dto.MemberRequestDto;
import pjs.golf.app.member.entity.Member;
import pjs.golf.app.member.service.MemberService;
import pjs.golf.common.BaseControllerTest;

import java.util.stream.IntStream;

import static javax.management.Query.or;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GameControllerTest extends BaseControllerTest {

    @Autowired
    MemberService memberService;



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
    @WithMockUser
    public void createMember() throws Exception {

        String[] names = {"memeber_1","bbb","ccc"};
        GameRequestDto game = GameRequestDto.builder()
                .names(names)
                .build();

        mockMvc.perform(post("/api/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(game)))
                .andExpect(status().isOk());
    }

}