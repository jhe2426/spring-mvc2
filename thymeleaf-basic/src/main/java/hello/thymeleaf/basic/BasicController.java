package hello.thymeleaf.basic;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @GetMapping("/variable")
    public String variable(Model model) {
        User userA = new User("userA", 10);
        User userB = new User("userB", 20);

        List<User> list = new ArrayList<>();
        list.add(userA);
        list.add(userB);

        HashMap<String, User> map = new HashMap<>();
        map.put("userA", userA);
        map.put("userB", userB);

        model.addAttribute("user", userA);
        model.addAttribute("users", list);
        model.addAttribute("userMap", map);

        return "basic/variable";
    }

    @GetMapping("/basic-objects")
    public String basicObjects(
            Model model,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session
    ) {
        session.setAttribute("sessionData", "Hello Session");
        model.addAttribute("request", request);
        model.addAttribute("response", response);
        model.addAttribute("servletContext", request.getServletContext());
        return "basic/basic-objects";
    }

    @Component("helloBean")
    static class HelloBean {
        public String hello(String data) {
            return "Hello " + data;
        }
    }

    @GetMapping("/date")
    public String date(Model model) {
        model.addAttribute("localDateTime", LocalDateTime.now());
        return "basic/date";
    }

    @GetMapping("/link")
    public String link(Model model) {
        model.addAttribute("param1", "data1");
        model.addAttribute("param2", "data2");
        return "basic/link";
    }

    @GetMapping("/literal")
    public String literal(Model model) {
        model.addAttribute("data", "Spring!");
        return "basic/literal";
    }

    @GetMapping("/operation")
    public String operation(Model model) {
        model.addAttribute("nullData", null);
        model.addAttribute("data", "Spring!");
        return "basic/operation";
    }

    @GetMapping("attribute")
    public String attribute(Model model) {
        return "basic/attribute";
    }

    @GetMapping("/each")
    public String each(Model model) {
        addUsers(model);
        return "basic/each";
    }

    @GetMapping("/condition")
    public String condition(Model model) {
        addUsers(model);
        return "basic/condition";
    }

    @GetMapping("/comments")
    public String comments(Model model) {
        model.addAttribute("data", "Spring!");
        return "basic/comments";
    }

    private void addUsers(Model model) {
        List<User> list = new ArrayList<>();
        list.add(new User("UserA", 10));
        list.add(new User("UserB", 20));
        list.add(new User("UserC", 30));

        model.addAttribute("users", list);
    }

    @Data
    static class User {
        private String username;
        private int age;

        public User(String username, int age) {
            this.username = username;
            this.age = age;
        }
    }
}
