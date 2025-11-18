package utn.frc.piv.parcial.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utn.frc.piv.parcial.dtos.ProposalDTO;
import utn.frc.piv.parcial.dtos.ProposalResponseDTO;
import utn.frc.piv.parcial.dtos.VoteRequestDTO;
import utn.frc.piv.parcial.dtos.VoteResponseDTO;
import utn.frc.piv.parcial.entities.Proposal;
import utn.frc.piv.parcial.entities.Vote;
import utn.frc.piv.parcial.exceptions.ErrorResponse;
import utn.frc.piv.parcial.services.GreetingsClientService;
import utn.frc.piv.parcial.services.ProposalService;
import utn.frc.piv.parcial.services.VoteService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*")
@Tag(name = "Proposals", description = "API para gestión de propuestas y votaciones")
public class ProposalController {

    private final ProposalService proposalService;
    private final VoteService voteService;
    private final GreetingsClientService greetingsClientService;

    @Operation(summary = "Obtener todas las propuestas activas", 
               description = "Devuelve todas las propuestas activas ordenadas por fecha de inicio ascendente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de propuestas obtenida exitosamente",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = ProposalResponseDTO.class)))
    })
    @GetMapping("/proposals")
    public ResponseEntity<List<ProposalResponseDTO>> getAllActiveProposals() {
        List<ProposalResponseDTO> proposals = proposalService.getAllActiveProposals();
        return ResponseEntity.ok(proposals);
    }

    @Operation(summary = "Obtener propuesta por ID", 
               description = "Devuelve una propuesta específica por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Propuesta encontrada",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = ProposalResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Propuesta no encontrada",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/proposals/{id}")
    public ResponseEntity<ProposalResponseDTO> getProposalById(
            @Parameter(description = "ID de la propuesta", required = true)
            @PathVariable Long id) {
        ProposalResponseDTO proposal = proposalService.getProposalById(id);
        return ResponseEntity.ok(proposal);
    }

    @Operation(summary = "Crear nueva propuesta", 
               description = "Crea una nueva propuesta con título, descripción y fechas futuras")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Propuesta creada exitosamente",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = ProposalResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/proposals")
    public ResponseEntity<ProposalResponseDTO> createProposal(
            @Parameter(description = "Datos de la propuesta a crear", required = true)
            @Valid @RequestBody ProposalDTO proposalDTO) {
        ProposalResponseDTO createdProposal = proposalService.createProposal(proposalDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProposal);
    }

    @PutMapping("/proposals/{id}")
    public ResponseEntity<Proposal> updateProposal(@PathVariable Long id, 
                                                   @Valid @RequestBody ProposalDTO proposalDTO) {
        Proposal updatedProposal = proposalService.updateProposal(id, proposalDTO);
        return ResponseEntity.ok(updatedProposal);
    }

    @DeleteMapping("/proposals/{id}")
    public ResponseEntity<Void> deleteProposal(@PathVariable Long id) {
        proposalService.deleteProposal(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/proposals/{id}/votes/count")
    public ResponseEntity<Integer> getTotalVotes(@PathVariable Long id) {
        Integer totalVotes = proposalService.getTotalVotes(id);
        return ResponseEntity.ok(totalVotes);
    }

    @Operation(summary = "Emitir voto en propuesta", 
               description = "Permite a un usuario votar en una propuesta específica. Requiere header x-user-id.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Voto emitido exitosamente",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = VoteResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Error en el voto (usuario ya votó, propuesta no activa, etc.)",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Propuesta no encontrada",
                    content = @Content(mediaType = "application/json", 
                                      schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/proposals/{id}/votes")
    public ResponseEntity<VoteResponseDTO> castVote(
            @Parameter(description = "ID de la propuesta", required = true)
            @PathVariable Long id,
            @Parameter(description = "Datos del voto", required = true)
            @Valid @RequestBody VoteRequestDTO voteRequest,
            @Parameter(description = "ID del usuario que vota", required = true)
            @RequestHeader(name = "x-user-id", required = true) String userIdHeader) {
        Long userId = Long.parseLong(userIdHeader);
        Vote vote = voteService.castVoteWithHeader(id, voteRequest.getVote(), userId);
        
        VoteResponseDTO response = VoteResponseDTO.builder()
                .vote(vote.getVote())
                .proposalId(vote.getProposal().getId())
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obtener saludo del servicio externo",
               description = "Llama al servicio greetings-be y devuelve su respuesta")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Saludo obtenido exitosamente",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/greetings")
    public ResponseEntity<String> getExternalGreeting() {
        String greeting = greetingsClientService.fetchGreeting();
        return ResponseEntity.ok(greeting);
    }
}