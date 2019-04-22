package com.example.config;

import com.example.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

/**
 * @auther: mzc
 */
@Slf4j
@Order(1000)
@Component
public class ContextStartup implements ApplicationRunner {

    @Autowired
    private ServletContext servletContext;

    @Autowired
    PostService postService;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        servletContext.setAttribute("base", servletContext.getContextPath());

        postService.initIndexWeekRank();
    }
}