package utn.frc.piv.parcial.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utn.frc.piv.parcial.dtos.ProposalDTO;
import utn.frc.piv.parcial.dtos.ProposalResponseDTO;
import utn.frc.piv.parcial.entities.Proposal;
import utn.frc.piv.parcial.exceptions.BusinessException;
import utn.frc.piv.parcial.exceptions.ResourceNotFoundException;
import utn.frc.piv.parcial.repositories.ProposalRepository;
import utn.frc.piv.parcial.repositories.VoteRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Proposal Service Tests")
class ProposalServiceTest {

    @Mock
    private ProposalRepository proposalRepository;

    @Mock
    private VoteRepository voteRepository;

    @InjectMocks
    private ProposalService proposalService;

    private Proposal testProposal;
    private ProposalDTO testProposalDTO;
    private LocalDateTime futureStartDate;
    private LocalDateTime futureEndDate;

    @BeforeEach
    void setUp() {
        futureStartDate = LocalDateTime.now().plusDays(1);
        futureEndDate = LocalDateTime.now().plusDays(7);

        testProposal = Proposal.builder()
                .id(1L)
                .title("Test Proposal")
                .description("Test Description")
                .startDate(futureStartDate)
                .endDate(futureEndDate)
                .isActive(true)
                .build();

        testProposalDTO = ProposalDTO.builder()
                .title("Test Proposal")
                .description("Test Description")
                .startDate(futureStartDate)
                .endDate(futureEndDate)
                .build();
    }

    @Test
    @DisplayName("Should return all active proposals")
    void shouldReturnAllActiveProposals() {
        // Given
        List<Proposal> mockProposals = Arrays.asList(testProposal);
        when(proposalRepository.findByIsActiveTrueOrderByStartDateAsc()).thenReturn(mockProposals);

        // When
        List<ProposalResponseDTO> result = proposalService.getAllActiveProposals();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProposal.getTitle(), result.get(0).getTitle());
        verify(proposalRepository).findByIsActiveTrueOrderByStartDateAsc();
    }

    @Test
    @DisplayName("Should return proposal by ID when exists")
    void shouldReturnProposalByIdWhenExists() {
        // Given
        when(proposalRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(testProposal));

        // When
        ProposalResponseDTO result = proposalService.getProposalById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testProposal.getTitle(), result.getTitle());
        assertEquals(testProposal.getDescription(), result.getDescription());
        verify(proposalRepository).findByIdAndIsActiveTrue(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when proposal not found by ID")
    void shouldThrowResourceNotFoundExceptionWhenProposalNotFoundById() {
        // Given
        when(proposalRepository.findByIdAndIsActiveTrue(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> proposalService.getProposalById(999L)
        );

        assertEquals("Propuesta no encontrada con ID: 999", exception.getMessage());
        verify(proposalRepository).findByIdAndIsActiveTrue(999L);
    }

    @Test
    @DisplayName("Should return active proposals")
    void shouldReturnActiveProposals() {
        // Given
        List<Proposal> mockProposals = Arrays.asList(testProposal);
        when(proposalRepository.findActiveProposals(any(LocalDateTime.class))).thenReturn(mockProposals);

        // When
        List<Proposal> result = proposalService.getActiveProposals();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(proposalRepository).findActiveProposals(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should return finished proposals")
    void shouldReturnFinishedProposals() {
        // Given
        List<Proposal> mockProposals = Arrays.asList(testProposal);
        when(proposalRepository.findFinishedProposals(any(LocalDateTime.class))).thenReturn(mockProposals);

        // When
        List<Proposal> result = proposalService.getFinishedProposals();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(proposalRepository).findFinishedProposals(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should return future proposals")
    void shouldReturnFutureProposals() {
        // Given
        List<Proposal> mockProposals = Arrays.asList(testProposal);
        when(proposalRepository.findFutureProposals(any(LocalDateTime.class))).thenReturn(mockProposals);

        // When
        List<Proposal> result = proposalService.getFutureProposals();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(proposalRepository).findFutureProposals(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should create proposal successfully")
    void shouldCreateProposalSuccessfully() {
        // Given
        when(proposalRepository.save(any(Proposal.class))).thenReturn(testProposal);

        // When
        ProposalResponseDTO result = proposalService.createProposal(testProposalDTO);

        // Then
        assertNotNull(result);
        assertEquals(testProposalDTO.getTitle(), result.getTitle());
        assertEquals(testProposalDTO.getDescription(), result.getDescription());
        verify(proposalRepository).save(any(Proposal.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when start date is in the past")
    void shouldThrowBusinessExceptionWhenStartDateIsInThePast() {
        // Given
        ProposalDTO invalidDTO = ProposalDTO.builder()
                .title("Test")
                .description("Test")
                .startDate(LocalDateTime.now().minusDays(1))
                .endDate(futureEndDate)
                .build();

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> proposalService.createProposal(invalidDTO)
        );

        assertEquals("La fecha de inicio no puede ser anterior a la fecha actual", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw BusinessException when end date is before start date")
    void shouldThrowBusinessExceptionWhenEndDateIsBeforeStartDate() {
        // Given
        ProposalDTO invalidDTO = ProposalDTO.builder()
                .title("Test")
                .description("Test")
                .startDate(futureEndDate)
                .endDate(futureStartDate)
                .build();

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> proposalService.createProposal(invalidDTO)
        );

        assertEquals("La fecha de fin no puede ser anterior a la fecha de inicio", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw BusinessException when start and end dates are equal")
    void shouldThrowBusinessExceptionWhenStartAndEndDatesAreEqual() {
        // Given
        ProposalDTO invalidDTO = ProposalDTO.builder()
                .title("Test")
                .description("Test")
                .startDate(futureStartDate)
                .endDate(futureStartDate)
                .build();

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> proposalService.createProposal(invalidDTO)
        );

        assertEquals("La fecha de inicio y fin no pueden ser iguales", exception.getMessage());
    }

    @Test
    @DisplayName("Should update proposal successfully when no votes exist")
    void shouldUpdateProposalSuccessfullyWhenNoVotesExist() {
        // Given
        when(proposalRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(testProposal));
        when(voteRepository.countByProposalId(1L)).thenReturn(0);
        when(proposalRepository.save(any(Proposal.class))).thenReturn(testProposal);

        // When
        Proposal result = proposalService.updateProposal(1L, testProposalDTO);

        // Then
        assertNotNull(result);
        verify(proposalRepository).findByIdAndIsActiveTrue(1L);
        verify(voteRepository).countByProposalId(1L);
        verify(proposalRepository).save(any(Proposal.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when updating proposal with votes")
    void shouldThrowBusinessExceptionWhenUpdatingProposalWithVotes() {
        // Given
        when(proposalRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(testProposal));
        when(voteRepository.countByProposalId(1L)).thenReturn(5);

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> proposalService.updateProposal(1L, testProposalDTO)
        );

        assertEquals("No se puede modificar una propuesta que ya tiene votos", exception.getMessage());
        verify(proposalRepository).findByIdAndIsActiveTrue(1L);
        verify(voteRepository).countByProposalId(1L);
        verify(proposalRepository, never()).save(any(Proposal.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent proposal")
    void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistentProposal() {
        // Given
        when(proposalRepository.findByIdAndIsActiveTrue(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> proposalService.updateProposal(999L, testProposalDTO)
        );

        assertEquals("Propuesta no encontrada con ID: 999", exception.getMessage());
        verify(proposalRepository).findByIdAndIsActiveTrue(999L);
    }

    @Test
    @DisplayName("Should delete proposal successfully (logical delete)")
    void shouldDeleteProposalSuccessfully() {
        // Given
        when(proposalRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(testProposal));
        when(proposalRepository.save(any(Proposal.class))).thenReturn(testProposal);

        // When
        proposalService.deleteProposal(1L);

        // Then
        verify(proposalRepository).findByIdAndIsActiveTrue(1L);
        verify(proposalRepository).save(argThat(proposal -> !proposal.getIsActive()));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent proposal")
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistentProposal() {
        // Given
        when(proposalRepository.findByIdAndIsActiveTrue(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> proposalService.deleteProposal(999L)
        );

        assertEquals("Propuesta no encontrada con ID: 999", exception.getMessage());
        verify(proposalRepository).findByIdAndIsActiveTrue(999L);
        verify(proposalRepository, never()).save(any(Proposal.class));
    }

    @Test
    @DisplayName("Should return total votes count")
    void shouldReturnTotalVotesCount() {
        // Given
        when(proposalRepository.countVotesByProposalId(1L)).thenReturn(10);

        // When
        Integer result = proposalService.getTotalVotes(1L);

        // Then
        assertEquals(10, result);
        verify(proposalRepository).countVotesByProposalId(1L);
    }

    @Test
    @DisplayName("Should convert to response DTO with votes when proposal is finished")
    void shouldConvertToResponseDTOWithVotesWhenProposalIsFinished() {
        // Given
        Proposal finishedProposal = Proposal.builder()
                .id(1L)
                .title("Finished Proposal")
                .description("Finished Description")
                .startDate(LocalDateTime.now().minusDays(7))
                .endDate(LocalDateTime.now().minusDays(1))
                .isActive(true)
                .build();

        when(proposalRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(finishedProposal));
        when(voteRepository.countPositiveVotesByProposalId(1L)).thenReturn(5);
        when(voteRepository.countNegativeVotesByProposalId(1L)).thenReturn(3);

        // When
        ProposalResponseDTO result = proposalService.getProposalById(1L);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getPositiveVotes());
        assertEquals(3, result.getNegativeVotes());
        verify(voteRepository).countPositiveVotesByProposalId(1L);
        verify(voteRepository).countNegativeVotesByProposalId(1L);
    }

    @Test
    @DisplayName("Should convert to response DTO without votes when proposal is not finished")
    void shouldConvertToResponseDTOWithoutVotesWhenProposalIsNotFinished() {
        // Given
        when(proposalRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(testProposal));

        // When
        ProposalResponseDTO result = proposalService.getProposalById(1L);

        // Then
        assertNotNull(result);
        assertNull(result.getPositiveVotes());
        assertNull(result.getNegativeVotes());
        verify(voteRepository, never()).countPositiveVotesByProposalId(any());
        verify(voteRepository, never()).countNegativeVotesByProposalId(any());
    }
}