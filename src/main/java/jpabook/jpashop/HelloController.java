package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("data", "hello!");
        model.addAttribute("data2", "반갑습니다!");
        // hello.html을 렌더링
        // data, data2를 hello.html에 함께 보냄
        return "hello";
    }

}
