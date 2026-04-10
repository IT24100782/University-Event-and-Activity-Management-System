package com.unievent.service;

import com.unievent.entity.Club;
import com.unievent.entity.Role;
import com.unievent.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClubService {

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Create a club with role-based rules:
     * - ADMIN: auto-approved, can register multiple clubs
     * - CLUB_ADMIN: only one club allowed, requires admin approval (PENDING)
     */
    public Club createClub(Club club, Role creatorRole) {
        if (creatorRole == Role.ADMIN) {
            // Admin-created clubs are auto-approved and active
            club.setApprovalStatus(Club.ApprovalStatus.APPROVED);
            club.setActive(true);
        } else {
            // CLUB_ADMIN: enforce one-club limit
            long existingCount = clubRepository.findByPresidentEmail(club.getPresidentEmail())
                    .stream()
                    .filter(c -> !c.isDeleted())
                    .count();
            if (existingCount > 0) {
                throw new RuntimeException("You have already registered a club. Only one club per Club Admin is allowed.");
            }
            // Club admin clubs need approval
            club.setApprovalStatus(Club.ApprovalStatus.PENDING);
            club.setActive(false);
        }
        return clubRepository.save(club);
    }

    /**
     * Backward-compatible overload — defaults to CLUB_ADMIN behavior
     */
    public Club createClub(Club club) {
        return createClub(club, Role.CLUB_ADMIN);
    }

    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    public List<Club> getActiveClubs() {
        return clubRepository.findByActive(true);
    }

    public List<Club> getClubsByCategory(String category) {
        return clubRepository.findByCategory(category);
    }

    public Optional<Club> getClubById(Long id) {
        return clubRepository.findById(id);
    }

    public Club updateClub(Long id, Club clubDetails) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + id));

        club.setName(clubDetails.getName());
        club.setCategory(clubDetails.getCategory());
        club.setDescription(clubDetails.getDescription());
        club.setPresidentEmail(clubDetails.getPresidentEmail());
        club.setEmail(clubDetails.getEmail());
        club.setLogoUrl(clubDetails.getLogoUrl());
        club.setCoverUrl(clubDetails.getCoverUrl());
        club.setActive(clubDetails.isActive());
        club.setPlannedActivities(clubDetails.getPlannedActivities());
        club.setEventFrequency(clubDetails.getEventFrequency());
        club.setMembershipType(clubDetails.getMembershipType());

        return clubRepository.save(club);
    }

    public void deleteClub(Long id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + id));
        // Soft delete: mark as deleted so it no longer appears anywhere
        club.setActive(false);
        club.setDeleted(true);
        clubRepository.save(club);
    }

    // ============ ADMIN METHODS ============

    /**
     * Get all pending clubs (newly registered, awaiting approval)
     */
    public List<Club> getPendingClubs() {
        return clubRepository.findByApprovalStatus(Club.ApprovalStatus.PENDING);
    }

    /**
     * Get all approved clubs
     */
    public List<Club> getApprovedClubs() {
        return clubRepository.findAll().stream()
                .filter(c -> c.getApprovalStatus() == Club.ApprovalStatus.APPROVED && c.isActive() && !c.isDeleted())
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Get clubs by approval status
     */
    public List<Club> getClubsByApprovalStatus(Club.ApprovalStatus status) {
        return clubRepository.findByApprovalStatus(status);
    }

    /**
     * Approve a club registration
     */
    public Club approveClub(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + clubId));
        club.setApprovalStatus(Club.ApprovalStatus.APPROVED);
        club.setActive(true);
        Club savedClub = clubRepository.save(club);

        // Send approval email with PDF attachment
        try {
            emailService.sendClubApprovalEmail(savedClub);
        } catch (Exception e) {
            System.err.println("Failed to send approval email: " + e.getMessage());
        }

        return savedClub;
    }

    /**
     * Reject a club registration
     */
    public Club rejectClub(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + clubId));
        club.setApprovalStatus(Club.ApprovalStatus.REJECTED);
        club.setActive(false);
        return clubRepository.save(club);
    }

    public List<Club> getClubsByPresidentEmail(String email) {
        // Return all non-deleted clubs for this president (PENDING, APPROVED, REJECTED)
        // Deleted clubs are excluded so they don't appear in the admin's My Clubs view.
        return clubRepository.findByPresidentEmail(email).stream()
                .filter(c -> !c.isDeleted())
                .collect(java.util.stream.Collectors.toList());
    }
}
