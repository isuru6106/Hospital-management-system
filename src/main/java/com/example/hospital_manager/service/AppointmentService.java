
package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.AppointmentRequest;
import com.example.hospital_manager.dto.AppointmentResponse;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.Appointment;
import com.example.hospital_manager.model.AppointmentStatus;
import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.model.Role;
import com.example.hospital_manager.repository.AppointmentRepository;
import com.example.hospital_manager.repository.PatientRepository;
import com.example.hospital_manager.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            UserRepository userRepository) {

        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    // Book an appointment for the authenticated patient
    @Transactional
    public AppointmentResponse bookAppointment(
            AppointmentRequest request,
            String patientUsername) {

        AppUser patientUser = userRepository
                .findByUsername(patientUsername)
                .orElseThrow(() -> new ApiException(
                        "Patient account not found",
                        HttpStatus.NOT_FOUND
                ));

        if (patientUser.getRole() != Role.PATIENT) {
            throw new ApiException(
                    "Only patients can book appointments",
                    HttpStatus.FORBIDDEN
            );
        }

        Patient patient = patientRepository.findByUser(patientUser)
                .orElseThrow(() -> new ApiException(
                        "Patient profile not found for this account",
                        HttpStatus.NOT_FOUND
                ));

        AppUser doctor = userRepository.findById(request.doctorId())
                .orElseThrow(() -> new ApiException(
                        "Doctor not found",
                        HttpStatus.NOT_FOUND
                ));

        if (doctor.getRole() != Role.DOCTOR) {
            throw new ApiException(
                    "Selected user is not a doctor",
                    HttpStatus.BAD_REQUEST
            );
        }

        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDateTime(
                request.appointmentDateTime()
        );
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        Appointment saved = appointmentRepository.save(appointment);

        return toResponse(saved);
    }

    // Get appointments assigned to a doctor
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getDoctorAppointments(
            String doctorUsername) {

        AppUser doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> new ApiException(
                        "Doctor not found",
                        HttpStatus.NOT_FOUND
                ));

        return appointmentRepository.findByDoctor(doctor)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Update appointment status with ownership authorization
    @Transactional
    public AppointmentResponse updateAppointmentStatus(
            Long appointmentId,
            AppointmentStatus status,
            String username) {

        AppUser currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(
                        "User not found",
                        HttpStatus.NOT_FOUND
                ));

        Appointment appointment = appointmentRepository
                .findById(appointmentId)
                .orElseThrow(() -> new ApiException(
                        "Appointment not found",
                        HttpStatus.NOT_FOUND
                ));

        // ADMIN can update any appointment.
        // DOCTOR can update only their assigned appointments.
        if (currentUser.getRole() == Role.DOCTOR) {

            if (!appointment.getDoctor().getId()
                    .equals(currentUser.getId())) {

                throw new ApiException(
                        "You can only update your own appointments",
                        HttpStatus.FORBIDDEN
                );
            }

        } else if (currentUser.getRole() != Role.ADMIN) {

            throw new ApiException(
                    "You are not authorized to update appointments",
                    HttpStatus.FORBIDDEN
            );
        }

        appointment.setStatus(status);

        Appointment updated = appointmentRepository.save(appointment);

        return toResponse(updated);
    }

    // Convert Appointment entity into AppointmentResponse DTO
    private AppointmentResponse toResponse(Appointment appointment) {

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getName(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getUsername(),
                appointment.getAppointmentDateTime(),
                appointment.getStatus()
        );
    }
}
