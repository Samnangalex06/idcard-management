package com.example.idcard_management.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "<h1>ID Card Management</h1>"
             + "<p>Docker Compose deployment is working.</p>"
             + "<p>NGINX proxy is working on port 8443.</p>"
             + "<p>Database: B-Sam_Nangalex-db</p>";
    }
}
