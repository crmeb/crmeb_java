package com.zbkj.crmeb.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RedirectController {

    @RequestMapping("/")
    public String What(){
        return "redirect:/doc.html";
    }
}
