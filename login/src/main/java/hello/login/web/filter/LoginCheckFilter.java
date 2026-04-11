package hello.login.web.filter;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class LoginCheckFilter implements Filter {

    private static final String[] whitelist = {"/", "/members/add", "/login", "logout", "/css/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            log.info("인증 체크 필터 시작 {}", requestURI);

            if (isLoginCheckPath(requestURI)) {
                log.info("인증 체크 로직 실행 {}", requestURI);
                HttpSession session = httpRequest.getSession(false);
                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {

                    log.info("미인증 사용자 요청 {}", requestURI);
                    // 로그인으로 redirect
                    /*
                        /login?redirectURL=" + requestURI
                        requestURI정보도 URL에 남기는 이유는 로그인페이지에서 로그인 완료 시 사용자가 처음에 들어가려고 했던 페이지로
                        이동시켜주기 위해서 해당 정보 또한 포함시키는 것이다.
                    */
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);
                    return; // doFilter()가 아닌 return을 하겠다는 것은 다음 서블릿이랑 컨트롤러를 호출하지 않고 해당 요청은 종료하겠다라는 의미
                }
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            /*
                필터에서 예외를 잡지 않고 던지는 이유는 요청은 크게 톰캣(서블릿 컨테이너, WAS) -> 필터 -> 서블릿 -> 컨트롤러 이런식으로 요청이 되는데
                여기 필터에서 예외를 잡아버리면 서블릿, 컨트롤러에서 예외가 발생하더라도 톰캣에는 내부적으로 예외가 터진 것을 알 수 있는 방법이
                없으므로 요청이 정상적으로 처리가 됐구나라고 판단을 하므로 필터에서 예외를 던지는 코드를 작성한 것
                    필터에서 예외를 잡는 경우
                        컨트롤러나 서블릿에서 예외 발생 -> 그 예외가 필터까지 올라옴 -> 그런데 필터 catch에서 그 예외를 잡아버림 ->
                        그럼 톰캣까지는 예외가 전달되지 않아 톰켓 입장에서는 정상 완료처럼 볼 수 있게 됨
            */
            throw e;
        } finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }
    }

    // 화이트 리스트의 경우 인증 체크X
    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }

}
