package com.unievent.controller;

import com.unievent.entity.ClubMembership;
import com.unievent.service.ClubMembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership")
@CrossOrigin(origins = "*")
public class ClubMembershipController {

    @Autowired
    private ClubMembershipService membershipService;

    @PostMapping("/join/{studentId}/{clubId}")
    public ResponseEntity<ClubMembership> requestToJoin(
            @PathVariable Long studentId, 
            @PathVariable Long clubId,
            @RequestParam(required = false) String reason) {
        try {
            return ResponseEntity.ok(membershipService.joinRequest(studentId, clubId, reason));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/pending/{clubId}")
    public ResponseEntity<List<ClubMembership>> getPendingRequests(@PathVariable Long clubId) {
        return ResponseEntity.ok(membershipService.getPendingRequests(clubId));
    }

    @GetMapping("/active/{clubId}")
    public ResponseEntity<List<ClubMembership>> getActiveMembers(@PathVariable Long clubId) {
        return ResponseEntity.ok(membershipService.getActiveMembers(clubId));
    }

    @PostMapping("/approve/{membershipId}")
    public ResponseEntity<ClubMembership> approveMembership(@PathVariable Long membershipId) {
        try {
            return ResponseEntity.ok(membershipService.approveMembership(membershipId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/reject/{membershipId}")
    public ResponseEntity<ClubMembership> rejectMembership(@PathVariable Long membershipId) {
        try {
            return ResponseEntity.ok(membershipService.rejectMembership(membershipId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMembership(@PathVariable Long id) {
        membershipService.deleteMembership(id);
        return ResponseEntity.ok().build();
    }
}
