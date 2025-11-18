package utn.frc.piv.parcial.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta para propuestas")
public class ProposalResponseDTO {

    @Schema(description = "ID único de la propuesta", example = "1")
    private Long id;
    
    @Schema(description = "Título de la propuesta", example = "Hackathon")
    private String title;
    
    @Schema(description = "Descripción de la propuesta", example = "hacemos algo?")
    private String description;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Fecha y hora de inicio", example = "2025-10-01 10:00:00")
    private LocalDateTime startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Fecha y hora de fin", example = "2025-10-15 18:00:00")
    private LocalDateTime endDate;
    
    @Schema(description = "Cantidad de votos positivos (null si la propuesta no ha finalizado)", example = "5", nullable = true)
    private Integer positiveVotes;
    
    @Schema(description = "Cantidad de votos negativos (null si la propuesta no ha finalizado)", example = "2", nullable = true)
    private Integer negativeVotes;
}