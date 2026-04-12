package hello.login.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId";

    /*
        preHandle(): 컨트롤러 호출 전에 호출된다. (더 정확히는 핸들러 어댑터 호출 전에 호출된다.)
            preHandle의 응답값이 true이면 다음으로 진행하고, false이면 더는 진행하지 않는다.
            false인 경우 나머지 인터셉터는 물론이고, 핸들러 어댑터도 호출되지 않는다.(따라서 컨트롤러도 호출되지 않게 됨)
                핸들러 어댑터: DispatcherServlet이 다양한 형태의 컨트롤러를 직접 호출하지 못하기 때문에,
                    해당 컨트롤러에 맞는 방식으로 실행해주는 어댑터 역할을 한다. 즉, 컨트롤러 호출 방식을 통일하여 실행을 담당하는 중간 객체이다.
    */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        String logId = UUID.randomUUID().toString();

        request.setAttribute(LOG_ID, logId);

        // @RequestMapping: HandlerMethod
        // 정적 리소스: ResourceHttpRequestHandler
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler; // 호출할 컨트롤러 메서드의 모든 정보가 포함되어 있다.
        }

        log.info("REQUEST [{}][{}][{}]", logId, requestURI, handler);
        return true;
    }

    // postHandler()는 컨트롤러 호출 후에 호출된다. (더 정확히는 핸들러 어댑터 호줄 후에 호출된다.)
    // postHandler()는 컨트롤러에서 예외가 발생하면 호출되지 않는 메서드다.
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle [{}]", modelAndView);
    }

    // afterCompletion()은 뷰가 렌더링 된 이후에 호출된다.
    // afterCompletion()은 항상 호출이 되는 메서드이다. 예외가 발생해도 호줄된다.
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String logId = (String) request.getAttribute(LOG_ID);
        log.info("RESPONSE [{}][{}][{}]", logId, requestURI, handler);
        if (ex != null) {
            log.error("afterCompletion error!!", ex);
        }
    }
}
