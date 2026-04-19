package exception.servlet;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Slf4j
@Controller
public class ServletExController {

    @GetMapping("/error-ex")
    public void errorEx() {
        // Exception의 경우 서버 내부에서 처리할 수 없는 오류가 발생한 것으로 생각해서 HTTP 상태 코드 500을 반환한다.
        throw new RuntimeException("예외 발생!");
    }

    @GetMapping("/error-404")
    public void error404(HttpServletResponse response) throws IOException {
        /*
            WAS(sendError 호출 기록 확인) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(response.sendError())
            response.sendError()를 호출하면 response 내부에는 오류가 발생했다는 상태를 저장해둔다.
            그리고 서블릿 컨테이너는 고객에게 응답 전에 response에 sendError()가 호출되었는지 확인한다. 그리고 호출되었다면 설정한
            오류 코드에 맞추어 기본 오류 페이지를 보여준다.
        */
        response.sendError(404, "404 오류!");
    }

    @GetMapping("/error-400")
    public void error400(HttpServletResponse response) throws IOException {
        /*
            스프링 부트가 제공하는 오류 페이지 처리
                스프링 부트가 제공하는 BasicErrorController에 기본적이 로직이 모두 개발이 되어있다.
                그래서 개발자는 오류 페이지 화면만 BasicErrorController가 제공하는 툴과 우선순위에 따라서 등록하면 됨

            BasicErrorController의 처리 순서
            1. 뷰 템플릿
                resources/templates/error/500.html
                resources/templates/error/5xx.html

            2. 정적 리소스(static, public)
                resources/static/error/400.html
                resources/static/error/404.html
                resources/static/error/4xx.html

            3. 적용 대상이 없을 때 뷰 이름(error)
                resources/templates/error.html

            위의 경로 위치에 HTTP 상태 코드 이름의 뷰 파일을 넣어두면 해당 에러 발생시 에러 페이지가 화면에 보여지게 된다.
            뷰 템플릿이 정적 리소스보다 우선순이가 놓고, 404, 500처럼 구체적인 것이 4xx, 5xx처럼 덜 구체적인 것보다 우선순위가 높다.
        */
        response.sendError(400, "400 오류!");
    }

    @GetMapping("/error-500")
    public void error500(HttpServletResponse response) throws IOException {
        response.sendError(500);
    }
}
