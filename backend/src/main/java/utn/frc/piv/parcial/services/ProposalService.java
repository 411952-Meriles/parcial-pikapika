package utn.frc.piv.parcial.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utn.frc.piv.parcial.dtos.ProposalDTO;
import utn.frc.piv.parcial.dtos.ProposalResponseDTO;
import utn.frc.piv.parcial.entities.Proposal;
import utn.frc.piv.parcial.exceptions.ResourceNotFoundException;
import utn.frc.piv.parcial.exceptions.BusinessException;
import utn.frc.piv.parcial.repositories.ProposalRepository;
import utn.frc.piv.parcial.repositories.VoteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final VoteRepository voteRepository;

    @Transactional(readOnly = true)
    public List<ProposalResponseDTO> getAllActiveProposals() {
        log.debug("Obteniendo todas las propuestas activas");
        List<Proposal> proposals = proposalRepository.findByIsActiveTrueOrderByStartDateAsc();
        return proposals.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProposalResponseDTO getProposalById(Long id) {
        log.debug("Obteniendo propuesta por ID: {}", id);
        Proposal proposal = proposalRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propuesta no encontrada con ID: " + id));
        return convertToResponseDTO(proposal);
    }

    @Transactional(readOnly = true)
    public List<Proposal> getActiveProposals() {
        LocalDateTime now = LocalDateTime.now();
        log.debug("Obteniendo propuestas activas para fecha: {}", now);
        return proposalRepository.findActiveProposals(now);
    }

    @Transactional(readOnly = true)
    public List<Proposal> getFinishedProposals() {
        LocalDateTime now = LocalDateTime.now();
        log.debug("Obteniendo propuestas finalizadas para fecha: {}", now);
        return proposalRepository.findFinishedProposals(now);
    }

    @Transactional(readOnly = true)
    public List<Proposal> getFutureProposals() {
        LocalDateTime now = LocalDateTime.now();
        log.debug("Obteniendo propuestas futuras para fecha: {}", now);
        return proposalRepository.findFutureProposals(now);
    }

    public ProposalResponseDTO createProposal(ProposalDTO proposalDTO) {
        log.debug("Creando nueva propuesta: {}", proposalDTO.getTitle());
        
        validateProposalDates(proposalDTO.getStartDate(), proposalDTO.getEndDate());
        
        Proposal proposal = Proposal.builder()
                .title(proposalDTO.getTitle())
                .description(proposalDTO.getDescription())
                .startDate(proposalDTO.getStartDate())
                .endDate(proposalDTO.getEndDate())
                .isActive(true)
                .build();

        Proposal savedProposal = proposalRepository.save(proposal);
        log.info("Propuesta creada exitosamente con ID: {}", savedProposal.getId());
        return convertToResponseDTO(savedProposal);
    }

    public Proposal updateProposal(Long id, ProposalDTO proposalDTO) {
        log.debug("Actualizando propuesta con ID: {}", id);
        
        Proposal existingProposal = proposalRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propuesta no encontrada con ID: " + id));
        
        // Verificar si la propuesta ya tiene votos
        Integer totalVotes = voteRepository.countByProposalId(id);
        if (totalVotes > 0) {
            throw new BusinessException("No se puede modificar una propuesta que ya tiene votos");
        }
        
        validateProposalDates(proposalDTO.getStartDate(), proposalDTO.getEndDate());
        
        existingProposal.setTitle(proposalDTO.getTitle());
        existingProposal.setDescription(proposalDTO.getDescription());
        existingProposal.setStartDate(proposalDTO.getStartDate());
        existingProposal.setEndDate(proposalDTO.getEndDate());
        
        Proposal updatedProposal = proposalRepository.save(existingProposal);
        log.info("Propuesta actualizada exitosamente con ID: {}", id);
        return updatedProposal;
    }

    public void deleteProposal(Long id) {
        log.debug("Eliminando propuesta con ID: {}", id);
        
        Proposal proposal = proposalRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propuesta no encontrada con ID: " + id));
        
        // Baja lógica
        proposal.setIsActive(false);
        proposalRepository.save(proposal);
        
        log.info("Propuesta desactivada exitosamente con ID: {}", id);
    }

    @Transactional(readOnly = true)
    public Integer getTotalVotes(Long proposalId) {
        log.debug("Obteniendo total de votos para propuesta ID: {}", proposalId);
        return proposalRepository.countVotesByProposalId(proposalId);
    }

    private ProposalResponseDTO convertToResponseDTO(Proposal proposal) {
        LocalDateTime now = LocalDateTime.now();
        
        Integer positiveVotes = null;
        Integer negativeVotes = null;
        
        // Solo calcular votos si la propuesta ya finalizó
        if (now.isAfter(proposal.getEndDate())) {
            positiveVotes = voteRepository.countPositiveVotesByProposalId(proposal.getId());
            negativeVotes = voteRepository.countNegativeVotesByProposalId(proposal.getId());
        }
        
        return ProposalResponseDTO.builder()
                .id(proposal.getId())
                .title(proposal.getTitle())
                .description(proposal.getDescription())
                .startDate(proposal.getStartDate())
                .endDate(proposal.getEndDate())
                .positiveVotes(positiveVotes)
                .negativeVotes(negativeVotes)
                .build();
    }

    private void validateProposalDates(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        
        if (startDate.isBefore(now)) {
            throw new BusinessException("La fecha de inicio no puede ser anterior a la fecha actual");
        }
        
        if (endDate.isBefore(startDate)) {
            throw new BusinessException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
        
        if (startDate.isEqual(endDate)) {
            throw new BusinessException("La fecha de inicio y fin no pueden ser iguales");
        }
    }
}