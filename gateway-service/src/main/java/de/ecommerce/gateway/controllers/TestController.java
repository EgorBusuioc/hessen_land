package de.ecommerce.gateway.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author EgorBusuioc
 * 27.05.2025
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/admin/test")
    public ResponseEntity<String> testAdmin() {
        return new ResponseEntity<>("Hello Admin", HttpStatus.OK);
    }
}
