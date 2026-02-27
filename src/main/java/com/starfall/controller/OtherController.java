package com.starfall.controller;

import com.starfall.entity.ResultMsg;
import com.starfall.service.OtherService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.io.IOException;

@RestController
@RequestMapping("/other")
public class OtherController {

    @Autowired
    OtherService otherService;

    @GetMapping("/getCodeImage")
    public void getCodeImage(
            HttpServletRequest req,
            HttpServletResponse resp
    ) throws IOException {
        otherService.getCodeImage(req,resp);
    }

}
