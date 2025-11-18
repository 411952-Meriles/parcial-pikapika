package utn.frc.piv.parcial.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utn.frc.piv.parcial.dtos.VoteDTO;
import utn.frc.piv.parcial.entities.Proposal;
import utn.frc.piv.parcial.entities.Vote;
import utn.frc.piv.parcial.entities.VoteType;
import utn.frc.piv.parcial.exceptions.BusinessException;
import utn.frc.piv.parcial.exceptions.ResourceNotFoundException;
import utn.frc.piv.parcial.repositories.ProposalRepository;
import utn.frc.piv.parcial.repositories.VoteRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Vote Service Tests")
class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private ProposalRepository proposalRepository;

    @InjectMocks
    private VoteService voteService;

    private Proposal activeProposal;
    private Proposal inactiveProposal;
    private Proposal futureProposal;
    private Proposal pastProposal;
    private Vote testVote;
    private VoteDTO testVoteDTO;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        activeProposal = Proposal.builder()
                .id(1L)
                .title("Active Proposal")
                .description("Description")
                .startDate(now.minusHours(1))
                .endDate(now.plusHours(1))
                .isActive(true)
                .build();

        inactiveProposal = Proposal.builder()
                .id(2L)
                .title("Inactive Proposal")
                .description("Description")
                .startDate(now.minusHours(1))
                .endDate(now.plusHours(1))
                .isActive(false)
                .build();

        futureProposal = Proposal.builder()
                .id(3L)
                .title("Future Proposal")
                .description("Description")
                .startDate(now.plusHours(1))
                .endDate(now.plusHours(2))
                .isActive(true)
                .build();

        pastProposal = Proposal.builder()
                .id(4L)
                .title("Past Proposal")
                .description("Description")
                .startDate(now.minusHours(2))
                .endDate(now.minusHours(1))
                .isActive(true)
                .build();

        testVote = Vote.builder()
                .id(1L)
                .userId(1L)
                .vote(VoteType.POSITIVE)
                .proposal(activeProposal)
                .build();

        testVoteDTO = VoteDTO.builder()
                .userId(1L)
                .proposalId(1L)
                .vote(VoteType.POSITIVE)
                .build();
    }

    @Test
    @DisplayName("Should return votes by proposal")
    void shouldReturnVotesByProposal() {
        // Given
        List<Vote> mockVotes = Arrays.asList(testVote);
        when(voteRepository.findByProposalId(1L)).thenReturn(mockVotes);

        // When
        List<Vote> result = voteService.getVotesByProposal(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testVote, result.get(0));
        verify(voteRepository).findByProposalId(1L);
    }

    @Test
    @DisplayName("Should return votes by user")
    void shouldReturnVotesByUser() {
        // Given
        List<Vote> mockVotes = Arrays.asList(testVote);
        when(voteRepository.findByUserId(1L)).thenReturn(mockVotes);

        // When
        List<Vote> result = voteService.getVotesByUser(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testVote, result.get(0));
        verify(voteRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("Should return true when user has voted")
    void shouldReturnTrueWhenUserHasVoted() {
        // Given
        when(voteRepository.existsByUserIdAndProposalId(1L, 1L)).thenReturn(true);

        // When
        boolean result = voteService.hasUserVoted(1L, 1L);

        // Then
        assertTrue(result);
        verify(voteRepository).existsByUserIdAndProposalId(1L, 1L);
    }

    @Test
    @DisplayName("Should return false when user has not voted")
    void shouldReturnFalseWhenUserHasNotVoted() {
        // Given
        when(voteRepository.existsByUserIdAndProposalId(1L, 1L)).thenReturn(false);

        // When
        boolean result = voteService.hasUserVoted(1L, 1L);

        // Then
        assertFalse(result);
        verify(voteRepository).existsByUserIdAndProposalId(1L, 1L);
    }

    @Test
    @DisplayName("Should cast vote successfully")
    void shouldCastVoteSuccessfully() {
        // Given
        when(proposalRepository.findById(1L)).thenReturn(Optional.of(activeProposal));
        when(voteRepository.existsByUserIdAndProposalId(1L, 1L)).thenReturn(false);
        when(voteRepository.save(any(Vote.class))).thenReturn(testVote);

        // When
        Vote result = voteService.castVote(testVoteDTO);

        // Then
        assertNotNull(result);
        assertEquals(testVote, result);
        verify(proposalRepository).findById(1L);
        verify(voteRepository).existsByUserIdAndProposalId(1L, 1L);
        verify(voteRepository).save(any(Vote.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when proposal not found for vote")
    void shouldThrowResourceNotFoundExceptionWhenProposalNotFoundForVote() {
        // Given
        when(proposalRepository.findById(999L)).thenReturn(Optional.empty());
        VoteDTO voteDTO = VoteDTO.builder()
                .userId(1L)
                .proposalId(999L)
                .vote(VoteType.POSITIVE)
                .build();

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> voteService.castVote(voteDTO)
        );

        assertEquals("Propuesta no encontrada con ID: 999", exception.getMessage());
        verify(proposalRepository).findById(999L);
    }

    @Test
    @DisplayName("Should throw BusinessException when proposal has not started")
    void shouldThrowBusinessExceptionWhenProposalHasNotStarted() {
        // Given
        when(proposalRepository.findById(3L)).thenReturn(Optional.of(futureProposal));
        VoteDTO voteDTO = VoteDTO.builder()
                .userId(1L)
                .proposalId(3L)
                .vote(VoteType.POSITIVE)
                .build();

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> voteService.castVote(voteDTO)
        );

        assertEquals("La propuesta aún no ha comenzado", exception.getMessage());
        verify(proposalRepository).findById(3L);
    }

    @Test
    @DisplayName("Should throw BusinessException when proposal has ended")
    void shouldThrowBusinessExceptionWhenProposalHasEnded() {
        // Given
        when(proposalRepository.findById(4L)).thenReturn(Optional.of(pastProposal));
        VoteDTO voteDTO = VoteDTO.builder()
                .userId(1L)
                .proposalId(4L)
                .vote(VoteType.POSITIVE)
                .build();

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> voteService.castVote(voteDTO)
        );

        assertEquals("La propuesta ya ha finalizado", exception.getMessage());
        verify(proposalRepository).findById(4L);
    }

    @Test
    @DisplayName("Should throw BusinessException when user already voted")
    void shouldThrowBusinessExceptionWhenUserAlreadyVoted() {
        // Given
        when(proposalRepository.findById(1L)).thenReturn(Optional.of(activeProposal));
        when(voteRepository.existsByUserIdAndProposalId(1L, 1L)).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> voteService.castVote(testVoteDTO)
        );

        assertEquals("El usuario ya ha emitido un voto en esta propuesta", exception.getMessage());
        verify(proposalRepository).findById(1L);
        verify(voteRepository).existsByUserIdAndProposalId(1L, 1L);
        verify(voteRepository, never()).save(any(Vote.class));
    }

    @Test
    @DisplayName("Should cast vote with header successfully")
    void shouldCastVoteWithHeaderSuccessfully() {
        // Given
        when(proposalRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(activeProposal));
        when(voteRepository.existsByUserIdAndProposalId(1L, 1L)).thenReturn(false);
        when(voteRepository.save(any(Vote.class))).thenReturn(testVote);

        // When
        Vote result = voteService.castVoteWithHeader(1L, VoteType.POSITIVE, 1L);

        // Then
        assertNotNull(result);
        assertEquals(testVote, result);
        verify(proposalRepository).findByIdAndIsActiveTrue(1L);
        verify(voteRepository).existsByUserIdAndProposalId(1L, 1L);
        verify(voteRepository).save(any(Vote.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when proposal not found or inactive for vote with header")
    void shouldThrowResourceNotFoundExceptionWhenProposalNotFoundOrInactiveForVoteWithHeader() {
        // Given
        when(proposalRepository.findByIdAndIsActiveTrue(2L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> voteService.castVoteWithHeader(2L, VoteType.POSITIVE, 1L)
        );

        assertEquals("Propuesta no encontrada o inactiva con ID: 2", exception.getMessage());
        verify(proposalRepository).findByIdAndIsActiveTrue(2L);
    }

    @Test
    @DisplayName("Should count votes by proposal")
    void shouldCountVotesByProposal() {
        // Given
        when(voteRepository.countByProposalId(1L)).thenReturn(5);

        // When
        Integer result = voteService.countVotesByProposal(1L);

        // Then
        assertEquals(5, result);
        verify(voteRepository).countByProposalId(1L);
    }

    @Test
    @DisplayName("Should return vote counts by type")
    void shouldReturnVoteCountsByType() {
        // Given
        when(voteRepository.countByProposalIdAndVoteType(1L, VoteType.POSITIVE)).thenReturn(3);
        when(voteRepository.countByProposalIdAndVoteType(1L, VoteType.NEGATIVE)).thenReturn(2);
        when(voteRepository.countByProposalIdAndVoteType(1L, VoteType.ABSTENCY)).thenReturn(1);

        // When
        Map<VoteType, Integer> result = voteService.getVoteCountsByType(1L);

        // Then
        assertNotNull(result);
        assertEquals(3, result.get(VoteType.POSITIVE));
        assertEquals(2, result.get(VoteType.NEGATIVE));
        assertEquals(1, result.get(VoteType.ABSTENCY));
        verify(voteRepository).countByProposalIdAndVoteType(1L, VoteType.POSITIVE);
        verify(voteRepository).countByProposalIdAndVoteType(1L, VoteType.NEGATIVE);
        verify(voteRepository).countByProposalIdAndVoteType(1L, VoteType.ABSTENCY);
    }

    @Test
    @DisplayName("Should delete votes by proposal")
    void shouldDeleteVotesByProposal() {
        // Given
        doNothing().when(voteRepository).deleteByProposalId(1L);

        // When
        voteService.deleteVotesByProposal(1L);

        // Then
        verify(voteRepository).deleteByProposalId(1L);
    }

    @Test
    @DisplayName("Should handle empty vote lists")
    void shouldHandleEmptyVoteLists() {
        // Given
        when(voteRepository.findByProposalId(1L)).thenReturn(Arrays.asList());

        // When
        List<Vote> result = voteService.getVotesByProposal(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(voteRepository).findByProposalId(1L);
    }

    @Test
    @DisplayName("Should return zero count when no votes exist")
    void shouldReturnZeroCountWhenNoVotesExist() {
        // Given
        when(voteRepository.countByProposalId(1L)).thenReturn(0);

        // When
        Integer result = voteService.countVotesByProposal(1L);

        // Then
        assertEquals(0, result);
        verify(voteRepository).countByProposalId(1L);
    }
}