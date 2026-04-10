package com.unievent.repository;

import com.unievent.entity.ClubMembership;
import com.unievent.entity.ClubMembership.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubMembershipRepository extends JpaRepository<ClubMembership, Long> {

    List<ClubMembership> findByClubId(Long clubId);

    List<ClubMembership> findByClubIdAndStatus(Long clubId, MembershipStatus status);

    List<ClubMembership> findByStudentId(Long studentId);

    Optional<ClubMembership> findByStudentIdAndClubId(Long studentId, Long clubId);
}
