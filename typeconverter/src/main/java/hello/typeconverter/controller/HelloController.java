package hello.typeconverter.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello-v1")
    public String helloV1(HttpServletRequest request) {

        // HTTP 요청 파라미터는 모두 문자로 처리된다. 따라서 요청 파라미터를 자바에서 다른 타입으로 변환해서 사용하고 싶으면
        // 다음과 같이 숫자타입으로 변환하는 과정을 거처야 한다.
        String data = request.getParameter("data"); // 문자 타입으로 조회
        Integer intValue = Integer.valueOf(data); // 숫자 타입으로 변경
        System.out.println("intValue = " + intValue);
        return "ok";
    }

    @GetMapping("/hello-v2")
    /*
        HTTP 쿼리 스트링으로 전달하는 data=10 부분에서 10은 숫자 10이 아니라 문자 10이다.
        스프링이 제공하는 @RequestParam을 사용하면 이 문자 10을 Integer 타입의 숫자 10으로 스프링이 중간에서
        타입을 변환을 해서 Integer data 변수에 대입을 해준다.
    */
    public String helloV2(@RequestParam Integer data) {
        System.out.println("data = " + data);
        return "ok";
    }
}
