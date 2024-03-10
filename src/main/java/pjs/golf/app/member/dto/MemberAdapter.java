package pjs.golf.app.member.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import pjs.golf.app.member.entity.Member;
import pjs.golf.app.member.entity.MemberRole;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MemberAdapter extends User {

    private Member member;

    public MemberAdapter(Member member) {
        super(member.getUsername(), member.getPassword(), authorities(member.getRoles()));
        this.member =member;
    }


    /**
     * Account 에 roles 의 FetchType.Lazy 일 경우 game컨트롤러의 createGame에서 LazyInitialized 예외 발생
     * */
    private static Collection<? extends GrantedAuthority> authorities(Set<MemberRole> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toSet());
    }

    public Member getMember() {
        return member;
    }

}
