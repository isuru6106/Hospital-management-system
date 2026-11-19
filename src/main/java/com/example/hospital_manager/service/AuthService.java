
package com.example.hospital_manager.service;

import com.example.hospital_manager.dto.AuthResponse;
import com.example.hospital_manager.dto.LoginRequest;
import com.example.hospital_manager.dto.RegisterRequest;
import com.example.hospital_manager.dto.UserResponse;
import com.example.hospital_manager.exception.ApiException;
import com.example.hospital_manager.model.AppUser;
import com.example.hospital_manager.model.Patient;
import com.example.hospital_manager.model.Role;
import com.example.hospital_manager.repository.PatientRepository;
import com.example.hospital_manager.repository.UserRepository;
import com.example.hospital_manager.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {

        // Check whether the username already exists
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new ApiException(
                    "Username already exists",
                    HttpStatus.CONFLICT
            );
        }

        // Prevent public registration as ADMIN
        if (request.role() == null ||
                (request.role() != Role.DOCTOR &&
                 request.role() != Role.PATIENT)) {

            throw new ApiException(
                    "Public registration is allowed only for DOCTOR and PATIENT roles",
                    HttpStatus.BAD_REQUEST
            );
        }

        // Create user and encode password
        AppUser user = new AppUser(
                request.name(),
                request.username(),
                passwordEncoder.encode(request.password()),
                request.role()
        );

        AppUser savedUser = userRepository.save(user);

        // Automatically create patient profile for PATIENT accounts
        if (savedUser.getRole() == Role.PATIENT) {

            Patient patient = new Patient();

            patient.setUser(savedUser);
            patient.setName(savedUser.getName());

            patientRepository.save(patient);
        }

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getUsername(),
                savedUser.getRole()
        );
    }

    public AuthResponse login(LoginRequest request) {

        // Authenticate username and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        // Retrieve authenticated user
        AppUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ApiException(
                        "User not found",
                        HttpStatus.NOT_FOUND
                ));

        // Generate JWT
        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationInSeconds(),
                user.getRole()
        );
    }
}
