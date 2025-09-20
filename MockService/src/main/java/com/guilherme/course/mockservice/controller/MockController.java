package com.guilherme.course.mockservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/response")
public class MockController {
    @GetMapping("/200")
    public ResponseEntity<String> goodResponse() {
        return ResponseEntity.ok("200");
    }

    @GetMapping("/500")
    public ResponseEntity<String> badResponse() {
        return ResponseEntity.internalServerError().body("500");
    }
}
