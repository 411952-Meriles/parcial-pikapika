package utn.frc.piv.parcial.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import utn.frc.piv.parcial.entities.VoteType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de request para crear un voto")
public class VoteRequestDTO {

    @NotNull(message = "El tipo de voto es obligatorio")
    @Schema(description = "Tipo de voto", example = "POSITIVE", allowableValues = {"POSITIVE", "NEGATIVE", "ABSTENCY"})
    private VoteType vote;
}