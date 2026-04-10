package com.unievent.repository;

import com.unievent.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    List<Club> findByCategory(String category);

    List<Club> findByActive(boolean active);

    // Admin queries
    List<Club> findByApprovalStatus(Club.ApprovalStatus approvalStatus);

    List<Club> findByCategoryAndApprovalStatus(String category, Club.ApprovalStatus approvalStatus);

    List<Club> findByPresidentEmail(String email);
}
