package com.example.demo.systemconfig;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/index", "/dashboard"})
    public String index() {
        return "forward:/index.html";
    }
}
