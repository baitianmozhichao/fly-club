package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @auther: mzc
 */
@Controller
public class IndexController extends BaseController {

    @RequestMapping({"", "/", "/index"})
    public String index () {
        return "index";
    }
}
