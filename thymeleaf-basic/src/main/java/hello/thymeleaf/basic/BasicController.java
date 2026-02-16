package hello.thymeleaf.basic;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/basic")
public class BasicController {

    @GetMapping("/text-basic")
    public String textBasic(Model model) {

        /**
         * HTML 엔티티
         * 웹 브라우저는 '<'를 HTML 태그의 시작으로 인식하는데 '<'를 태그의 시작이 아니라 문자로 표현할 수 있는 방법을 HTML 엔티티라고 한다.
         * 그래서 HTML에서 사용하는 특수 문자를 HTML 엔티티로 변경하는 것을 이스케이프(escape)라고 한다.
         * 그리고 타임리프가 제공하는 th:text, [[]]는 기본적으로 이스케이스를 제공하므로 아래의 data는 Spring!이 진하게 변경되는 것이 아니라
         * <b>Spring!</b> 이렇게 화면에 그대로 표시되게 된 것이다.
         * 타임리프는 이스케이프 기능을 사용하지 않도록 하는 th:utext, [()] 형태의 Unescape 문법이 존재한다.
         * 실제 서비스에서는 escape를 사용하지 않아서 HTML이 정상 렌더링이 되지 않는 많은 경우가 종종 발생한다. 그래서 escape를 기본적으로
         * 사용하고, 꼭 필요할 때만 unescape를 사용해야한다.
         */
        model.addAttribute("data", "Hello <b>Spring!</b>");
        return "basic/text-basic";
    }

    @GetMapping("/text-unescaped")
    public String textUnescaped(Model model) {
        model.addAttribute("data", "Hello <b>Spring!</b>");
        return "basic/text-unescaped";
    }
}
