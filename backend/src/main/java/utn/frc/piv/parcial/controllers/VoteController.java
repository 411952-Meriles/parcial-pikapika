package utn.frc.piv.parcial.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utn.frc.piv.parcial.dtos.VoteDTO;
import utn.frc.piv.parcial.entities.Vote;
import utn.frc.piv.parcial.entities.VoteType;
import utn.frc.piv.parcial.services.VoteService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*")
public class VoteController {

    private final VoteService voteService;

    @GetMapping("/proposal/{proposalId}")
    public ResponseEntity<List<Vote>> getVotesByProposal(@PathVariable Long proposalId) {
        List<Vote> votes = voteService.getVotesByProposal(proposalId);
        return ResponseEntity.ok(votes);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Vote>> getVotesByUser(@PathVariable Long userId) {
        List<Vote> votes = voteService.getVotesByUser(userId);
        return ResponseEntity.ok(votes);
    }

    @GetMapping("/check/{userId}/{proposalId}")
    public ResponseEntity<Boolean> hasUserVoted(@PathVariable Long userId, 
                                               @PathVariable Long proposalId) {
        boolean hasVoted = voteService.hasUserVoted(userId, proposalId);
        return ResponseEntity.ok(hasVoted);
    }

    @PostMapping
    public ResponseEntity<Vote> castVote(@Valid @RequestBody VoteDTO voteDTO, 
                                        HttpServletRequest request) {
        Vote vote = voteService.castVote(voteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(vote);
    }

    @GetMapping("/proposal/{proposalId}/count")
    public ResponseEntity<Integer> countVotesByProposal(@PathVariable Long proposalId) {
        Integer count = voteService.countVotesByProposal(proposalId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/proposal/{proposalId}/count-by-type")
    public ResponseEntity<Map<VoteType, Integer>> getVoteCountsByType(@PathVariable Long proposalId) {
        Map<VoteType, Integer> counts = voteService.getVoteCountsByType(proposalId);
        return ResponseEntity.ok(counts);
    }

    @DeleteMapping("/proposal/{proposalId}")
    public ResponseEntity<Void> deleteVotesByProposal(@PathVariable Long proposalId) {
        voteService.deleteVotesByProposal(proposalId);
        return ResponseEntity.noContent().build();
    }
}