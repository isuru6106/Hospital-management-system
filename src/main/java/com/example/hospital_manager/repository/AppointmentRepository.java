
package com.example.hospital_manager.repository;

import com.example.hospital_manager.model.Appointment;
import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctor(AppUser doctor);

    List<Appointment> findByPatient(Patient patient);

    boolean existsByPatient(Patient patient);
}
