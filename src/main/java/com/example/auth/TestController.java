package com.example.auth;

import com.example.auth.interceptors.TestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/tests")
public class TestController {
    @GetMapping
    public String test() {
        return "done";
    }

    @PostMapping
    public String testBody(
            @RequestBody
            TestDto dto
    ) {
        log.info(dto.toString());
        return "done";
    }
}
