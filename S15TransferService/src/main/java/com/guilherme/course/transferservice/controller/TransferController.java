package com.guilherme.course.transferservice.controller;

import com.guilherme.course.transferservice.command.TransferCommand;
import com.guilherme.course.transferservice.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<Boolean> transfer(@RequestBody TransferCommand command) {
        return ResponseEntity.ok(transferService.transfer(command));
    }
}
