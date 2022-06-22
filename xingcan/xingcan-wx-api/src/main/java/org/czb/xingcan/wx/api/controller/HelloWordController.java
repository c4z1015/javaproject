package org.czb.xingcan.wx.api.controller;

import org.czb.xingcan.db.domain.TestUser;
import org.czb.xingcan.db.service.TestUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HelloWordController {

    @GetMapping("/")
    public String hello() {
        return "微信云托管启用...HelloWord!";
    }
}
