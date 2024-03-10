package pjs.golf.app.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import pjs.golf.app.member.dto.MemberRequestDto;
import pjs.golf.app.member.entity.Member;
import pjs.golf.app.member.service.MemberService;
import pjs.golf.common.BaseControllerTest;
import pjs.golf.config.AppProperties;

import java.net.http.HttpHeaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class MemberControllerTest extends BaseControllerTest {

    @Autowired
    AppProperties appProperties;
    @Autowired
    MemberService memberService;
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Test
    public void createMember() throws Exception {

        MemberRequestDto member = MemberRequestDto.builder()
                .username(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .name(appProperties.getUserName())
                .build();

        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk());

    }

}