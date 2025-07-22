package com.ahmad.ProductFinder.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingApp {

    @GetMapping("/")
    public String pingApp(){
        return "hello";
    }
}
