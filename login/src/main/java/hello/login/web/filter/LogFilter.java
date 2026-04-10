package hello.login.web.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("log filter doFilter");

        // ServletRequest는 HttpServletRequest의 부모 인터페이스이므로 사용할 수 있는 기능이 별로 없어서 다운캐스팅을 하여 사용
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        StringBuffer requestURL = httpRequest.getRequestURL();

        String uuid = UUID.randomUUID().toString();

        try {
            log.info("REQUEST [{}][{}]", uuid, requestURL);
            // 서블릿을 호출하기 위해서는 반드시 다음 필터를 꼭 호출해줘야 함 안 그러면 여기에서 요청이 끝나게 되어 컨트롤러는 호출되지 않게 됨
            // chain.doFilter(request, response): 다음 필터가 있으면 다음 필터가 호출이 되고 없으면 서블릿이 호출됨
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e;
        } finally {
            log.info("RESPONSE [{}][{}]", uuid, requestURL);
        }

    }

    @Override
    public void destroy() {
        log.info("log filter destroy");
    }
}
