package com.changgou.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.websocket.server.PathParam;

@Controller
@RequestMapping(value = "/oauth")
public class LoginRedirect {

    /***
     * 跳转到登录页面
     * @return
     */
    @GetMapping(value = "/login")
    public String login(@RequestParam(required = false,defaultValue = "",value = "From") String From, Model model){

        model.addAttribute("From", From);
        return "login";
    }
}