package hello.login.web.session;

import hello.login.domain.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.*;

public class SessionManagerTest {

    SessionManager sessionManager = new SessionManager();

    @Test
    void sessionTest() {

      /*
        HttpServletRequest, HttpservletResponse는 인터페이스이므로 스프링 부트를 실행하게 되면 톰캣에서 자동으로
        구현체를 만들어 줘서 테스트 코드에서 구현체를 만들기는 어려워서 비슷한 역할을 해주는 가짜 MockHttpServletResponse,
        MockHttpServletRequest를 사용함
      */

        // 세션 생성
        // 서버 -> 클라이언트
        // 세션이 정상적으로 완료 됐고 response를 통해 클라이언트의 쿠키에 해당 세션이 저장되었다고 가정
        MockHttpServletResponse response = new MockHttpServletResponse();
        Member member = new Member();
        sessionManager.createSession(member, response);

        // 요청에 응답 쿠키 저장
        // 클라이언트 -> 서버
        // 클라이언트에서 서버로 요청이 오는 상황이라서 해당 요청에 대해서는 쿠키에 새션이 저장되어 있어야 해서(세션 생성할 때 쿠키에 저장을 했으므로)
        // 그래서 request.setCookies()를 통해 쿠키를 설정해주는 코드가 있는 것
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(response.getCookies());

        // 세션 조회
        Object result = sessionManager.getSession(request);
        assertThat(result).isEqualTo(member);

        // 세션 만료
        sessionManager.expire(request);
        Object expired = sessionManager.getSession(request);
        assertThat(expired).isNull();
    }

}
