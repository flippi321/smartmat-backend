package edu.ntnu.idatt2106_09.backend.authentication;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public void register() {
        //TODO Add code for a register request
    }

    @PostMapping("/authenticate")
    public void authenticate(@RequestBody AuthenticationRequest request) {
        //TODO Add code for a authentication request
    }
}