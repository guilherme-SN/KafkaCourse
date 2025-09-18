package com.guilherme.course.controller;

import com.guilherme.course.command.CreateProductCommand;
import com.guilherme.course.exceptions.ErrorMessage;
import com.guilherme.course.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/async")
    public ResponseEntity<String> createProductAsynchronously(@RequestBody CreateProductCommand command) {
        String productId = productService.createProductAsynchronously(command);
        return ResponseEntity.ok(productId);
    }

    @PostMapping("/sync")
    public ResponseEntity<Object> createProductSynchronously(@RequestBody CreateProductCommand command) {
        try {
            String productId = productService.createProductSynchronously(command);
            return ResponseEntity.ok(productId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(new ErrorMessage(new Date(), e.getMessage(), "/products"));
        }
    }
}
