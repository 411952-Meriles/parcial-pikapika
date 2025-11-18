package utn.frc.piv.parcial.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.frc.piv.parcial.dtos.VoteDTO;
import utn.frc.piv.parcial.entities.Proposal;
import utn.frc.piv.parcial.entities.Vote;
import utn.frc.piv.parcial.entities.VoteType;
import utn.frc.piv.parcial.exceptions.BusinessException;
import utn.frc.piv.parcial.exceptions.ResourceNotFoundException;
import utn.frc.piv.parcial.repositories.ProposalRepository;
import utn.frc.piv.parcial.repositories.VoteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VoteService {

    private final VoteRepository voteRepository;
    private final ProposalRepository proposalRepository;

    @Transactional(readOnly = true)
    public List<Vote> getVotesByProposal(Long proposalId) {
        log.debug("Obteniendo votos para propuesta ID: {}", proposalId);
        return voteRepository.findByProposalId(proposalId);
    }

    @Transactional(readOnly = true)
    public List<Vote> getVotesByUser(Long userId) {
        log.debug("Obteniendo votos para usuario ID: {}", userId);
        return voteRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean hasUserVoted(Long userId, Long proposalId) {
        log.debug("Verificando si usuario {} ya votó en propuesta {}", userId, proposalId);
        return voteRepository.existsByUserIdAndProposalId(userId, proposalId);
    }

    public Vote castVote(VoteDTO voteDTO) {
        log.debug("Emitiendo voto para usuario: {} en propuesta: {}", voteDTO.getUserId(), voteDTO.getProposalId());

        // Verificar que la propuesta existe
        Proposal proposal = proposalRepository.findById(voteDTO.getProposalId())
                .orElseThrow(() -> new ResourceNotFoundException("Propuesta no encontrada con ID: " + voteDTO.getProposalId()));

        // Verificar que la propuesta está en curso
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(proposal.getStartDate())) {
            throw new BusinessException("La propuesta aún no ha comenzado");
        }
        if (now.isAfter(proposal.getEndDate())) {
            throw new BusinessException("La propuesta ya ha finalizado");
        }

        // Verificar que el usuario no haya votado antes
        if (hasUserVoted(voteDTO.getUserId(), voteDTO.getProposalId())) {
            throw new BusinessException("El usuario ya ha emitido un voto en esta propuesta");
        }

        // Crear y guardar el voto
        Vote vote = Vote.builder()
                .userId(voteDTO.getUserId())
                .vote(voteDTO.getVote())
                .proposal(proposal)
                .build();

        Vote savedVote = voteRepository.save(vote);

        log.info("Voto emitido exitosamente. Usuario: {}, Propuesta: {}, Voto: {}", 
                voteDTO.getUserId(), voteDTO.getProposalId(), voteDTO.getVote());

        return savedVote;
    }

    public Vote castVoteWithHeader(Long proposalId, VoteType voteType, Long userId) {
        log.debug("Emitiendo voto para usuario: {} en propuesta: {}", userId, proposalId);

        // Verificar que la propuesta existe y está activa
        Proposal proposal = proposalRepository.findByIdAndIsActiveTrue(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("Propuesta no encontrada o inactiva con ID: " + proposalId));

        // Verificar que la propuesta está en curso
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(proposal.getStartDate())) {
            throw new BusinessException("La propuesta aún no ha comenzado");
        }
        if (now.isAfter(proposal.getEndDate())) {
            throw new BusinessException("La propuesta ya ha finalizado");
        }

        // Verificar que el usuario no haya votado antes
        if (hasUserVoted(userId, proposalId)) {
            throw new BusinessException("El usuario ya ha emitido un voto en esta propuesta");
        }

        // Crear y guardar el voto
        Vote vote = Vote.builder()
                .userId(userId)
                .vote(voteType)
                .proposal(proposal)
                .build();

        Vote savedVote = voteRepository.save(vote);

        log.info("Voto emitido exitosamente. Usuario: {}, Propuesta: {}, Voto: {}", 
                userId, proposalId, voteType);

        return savedVote;
    }

    @Transactional(readOnly = true)
    public Integer countVotesByProposal(Long proposalId) {
        log.debug("Contando votos para propuesta ID: {}", proposalId);
        return voteRepository.countByProposalId(proposalId);
    }

    @Transactional(readOnly = true)
    public Map<VoteType, Integer> getVoteCountsByType(Long proposalId) {
        log.debug("Obteniendo conteo de votos por tipo para propuesta ID: {}", proposalId);
        
        Map<VoteType, Integer> voteCount = new HashMap<>();
        voteCount.put(VoteType.POSITIVE, voteRepository.countByProposalIdAndVoteType(proposalId, VoteType.POSITIVE));
        voteCount.put(VoteType.NEGATIVE, voteRepository.countByProposalIdAndVoteType(proposalId, VoteType.NEGATIVE));
        voteCount.put(VoteType.ABSTENCY, voteRepository.countByProposalIdAndVoteType(proposalId, VoteType.ABSTENCY));
        
        return voteCount;
    }

    public void deleteVotesByProposal(Long proposalId) {
        log.debug("Eliminando todos los votos de la propuesta ID: {}", proposalId);
        voteRepository.deleteByProposalId(proposalId);
        log.info("Votos eliminados exitosamente para propuesta ID: {}", proposalId);
    }
}