package utn.frc.piv.parcial.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utn.frc.piv.parcial.entities.Proposal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    List<Proposal> findByIsActiveTrueOrderByStartDateAsc();

    Optional<Proposal> findByIdAndIsActiveTrue(Long id);

    @Query("SELECT p FROM Proposal p WHERE p.startDate <= :now AND p.endDate >= :now AND p.isActive = true")
    List<Proposal> findActiveProposals(@Param("now") LocalDateTime now);

    @Query("SELECT p FROM Proposal p WHERE p.endDate < :now AND p.isActive = true")
    List<Proposal> findFinishedProposals(@Param("now") LocalDateTime now);

    @Query("SELECT p FROM Proposal p WHERE p.startDate > :now AND p.isActive = true")
    List<Proposal> findFutureProposals(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.proposal.id = :proposalId")
    Integer countVotesByProposalId(@Param("proposalId") Long proposalId);
}