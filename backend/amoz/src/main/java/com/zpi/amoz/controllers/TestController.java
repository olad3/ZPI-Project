package com.zpi.amoz.controllers;

import com.zpi.amoz.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/me")
    public ResponseEntity<Void> fetchMyPersonalData() {
        return ResponseEntity.status(214).build();
    }
}
