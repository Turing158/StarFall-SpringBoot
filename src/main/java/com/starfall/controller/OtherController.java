package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.service.OtherService;
import com.starfall.util.VersionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
public class OtherController {

    @Autowired
    OtherService otherService;

    @GetMapping("/code/image/find")
    public void getCodeImage(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {
        otherService.getCodeImage(req,resp);
    }
}
