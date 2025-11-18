package utn.frc.piv.parcial.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utn.frc.piv.parcial.entities.VoteType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta para un voto creado")
public class VoteResponseDTO {

    @Schema(description = "Tipo de voto emitido", example = "POSITIVE")
    private VoteType vote;
    
    @Schema(description = "ID de la propuesta votada", example = "1")
    private Long proposalId;
}