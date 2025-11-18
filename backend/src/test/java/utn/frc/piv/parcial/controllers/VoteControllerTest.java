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
import utn.frc.piv.parcial.dtos.VoteDTO;
import utn.frc.piv.parcial.entities.Proposal;
import utn.frc.piv.parcial.entities.Vote;
import utn.frc.piv.parcial.entities.VoteType;
import utn.frc.piv.parcial.exceptions.BusinessException;
import utn.frc.piv.parcial.exceptions.ResourceNotFoundException;
import utn.frc.piv.parcial.services.VoteService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = VoteController.class,
    excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@DisplayName("Vote Controller Tests")
class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoteService voteService;

    @Autowired
    private ObjectMapper objectMapper;

    private Proposal testProposal;
    private Vote testVote;
    private VoteDTO testVoteDTO;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        testProposal = Proposal.builder()
                .id(1L)
                .title("Test Proposal")
                .description("Test Description")
                .startDate(now.minusHours(1))
                .endDate(now.plusHours(1))
                .isActive(true)
                .build();

        testVote = Vote.builder()
                .id(1L)
                .userId(1L)
                .vote(VoteType.POSITIVE)
                .proposal(testProposal)
                .build();

        testVoteDTO = VoteDTO.builder()
                .userId(1L)
                .proposalId(1L)
                .vote(VoteType.POSITIVE)
                .build();
    }

    @Test
    @DisplayName("Should return votes by proposal")
    void shouldReturnVotesByProposal() throws Exception {
        // Given
        List<Vote> votes = Arrays.asList(testVote);
        when(voteService.getVotesByProposal(1L)).thenReturn(votes);

        // When & Then
        mockMvc.perform(get("/api/votes/proposal/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[0].vote", is("POSITIVE")));

        verify(voteService).getVotesByProposal(1L);
    }

    @Test
    @DisplayName("Should return empty list when no votes by proposal")
    void shouldReturnEmptyListWhenNoVotesByProposal() throws Exception {
        // Given
        when(voteService.getVotesByProposal(1L)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/votes/proposal/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(voteService).getVotesByProposal(1L);
    }

    @Test
    @DisplayName("Should return votes by user")
    void shouldReturnVotesByUser() throws Exception {
        // Given
        List<Vote> votes = Arrays.asList(testVote);
        when(voteService.getVotesByUser(1L)).thenReturn(votes);

        // When & Then
        mockMvc.perform(get("/api/votes/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[0].vote", is("POSITIVE")));

        verify(voteService).getVotesByUser(1L);
    }

    @Test
    @DisplayName("Should return true when user has voted")
    void shouldReturnTrueWhenUserHasVoted() throws Exception {
        // Given
        when(voteService.hasUserVoted(1L, 1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/votes/check/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));

        verify(voteService).hasUserVoted(1L, 1L);
    }

    @Test
    @DisplayName("Should return false when user has not voted")
    void shouldReturnFalseWhenUserHasNotVoted() throws Exception {
        // Given
        when(voteService.hasUserVoted(1L, 1L)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/votes/check/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));

        verify(voteService).hasUserVoted(1L, 1L);
    }

    @Test
    @DisplayName("Should cast vote successfully")
    void shouldCastVoteSuccessfully() throws Exception {
        // Given
        when(voteService.castVote(any(VoteDTO.class))).thenReturn(testVote);

        // When & Then
        mockMvc.perform(post("/api/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testVoteDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.vote", is("POSITIVE")));

        verify(voteService).castVote(any(VoteDTO.class));
    }

    @Test
    @DisplayName("Should return 400 when casting vote with invalid data")
    void shouldReturn400WhenCastingVoteWithInvalidData() throws Exception {
        // Given
        VoteDTO invalidDTO = VoteDTO.builder()
                .userId(null)  // Invalid null userId
                .proposalId(1L)
                .vote(VoteType.POSITIVE)
                .build();

        // When & Then
        mockMvc.perform(post("/api/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).castVote(any(VoteDTO.class));
    }

    @Test
    @DisplayName("Should return 400 when casting vote with business rule violation")
    void shouldReturn400WhenCastingVoteWithBusinessRuleViolation() throws Exception {
        // Given
        when(voteService.castVote(any(VoteDTO.class)))
                .thenThrow(new BusinessException("El usuario ya ha emitido un voto en esta propuesta"));

        // When & Then
        mockMvc.perform(post("/api/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testVoteDTO)))
                .andExpect(status().isBadRequest());

        verify(voteService).castVote(any(VoteDTO.class));
    }

    @Test
    @DisplayName("Should return 404 when casting vote on non-existent proposal")
    void shouldReturn404WhenCastingVoteOnNonExistentProposal() throws Exception {
        // Given
        when(voteService.castVote(any(VoteDTO.class)))
                .thenThrow(new ResourceNotFoundException("Propuesta no encontrada con ID: 999"));

        // When & Then
        mockMvc.perform(post("/api/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testVoteDTO)))
                .andExpect(status().isNotFound());

        verify(voteService).castVote(any(VoteDTO.class));
    }

    @Test
    @DisplayName("Should return vote count by proposal")
    void shouldReturnVoteCountByProposal() throws Exception {
        // Given
        when(voteService.countVotesByProposal(1L)).thenReturn(5);

        // When & Then
        mockMvc.perform(get("/api/votes/proposal/1/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(5)));

        verify(voteService).countVotesByProposal(1L);
    }

    @Test
    @DisplayName("Should return zero when no votes for proposal")
    void shouldReturnZeroWhenNoVotesForProposal() throws Exception {
        // Given
        when(voteService.countVotesByProposal(1L)).thenReturn(0);

        // When & Then
        mockMvc.perform(get("/api/votes/proposal/1/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(0)));

        verify(voteService).countVotesByProposal(1L);
    }

    @Test
    @DisplayName("Should return vote counts by type")
    void shouldReturnVoteCountsByType() throws Exception {
        // Given
        Map<VoteType, Integer> voteCounts = new HashMap<>();
        voteCounts.put(VoteType.POSITIVE, 3);
        voteCounts.put(VoteType.NEGATIVE, 2);
        voteCounts.put(VoteType.ABSTENCY, 1);

        when(voteService.getVoteCountsByType(1L)).thenReturn(voteCounts);

        // When & Then
        mockMvc.perform(get("/api/votes/proposal/1/count-by-type"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.POSITIVE", is(3)))
                .andExpect(jsonPath("$.NEGATIVE", is(2)))
                .andExpect(jsonPath("$.ABSTENCY", is(1)));

        verify(voteService).getVoteCountsByType(1L);
    }

    @Test
    @DisplayName("Should return empty counts when no votes by type")
    void shouldReturnEmptyCountsWhenNoVotesByType() throws Exception {
        // Given
        Map<VoteType, Integer> voteCounts = new HashMap<>();
        voteCounts.put(VoteType.POSITIVE, 0);
        voteCounts.put(VoteType.NEGATIVE, 0);
        voteCounts.put(VoteType.ABSTENCY, 0);

        when(voteService.getVoteCountsByType(1L)).thenReturn(voteCounts);

        // When & Then
        mockMvc.perform(get("/api/votes/proposal/1/count-by-type"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.POSITIVE", is(0)))
                .andExpect(jsonPath("$.NEGATIVE", is(0)))
                .andExpect(jsonPath("$.ABSTENCY", is(0)));

        verify(voteService).getVoteCountsByType(1L);
    }

    @Test
    @DisplayName("Should delete votes by proposal successfully")
    void shouldDeleteVotesByProposalSuccessfully() throws Exception {
        // Given
        doNothing().when(voteService).deleteVotesByProposal(1L);

        // When & Then
        mockMvc.perform(delete("/api/votes/proposal/1"))
                .andExpect(status().isNoContent());

        verify(voteService).deleteVotesByProposal(1L);
    }

    @Test
    @DisplayName("Should handle invalid proposal ID in path")
    void shouldHandleInvalidProposalIdInPath() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/votes/proposal/invalid"))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).getVotesByProposal(anyLong());
    }

    @Test
    @DisplayName("Should handle invalid user ID in path")
    void shouldHandleInvalidUserIdInPath() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/votes/user/invalid"))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).getVotesByUser(anyLong());
    }

    @Test
    @DisplayName("Should handle missing request body for vote creation")
    void shouldHandleMissingRequestBodyForVoteCreation() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/votes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).castVote(any(VoteDTO.class));
    }

    @Test
    @DisplayName("Should handle malformed JSON in request body")
    void shouldHandleMalformedJsonInRequestBody() throws Exception {
        // Given
        String malformedJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(post("/api/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(voteService, never()).castVote(any(VoteDTO.class));
    }
}