package utn.frc.piv.parcial.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import utn.frc.piv.parcial.dtos.ProposalDTO;
import utn.frc.piv.parcial.dtos.ProposalResponseDTO;
import utn.frc.piv.parcial.dtos.VoteRequestDTO;
import utn.frc.piv.parcial.dtos.VoteResponseDTO;
import utn.frc.piv.parcial.entities.Proposal;
import utn.frc.piv.parcial.entities.Vote;
import utn.frc.piv.parcial.entities.VoteType;
import utn.frc.piv.parcial.exceptions.BusinessException;
import utn.frc.piv.parcial.exceptions.ResourceNotFoundException;
import utn.frc.piv.parcial.services.ProposalService;
import utn.frc.piv.parcial.services.VoteService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = ProposalController.class, 
    excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@DisplayName("Proposal Controller Tests")
class ProposalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProposalService proposalService;

    @MockBean
    private VoteService voteService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProposalResponseDTO testProposalResponse;
    private ProposalDTO testProposalDTO;
    private Proposal testProposal;
    private Vote testVote;

    @BeforeEach
    void setUp() {
        LocalDateTime futureStart = LocalDateTime.now().plusDays(1);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(7);

        testProposalResponse = ProposalResponseDTO.builder()
                .id(1L)
                .title("Test Proposal")
                .description("Test Description")
                .startDate(futureStart)
                .endDate(futureEnd)
                .positiveVotes(null)
                .negativeVotes(null)
                .build();

        testProposalDTO = ProposalDTO.builder()
                .title("Test Proposal")
                .description("Test Description")
                .startDate(futureStart)
                .endDate(futureEnd)
                .build();

        testProposal = Proposal.builder()
                .id(1L)
                .title("Test Proposal")
                .description("Test Description")
                .startDate(futureStart)
                .endDate(futureEnd)
                .isActive(true)
                .build();

        testVote = Vote.builder()
                .id(1L)
                .userId(1L)
                .vote(VoteType.POSITIVE)
                .proposal(testProposal)
                .build();
    }

    @Test
    @DisplayName("Should return all active proposals")
    void shouldReturnAllActiveProposals() throws Exception {
        // Given
        List<ProposalResponseDTO> proposals = Arrays.asList(testProposalResponse);
        when(proposalService.getAllActiveProposals()).thenReturn(proposals);

        // When & Then
        mockMvc.perform(get("/api/proposals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Proposal")))
                .andExpect(jsonPath("$[0].description", is("Test Description")));

        verify(proposalService).getAllActiveProposals();
    }

    @Test
    @DisplayName("Should return empty list when no active proposals")
    void shouldReturnEmptyListWhenNoActiveProposals() throws Exception {
        // Given
        when(proposalService.getAllActiveProposals()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/proposals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(proposalService).getAllActiveProposals();
    }

    @Test
    @DisplayName("Should return proposal by ID when exists")
    void shouldReturnProposalByIdWhenExists() throws Exception {
        // Given
        when(proposalService.getProposalById(1L)).thenReturn(testProposalResponse);

        // When & Then
        mockMvc.perform(get("/api/proposals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Proposal")))
                .andExpect(jsonPath("$.description", is("Test Description")));

        verify(proposalService).getProposalById(1L);
    }

    @Test
    @DisplayName("Should return 404 when proposal not found by ID")
    void shouldReturn404WhenProposalNotFoundById() throws Exception {
        // Given
        when(proposalService.getProposalById(999L))
                .thenThrow(new ResourceNotFoundException("Propuesta no encontrada con ID: 999"));

        // When & Then
        mockMvc.perform(get("/api/proposals/999"))
                .andExpect(status().isNotFound());

        verify(proposalService).getProposalById(999L);
    }

    @Test
    @DisplayName("Should create proposal successfully")
    void shouldCreateProposalSuccessfully() throws Exception {
        // Given
        when(proposalService.createProposal(any(ProposalDTO.class))).thenReturn(testProposalResponse);

        // When & Then
        mockMvc.perform(post("/api/proposals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProposalDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Proposal")))
                .andExpect(jsonPath("$.description", is("Test Description")));

        verify(proposalService).createProposal(any(ProposalDTO.class));
    }

    @Test
    @DisplayName("Should return 400 when creating proposal with invalid data")
    void shouldReturn400WhenCreatingProposalWithInvalidData() throws Exception {
        // Given
        ProposalDTO invalidDTO = ProposalDTO.builder()
                .title("")  // Invalid empty title
                .description("Test Description")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(7))
                .build();

        // When & Then
        mockMvc.perform(post("/api/proposals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(proposalService, never()).createProposal(any(ProposalDTO.class));
    }

    @Test
    @DisplayName("Should return 400 when creating proposal with business rule violation")
    void shouldReturn400WhenCreatingProposalWithBusinessRuleViolation() throws Exception {
        // Given
        when(proposalService.createProposal(any(ProposalDTO.class)))
                .thenThrow(new BusinessException("La fecha de inicio no puede ser anterior a la fecha actual"));

        // When & Then
        mockMvc.perform(post("/api/proposals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProposalDTO)))
                .andExpect(status().isBadRequest());

        verify(proposalService).createProposal(any(ProposalDTO.class));
    }

    @Test
    @DisplayName("Should update proposal successfully")
    void shouldUpdateProposalSuccessfully() throws Exception {
        // Given
        when(proposalService.updateProposal(eq(1L), any(ProposalDTO.class))).thenReturn(testProposal);

        // When & Then
        mockMvc.perform(put("/api/proposals/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProposalDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Proposal")));

        verify(proposalService).updateProposal(eq(1L), any(ProposalDTO.class));
    }

    @Test
    @DisplayName("Should delete proposal successfully")
    void shouldDeleteProposalSuccessfully() throws Exception {
        // Given
        doNothing().when(proposalService).deleteProposal(1L);

        // When & Then
        mockMvc.perform(delete("/api/proposals/1"))
                .andExpect(status().isNoContent());

        verify(proposalService).deleteProposal(1L);
    }

    @Test
    @DisplayName("Should return total votes count")
    void shouldReturnTotalVotesCount() throws Exception {
        // Given
        when(proposalService.getTotalVotes(1L)).thenReturn(10);

        // When & Then
        mockMvc.perform(get("/api/proposals/1/votes/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(10)));

        verify(proposalService).getTotalVotes(1L);
    }

    @Test
    @DisplayName("Should cast vote successfully")
    void shouldCastVoteSuccessfully() throws Exception {
        // Given
        VoteRequestDTO voteRequest = VoteRequestDTO.builder()
                .vote(VoteType.POSITIVE)
                .build();

        when(voteService.castVoteWithHeader(eq(1L), eq(VoteType.POSITIVE), eq(123L)))
                .thenReturn(testVote);

        // When & Then
        mockMvc.perform(post("/api/proposals/1/votes")
                .header("x-user-id", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.vote", is("POSITIVE")))
                .andExpect(jsonPath("$.proposalId", is(1)));

        verify(voteService).castVoteWithHeader(eq(1L), eq(VoteType.POSITIVE), eq(123L));
    }

    @Test
    @DisplayName("Should return 400 when casting vote without user header")
    void shouldReturn400WhenCastingVoteWithoutUserHeader() throws Exception {
        // Given
        VoteRequestDTO voteRequest = VoteRequestDTO.builder()
                .vote(VoteType.POSITIVE)
                .build();

        // When & Then
        mockMvc.perform(post("/api/proposals/1/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).castVoteWithHeader(anyLong(), any(VoteType.class), anyLong());
    }

    @Test
    @DisplayName("Should return 400 when casting vote with business rule violation")
    void shouldReturn400WhenCastingVoteWithBusinessRuleViolation() throws Exception {
        // Given
        VoteRequestDTO voteRequest = VoteRequestDTO.builder()
                .vote(VoteType.POSITIVE)
                .build();

        when(voteService.castVoteWithHeader(eq(1L), eq(VoteType.POSITIVE), eq(123L)))
                .thenThrow(new BusinessException("El usuario ya ha emitido un voto en esta propuesta"));

        // When & Then
        mockMvc.perform(post("/api/proposals/1/votes")
                .header("x-user-id", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest());

        verify(voteService).castVoteWithHeader(eq(1L), eq(VoteType.POSITIVE), eq(123L));
    }

    @Test
    @DisplayName("Should return 404 when casting vote on non-existent proposal")
    void shouldReturn404WhenCastingVoteOnNonExistentProposal() throws Exception {
        // Given
        VoteRequestDTO voteRequest = VoteRequestDTO.builder()
                .vote(VoteType.POSITIVE)
                .build();

        when(voteService.castVoteWithHeader(eq(999L), eq(VoteType.POSITIVE), eq(123L)))
                .thenThrow(new ResourceNotFoundException("Propuesta no encontrada o inactiva con ID: 999"));

        // When & Then
        mockMvc.perform(post("/api/proposals/999/votes")
                .header("x-user-id", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isNotFound());

        verify(voteService).castVoteWithHeader(eq(999L), eq(VoteType.POSITIVE), eq(123L));
    }

    @Test
    @DisplayName("Should return 400 when casting vote with invalid vote type")
    void shouldReturn400WhenCastingVoteWithInvalidVoteType() throws Exception {
        // Given
        String invalidVoteRequest = """
                {
                    "vote": "INVALID_VOTE_TYPE"
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/proposals/1/votes")
                .header("x-user-id", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidVoteRequest))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).castVoteWithHeader(anyLong(), any(VoteType.class), anyLong());
    }

    @Test
    @DisplayName("Should handle invalid user ID in header")
    void shouldHandleInvalidUserIdInHeader() throws Exception {
        // Given
        VoteRequestDTO voteRequest = VoteRequestDTO.builder()
                .vote(VoteType.POSITIVE)
                .build();

        // When & Then
        mockMvc.perform(post("/api/proposals/1/votes")
                .header("x-user-id", "invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).castVoteWithHeader(anyLong(), any(VoteType.class), anyLong());
    }
}