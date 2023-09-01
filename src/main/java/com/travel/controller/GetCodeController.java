package com.travel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
public class GetCodeController {

    @ResponseBody
    @GetMapping("/login/oauth2/code/kakao")
    public void getCode(
            @RequestParam String code
    ) {
        log.info("redirect url 매핑 성공 code = {}", code);
    }
}
