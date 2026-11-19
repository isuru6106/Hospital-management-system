
package com.example.hospital_manager.controller;

import com.example.hospital_manager.dto.StatusRequest;
import com.example.hospital_manager.dto.AppointmentResponse;
import com.example.hospital_manager.service.AppointmentService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusRequest request,
            Authentication authentication) {

        AppointmentResponse response =
                appointmentService.updateAppointmentStatus(
                        id,
                        request.status(),
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
}
