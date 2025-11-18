package utn.frc.piv.parcial.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utn.frc.piv.parcial.entities.Vote;
import utn.frc.piv.parcial.entities.VoteType;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUserIdAndProposalId(Long userId, Long proposalId);

    List<Vote> findByProposalId(Long proposalId);

    List<Vote> findByUserId(Long userId);

    boolean existsByUserIdAndProposalId(Long userId, Long proposalId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.proposal.id = :proposalId")
    Integer countByProposalId(@Param("proposalId") Long proposalId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.proposal.id = :proposalId AND v.vote = :voteType")
    Integer countByProposalIdAndVoteType(@Param("proposalId") Long proposalId, @Param("voteType") VoteType voteType);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.proposal.id = :proposalId AND v.vote = 'POSITIVE'")
    Integer countPositiveVotesByProposalId(@Param("proposalId") Long proposalId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.proposal.id = :proposalId AND v.vote = 'NEGATIVE'")
    Integer countNegativeVotesByProposalId(@Param("proposalId") Long proposalId);

    void deleteByProposalId(Long proposalId);
}