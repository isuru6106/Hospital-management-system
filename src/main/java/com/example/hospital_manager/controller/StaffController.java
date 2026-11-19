
package com.example.hospital_manager.controller;

import com.example.hospital_manager.dto.RegisterRequest;
import com.example.hospital_manager.dto.UserResponse;
import com.example.hospital_manager.dto.AppointmentResponse;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.model.Role;
import com.example.hospital_manager.service.AuthService;
import com.example.hospital_manager.service.PatientService;
import com.example.hospital_manager.service.AppointmentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StaffController {

    private final PatientService patientService;
    private final AuthService authService;
    private final AppointmentService appointmentService;

    public StaffController(
            PatientService patientService,
            AuthService authService,
            AppointmentService appointmentService) {

        this.patientService = patientService;
        this.authService = authService;
        this.appointmentService = appointmentService;
    }

    // ADMIN: Retrieve all patients
    @GetMapping("/admin/patients")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Patient>> getAllPatients() {

        return ResponseEntity.ok(patientService.getAllPatients());
    }

    // ADMIN: Register a new doctor
    @PostMapping("/admin/doctors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> addDoctor(
            @Valid @RequestBody RegisterRequest request) {

        if (request.role() != Role.DOCTOR) {
            throw new ApiException(
                    "Only DOCTOR accounts can be created through this endpoint",
                    HttpStatus.BAD_REQUEST
            );
        }

        UserResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // DOCTOR: Retrieve their own appointments
    @GetMapping("/doctors/appointments")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<AppointmentResponse>> getDoctorAppointments(
            Authentication authentication) {

        return ResponseEntity.ok(
                appointmentService.getDoctorAppointments(
                        authentication.getName()
                )
        );
    }
}
