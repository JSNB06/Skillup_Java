package com.skillup.skillup.controller;

import com.skillup.skillup.Dto.NotificacionRequest;
import com.skillup.skillup.Dto.Response.ApiResponse;
import com.skillup.skillup.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @PostMapping("/email/estudiantes")
    public ResponseEntity<ApiResponse<String>> sendEmailToClients(
            @Validated @RequestBody NotificacionRequest request
    ) {

        ApiResponse<String> response = notificacionService.sendNotificationToAllClients(request);

        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }
}