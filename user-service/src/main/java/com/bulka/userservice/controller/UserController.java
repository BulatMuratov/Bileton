package com.bulka.userservice.controller;

import com.bulka.userservice.dto.response.UserInfoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/")
public class UserController {

    @GetMapping("/me")
    public UserInfoResponse getUserInfo() {
        return null;
    }

    /*
        TODO: получать информацию по id может только status=ADMIN
    */
    @GetMapping("/{id}")
    public UserInfoResponse getUserInfo(@RequestParam UUID id) {
        return null;
    }

}
