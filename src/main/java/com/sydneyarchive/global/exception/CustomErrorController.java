package com.sydneyarchive.global.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class CustomErrorController implements ErrorController {

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    @GetMapping("/error")
    public void handleError(HttpServletResponse response) throws IOException {
        response.sendRedirect(frontendBaseUrl + "/error?code=" + ErrorCode.INTERNAL_SERVER_ERROR.getCode().toLowerCase());
    }
}
