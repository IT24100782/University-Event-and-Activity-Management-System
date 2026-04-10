package com.unievent.service;

import com.unievent.entity.Club;
import com.unievent.entity.ClubMembership;
import com.unievent.entity.User;
import com.unievent.repository.ClubMembershipRepository;
import com.unievent.repository.ClubRepository;
import com.unievent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClubMembershipService {

    @Autowired
    private ClubMembershipRepository membershipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClubRepository clubRepository;

    public ClubMembership joinRequest(Long studentId, Long clubId, String reason) {
        // Prevent duplicate requests
        Optional<ClubMembership> existing = membershipRepository.findByStudentIdAndClubId(studentId, clubId);
        if (existing.isPresent()) {
            return existing.get();
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club not found"));

        ClubMembership membership = new ClubMembership();
        membership.setStudent(student);
        membership.setClub(club);
        membership.setReason(reason);
        membership.setJoinDate(LocalDateTime.now());
        membership.setStatus(ClubMembership.MembershipStatus.PENDING);

        return membershipRepository.save(membership);
    }

    public List<ClubMembership> getPendingRequests(Long clubId) {
        return membershipRepository.findByClubIdAndStatus(clubId, ClubMembership.MembershipStatus.PENDING);
    }

    public List<ClubMembership> getActiveMembers(Long clubId) {
        return membershipRepository.findByClubIdAndStatus(clubId, ClubMembership.MembershipStatus.APPROVED);
    }

    public ClubMembership approveMembership(Long membershipId) {
        ClubMembership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new RuntimeException("Membership request not found"));
        membership.setStatus(ClubMembership.MembershipStatus.APPROVED);
        return membershipRepository.save(membership);
    }

    public ClubMembership rejectMembership(Long membershipId) {
        ClubMembership membership = membershipRepository.findById(membershipId)
                .orElseThrow(() -> new RuntimeException("Membership request not found"));
        membership.setStatus(ClubMembership.MembershipStatus.REJECTED);
        return membershipRepository.save(membership);
    }

    public void deleteMembership(Long id) {
        membershipRepository.deleteById(id);
    }
}
