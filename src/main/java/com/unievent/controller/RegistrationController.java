package com.unievent.controller;

import com.unievent.entity.Registration;
import com.unievent.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "*")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<Registration> register(@RequestBody Registration registration) {
        try {
            return ResponseEntity.ok(registrationService.registerStudent(registration));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Registration>> getByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(registrationService.getRegistrationsByEvent(eventId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Registration>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(registrationService.getRegistrationsByStudent(studentId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Registration> updateStatus(@PathVariable Long id,
            @RequestParam Registration.RegistrationStatus status) {
        return ResponseEntity.ok(registrationService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelRegistration(@PathVariable Long id) {
        registrationService.cancelRegistration(id);
        return ResponseEntity.ok().build();
    }
}
