package co.devskills.springbootboilerplate.controller;

import co.devskills.springbootboilerplate.dto.healt.HealthCheckResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping(value = "/ping")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<HealthCheckResponse> healthCheck(){
        return ResponseEntity.ok(new HealthCheckResponse("The service is up and running"));
    }
}
