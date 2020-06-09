package demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/secured")
public class SecuredController {

    @GetMapping
    public String index() {
        return "/secured/index";
    }

    @GetMapping("/admin")
    public String admin() {
        return "/secured/admin";
    }
}
