package org.czb.xingcan.admin.api.controller;

import org.czb.xingcan.db.domain.TestUser;
import org.czb.xingcan.db.service.TestUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/testUser")
public class TestUserController {

    @Autowired
    private TestUserService testUserService;

    @GetMapping("/list")
    public List<TestUser> getAll(){
        return testUserService.list();
    }
}
