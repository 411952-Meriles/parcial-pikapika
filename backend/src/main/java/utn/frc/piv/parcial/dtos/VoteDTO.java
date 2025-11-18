package utn.frc.piv.parcial.dtos;

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
public class VoteDTO {

    private Long id;

    @NotNull(message = "El tipo de voto es obligatorio")
    private VoteType vote;

    @NotNull(message = "El ID de usuario es obligatorio")
    private Long userId;

    @NotNull(message = "El ID de propuesta es obligatorio")
    private Long proposalId;
}