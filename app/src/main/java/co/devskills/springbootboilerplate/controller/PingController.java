package co.devskills.springbootboilerplate.controller;

import co.devskills.springbootboilerplate.dto.DescriptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @GetMapping(value = "/ping")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DescriptionResponse> healthCheck(){
        return ResponseEntity.ok(new DescriptionResponse("The service is up and running"));
    }
}
